package com.lavariyalabs.snapy.android.data.model

/**
 * Data class representing a single flashcard.
 */
data class Flashcard(
    val id: Int,                    // Unique identifier for the card
    val question: String,           // Front side of the card
    val answer: String,             // Back side of the card
    val topic: String,              // Category (e.g., "Biology", "History")
    val difficulty: String = "MEDIUM" // Card difficulty level
)

/**
 * Data class for the quiz session state.
 * Tracks user's progress through cards and their performance.
 */
data class QuizSession(
    val totalCards: Int,            // Total cards in this session
    val currentCardIndex: Int = 0,  // Which card user is viewing (0-indexed)
    val correctAnswers: Int = 0,    // How many "I knew" responses
    val incorrectAnswers: Int = 0   // How many "I didn't know" responses
) {
    /**
     * Computed property: Progress percentage (0-100)
     */
    val progressPercent: Float
        get() = if (totalCards == 0) 0f else (currentCardIndex.toFloat() / totalCards) * 100f

    /**
     * Computed property: Check if quiz is finished
     */
    val isQuizComplete: Boolean
        get() = currentCardIndex >= totalCards
}
