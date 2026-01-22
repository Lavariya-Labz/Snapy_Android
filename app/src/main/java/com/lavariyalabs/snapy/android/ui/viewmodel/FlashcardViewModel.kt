// File: com/lavariyalabs/snapy/android/ui/viewmodel/FlashcardViewModel.kt

package com.lavariyalabs.snapy.android.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import kotlinx.coroutines.launch
import com.lavariyalabs.snapy.android.data.FlashcardRepository
import com.lavariyalabs.snapy.android.data.model.*
import com.lavariyalabs.snapy.android.utils.SM2Algorithm
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * FlashcardViewModel - Manages flashcard UI state
 *
 * Handles:
 * - Loading flashcards from Supabase
 * - Managing card flip state
 * - Tracking MCQ answers
 * - Recording progress
 */
class FlashcardViewModel(
    private val repository: FlashcardRepository
) : ViewModel() {

    // ========== USER ID ==========
    var userId: String = "default_user" // Will be set from AppStateViewModel

    // ========== UI STATE ==========
    private val _quizSession = mutableStateOf(QuizSession(totalCards = 0))
    val quizSession: State<QuizSession> = _quizSession

    private val _currentCardFlipped = mutableStateOf(false)
    val currentCardFlipped: State<Boolean> = _currentCardFlipped

    private val _flashcards = mutableStateOf<List<Flashcard>>(emptyList())
    val flashcards: State<List<Flashcard>> = _flashcards

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage

    // ========== MCQ STATE ==========
    private val _isAnswered = mutableStateOf(false)
    val isAnswered: State<Boolean> = _isAnswered

    private val _selectedOptionLetter = mutableStateOf<String?>(null)
    val selectedOptionLetter: State<String?> = _selectedOptionLetter

    private val _isSelectedCorrect = mutableStateOf(false)
    val isSelectedCorrect: State<Boolean> = _isSelectedCorrect

    // ========== METHODS ==========

    /**
     * Load flashcards from Supabase for a unit
     */
    fun loadFlashcardsByUnit(unitId: Long) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null

                val cards = repository.getFlashcardsByUnit(unitId)

                _flashcards.value = cards
                _quizSession.value = QuizSession(totalCards = cards.size)
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load flashcards: ${e.message}"
                _flashcards.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ========== SELF_EVAL METHODS ==========

    fun toggleCardFlip() {
        _currentCardFlipped.value = !_currentCardFlipped.value
    }

    // ========== MCQ METHODS ==========

    fun submitMCQAnswer(optionLetter: String, isCorrect: Boolean) {
        _selectedOptionLetter.value = optionLetter
        _isSelectedCorrect.value = isCorrect
        _isAnswered.value = true

        recordAnswer(isCorrect = isCorrect)
    }

    fun goToNextCardMCQ() {
        if (!_quizSession.value.isQuizComplete) {
            _quizSession.value = _quizSession.value.copy(
                currentCardIndex = _quizSession.value.currentCardIndex + 1
            )
            resetMCQState()
        }
    }

    private fun resetMCQState() {
        _isAnswered.value = false
        _selectedOptionLetter.value = null
        _isSelectedCorrect.value = false
    }

    // ========== SHARED METHODS ==========

    fun recordAnswer(isCorrect: Boolean) {
        val updated = if (isCorrect) {
            _quizSession.value.copy(
                correctAnswers = _quizSession.value.correctAnswers + 1
            )
        } else {
            _quizSession.value.copy(
                incorrectAnswers = _quizSession.value.incorrectAnswers + 1
            )
        }
        _quizSession.value = updated
    }

    fun getCurrentCard(): Flashcard? {
        val index = _quizSession.value.currentCardIndex
        return if (index < _flashcards.value.size) {
            _flashcards.value[index]
        } else {
            null
        }
    }

    fun goToNextCard() {
        if (!_quizSession.value.isQuizComplete) {
            _quizSession.value = _quizSession.value.copy(
                currentCardIndex = _quizSession.value.currentCardIndex + 1
            )
            _currentCardFlipped.value = false
            resetMCQState()
        }
    }

    fun resetQuiz() {
        _quizSession.value = QuizSession(totalCards = _flashcards.value.size)
        _currentCardFlipped.value = false
        resetMCQState()
    }

    // ========== SPACED REPETITION (SM2) ==========

    /**
     * Record answer with difficulty and update SM2 progress
     *
     * @param isCorrect Whether answer was correct
     * @param difficulty "EASY", "MEDIUM", or "HARD"
     */
    fun recordAnswerWithDifficulty(isCorrect: Boolean, difficulty: String = "MEDIUM") {
        val currentCard = getCurrentCard() ?: return

        viewModelScope.launch {
            try {
                // Convert difficulty to quality score (0-5)
                val quality = SM2Algorithm.difficultyToQuality(difficulty, isCorrect)

                // Get current progress
                val currentProgress = repository.getUserProgress(userId, currentCard.id)

                // Calculate new progress using SM2
                val newProgress = SM2Algorithm.calculateNewProgress(
                    currentProgress = currentProgress,
                    quality = quality,
                    userId = userId,
                    flashcardId = currentCard.id
                )

                // Save progress to database
                repository.saveUserProgress(newProgress)

                // Save quiz response
                val response = QuizResponse(
                    id = 0, // Will be set by database
                    userId = userId,
                    flashcardId = currentCard.id,
                    selectedOptionId = null,
                    responseType = if (isCorrect) "CORRECT" else "INCORRECT",
                    timeTakenSeconds = null,
                    respondedAt = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                )
                repository.saveQuizResponse(response)

            } catch (e: Exception) {
                e.printStackTrace()
                // Continue even if saving fails
            }
        }
    }

    /**
     * Record answer for SELF_EVAL cards with difficulty
     */
    fun recordSelfEvalAnswer(knew: Boolean, difficulty: String = "MEDIUM") {
        recordAnswer(isCorrect = knew)
        recordAnswerWithDifficulty(isCorrect = knew, difficulty = difficulty)
    }

    /**
     * Record answer for MCQ cards
     */
    fun recordMCQAnswer(isCorrect: Boolean) {
        recordAnswer(isCorrect = isCorrect)
        // MCQ is always considered MEDIUM difficulty
        recordAnswerWithDifficulty(isCorrect = isCorrect, difficulty = "MEDIUM")
    }
}
