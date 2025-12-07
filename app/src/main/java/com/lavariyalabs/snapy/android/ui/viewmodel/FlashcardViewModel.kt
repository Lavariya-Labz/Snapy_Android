package com.lavariyalabs.snapy.android.ui.viewmodel

// File: com/lavariyalabs/snapy/android/ui/viewmodel/FlashcardViewModel.kt

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import com.lavariyalabs.snapy.android.data.model.Flashcard
import com.lavariyalabs.snapy.android.data.model.QuizSession

/**
 * ViewModel manages all UI state for the flashcard screen.
 *
 * WHY ViewModel?
 * - Survives configuration changes (screen rotation, app minimization)
 * - Single place for state logic
 * - Decouples UI (Composables) from business logic
 * - Lifecycle-aware (automatically cleaned up when not needed)
 */
class FlashcardViewModel : ViewModel() {

    // ============= STATE VARIABLES =============

    /**
     * Backing property: Private, mutable
     * Public property: Exposed as read-only State
     *
     * WHY this pattern?
     * - Prevents external code from accidentally modifying state
     * - Only ViewModel can modify data
     * - UI only reads, doesn't write directly
     */
    private val _quizSession = mutableStateOf(QuizSession(totalCards = 10))
    val quizSession: State<QuizSession> = _quizSession

    private val _currentCardFlipped = mutableStateOf(false)
    val currentCardFlipped: State<Boolean> = _currentCardFlipped

    private val _cardFlipAnimation = mutableStateOf(0f)  // 0f = front, 1f = back
    val cardFlipAnimation: State<Float> = _cardFlipAnimation


    /**
     * Sample flashcards data for testing UI
     */
    private val sampleFlashcards = listOf(
        Flashcard(
            id = 1,
            question = "What is the process by which plants make their own food?",
            answer = "Photosynthesis is the process where plants use sunlight, water, and carbon dioxide to create glucose (sugar) and oxygen. It occurs mainly in the leaves.",
            topic = "Biology",
            difficulty = "EASY"
        ),
        Flashcard(
            id = 2,
            question = "What is the capital of France?",
            answer = "Paris is the capital city of France, located in the north-central part of the country along the Seine River.",
            topic = "Geography",
            difficulty = "EASY"
        ),
        Flashcard(
            id = 3,
            question = "Define mitochondria and its function.",
            answer = "Mitochondria are oval-shaped organelles in cells that generate energy in the form of ATP through cellular respiration. Often called the 'powerhouse of the cell'.",
            topic = "Biology",
            difficulty = "MEDIUM"
        ),
        Flashcard(
            id = 4,
            question = "What is the formula for photosynthesis?",
            answer = "6CO₂ + 6H₂O + light energy → C₆H₁₂O₆ + 6O₂\n\nCarbon dioxide + Water + Light → Glucose + Oxygen",
            topic = "Biology",
            difficulty = "HARD"
        ),
        Flashcard(
            id = 5,
            question = "What does DNA stand for?",
            answer = "DNA stands for Deoxyribonucleic Acid. It is the molecule that carries genetic instructions for life in all known organisms.",
            topic = "Biology",
            difficulty = "MEDIUM"
        ),
        Flashcard(
            id = 6,
            question = "Name the 5 main components of blood.",
            answer = "1. Red Blood Cells (oxygen transport)\n2. White Blood Cells (immunity)\n3. Platelets (clotting)\n4. Plasma (liquid medium)\n5. Antibodies (disease fighting)",
            topic = "Biology",
            difficulty = "MEDIUM"
        ),
        Flashcard(
            id = 7,
            question = "What is the definition of evolution?",
            answer = "Evolution is the process of change in all forms of life over time. It occurs through natural selection, where organisms with beneficial traits are more likely to survive and reproduce.",
            topic = "Biology",
            difficulty = "HARD"
        ),
        Flashcard(
            id = 8,
            question = "What is the difference between prokaryotes and eukaryotes?",
            answer = "Prokaryotes (bacteria, archaea) lack a nucleus and membrane-bound organelles. Eukaryotes (animals, plants, fungi) have a nucleus containing DNA and specialized organelles.",
            topic = "Biology",
            difficulty = "HARD"
        ),
        Flashcard(
            id = 9,
            question = "What is the role of enzymes in cells?",
            answer = "Enzymes are protein catalysts that speed up chemical reactions in cells without being consumed. They lower activation energy needed for reactions to occur.",
            topic = "Biology",
            difficulty = "MEDIUM"
        ),
        Flashcard(
            id = 10,
            question = "Define osmosis and give an example.",
            answer = "Osmosis is the movement of water across a semipermeable membrane from an area of low solute concentration to high solute concentration. Example: A raisin absorbs water and swells when placed in pure water.",
            topic = "Biology",
            difficulty = "MEDIUM"
        )
    )

    // ============= METHODS (FUNCTIONS THAT MODIFY STATE) =============

    /**
     * Initialize quiz with all sample flashcards
     * Called when screen first loads
     */
    fun initializeQuiz() {
        _quizSession.value = QuizSession(totalCards = sampleFlashcards.size)
    }

    /**
     * Toggle card flip state (front ↔ back)
     */
    fun toggleCardFlip() {
        _currentCardFlipped.value = !_currentCardFlipped.value
        // In future, we can add animation here
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
            _currentCardFlipped.value = false  // Reset to show question again
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
     *
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
     * Get currently displayed flashcard
     */
    fun getCurrentCard(): Flashcard? {
        val index = _quizSession.value.currentCardIndex
        return if (index < sampleFlashcards.size) sampleFlashcards[index] else null
    }

    /**
     * Reset entire quiz to starting state
     */
    fun resetQuiz() {
        _quizSession.value = QuizSession(totalCards = sampleFlashcards.size)
        _currentCardFlipped.value = false
    }
}
