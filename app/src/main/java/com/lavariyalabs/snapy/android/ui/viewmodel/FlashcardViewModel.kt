package com.lavariyalabs.snapy.android.ui.viewmodel

// File: com/lavariyalabs/snapy/android/ui/viewmodel/FlashcardViewModel.kt

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import com.lavariyalabs.snapy.android.data.model.Flashcard
import com.lavariyalabs.snapy.android.data.model.QuizSession
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.lavariyalabs.snapy.android.data.FlashcardRepository
import com.lavariyalabs.snapy.android.data.remote.FlashcardRemoteDataSource
/**
 * ViewModel manages all UI state for the flashcard screen.
 *
 * WHY ViewModel?
 * - Survives configuration changes (screen rotation, app minimization)
 * - Single place for state logic
 * - Decouples UI (Composables) from business logic
 * - Lifecycle-aware (automatically cleaned up when not needed)
 *
 * FlashcardViewModel - UI State Management
 *
 * UPDATED WITH SUPABASE:
 * - Fetches data from Supabase instead of static sample data
 * - Uses Repository pattern
 * - Manages loading/error states
 */
class FlashcardViewModel : ViewModel() {

    // ========== DEPENDENCIES ==========
    /**
     * Create instances of data layer
     */
    private val remoteDataSource = FlashcardRemoteDataSource()
    private val repository = FlashcardRepository(remoteDataSource)

    // ============= STATE VARIABLES =============

    /**
     * Quiz session state - tracks user progress
     */
    private val _quizSession = mutableStateOf(QuizSession(totalCards = 0))
    val quizSession: State<QuizSession> = _quizSession

    /**
     * Card flip state - is back side showing?
     */
    private val _currentCardFlipped = mutableStateOf(false)
    val currentCardFlipped: State<Boolean> = _currentCardFlipped

    /**
     * All flashcards from database
     * Initially empty, populated when fetched
     */
    private val _flashcards = mutableStateOf<List<Flashcard>>(emptyList())
    val flashcards: State<List<Flashcard>> = _flashcards

    /**
     * Loading state - show spinner while fetching
     * WHY? User should see feedback while waiting
     */
    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    /**
     * Error message - if something goes wrong
     * WHY? Tell user what failed
     */
    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage

    // ========== INITIALIZATION ==========

    /**
     * loadFlashcards() - Fetch data from Supabase
     *
     * SUSPEND FUNCTION + COROUTINE:
     * - viewModelScope.launch starts a coroutine
     * - Runs on background thread (doesn't block UI)
     * - When done, updates state
     * - State updates trigger recomposition (UI updates)
     *
     * FLOW:
     * 1. Set isLoading = true (show spinner)
     * 2. Call repository.getAllFlashcards() (fetch data)
     * 3. If success: update _flashcards and _quizSession
     * 4. If error: set _errorMessage
     * 5. Set isLoading = false (hide spinner)
     */
    fun loadFlashcards() {
        viewModelScope.launch {
            try {
                // Show loading state
                _isLoading.value = true
                _errorMessage.value = null

                // Fetch from database
                val cards = repository.getAllFlashcards()

                // Update state
                _flashcards.value = cards
                _quizSession.value = QuizSession(totalCards = cards.size)

                // Clear any previous errors
                _errorMessage.value = null
            } catch (e: Exception) {
                // Handle error
                _errorMessage.value = "Failed to load flashcards: ${e.message}"
                _flashcards.value = emptyList()
            } finally {
                // Always hide loading state
                _isLoading.value = false
            }
        }
    }

    /**
     * loadFlashcardsByTopic() - Fetch filtered data
     *
     * @param topic: Topic to filter by
     */
    fun loadFlashcardsByTopic(topic: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null

                val cards = repository.getFlashcardsByTopic(topic)

                _flashcards.value = cards
                _quizSession.value = QuizSession(totalCards = cards.size)
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load flashcards: ${e.message}"
                _flashcards.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ============= METHODS (FUNCTIONS THAT MODIFY STATE) =============

    /**
     * Toggle card flip state (front ↔ back)
     */
    fun toggleCardFlip() {
        _currentCardFlipped.value = !_currentCardFlipped.value
    }

    /**
     * Move to next card and reset flip state
     * Called when user swipes right or taps next
     */
    fun goToNextCard() {
        if (!_quizSession.value.isQuizComplete) {
            _quizSession.value = _quizSession.value.copy(
                currentCardIndex = _quizSession.value.currentCardIndex + 1
            )
            _currentCardFlipped.value = false
        }
    }

    /**
     * Move to previous card
     * Called when user swipes left
     */
    fun goToPreviousCard() {
        if (_quizSession.value.currentCardIndex > 0) {
            _quizSession.value = _quizSession.value.copy(
                currentCardIndex = _quizSession.value.currentCardIndex - 1
            )
            _currentCardFlipped.value = false
        }
    }

    /**
     * Record user's response and move to next card
     * recordAnswer() - User responded to card
     * @param isCorrect true if user clicked "I knew", false if "I didn't know"
     */
    fun recordAnswer(isCorrect: Boolean) {
        val updated = if (isCorrect) {
            _quizSession.value.copy(
                correctAnswers = _quizSession.value.correctAnswers + 1,
                currentCardIndex = _quizSession.value.currentCardIndex + 1
            )
        } else {
            _quizSession.value.copy(
                incorrectAnswers = _quizSession.value.incorrectAnswers + 1,
                currentCardIndex = _quizSession.value.currentCardIndex + 1
            )
        }
        _quizSession.value = updated
        _currentCardFlipped.value = false
    }

    /**
     * getCurrentCard() - Get the card user is viewing now
     *
     * @return Current flashcard, or null if no cards
     */
    fun getCurrentCard(): Flashcard? {
        val index = _quizSession.value.currentCardIndex
        return if (index < _flashcards.value.size) {
            _flashcards.value[index]
        } else {
            null
        }
    }
    /**
     * Reset entire quiz to starting state
     */
    fun resetQuiz() {
        _quizSession.value = QuizSession(totalCards = _flashcards.value.size)
        _currentCardFlipped.value = false
    }
}

/**
 * LEARNING EXPLANATION:
 *
 * viewModelScope.launch:
 * - Creates coroutine (async operation)
 * - Lifecycle-aware (cancels when ViewModel destroyed)
 * - WHY? Network calls take time, can't block UI thread
 *
 * try-catch-finally:
 * - Try: Normal code
 * - Catch: Handle errors
 * - Finally: Always runs (cleanup, hide spinner)
 *
 * State update triggers recomposition:
 * - When _flashcards.value changes
 * - Compose automatically rerenders screen
 * - Shows new data
 *
 * Coroutine safety:
 * - UI updates only happen on main thread
 * - viewModelScope handles this automatically
 * - No need for manual thread management
 */
