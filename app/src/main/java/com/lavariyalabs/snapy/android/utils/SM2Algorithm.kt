package com.lavariyalabs.snapy.android.utils

import com.lavariyalabs.snapy.android.data.model.UserProgress
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * SM2Algorithm - SuperMemo 2 Spaced Repetition Algorithm
 *
 * Calculates optimal review intervals based on user performance.
 *
 * Quality Scale (0-5):
 * - 5: Perfect recall (Easy)
 * - 4: Correct with hesitation (Medium)
 * - 3: Correct with difficulty (Hard)
 * - 0-2: Incorrect (Failed)
 *
 * Reference: https://super-memory.com/english/ol/sm2.htm
 */
object SM2Algorithm {

    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    /**
     * Calculate new progress based on user's quality of recall
     *
     * @param currentProgress Current user progress (or null for new card)
     * @param quality Quality of recall (0-5)
     * @param userId User identifier
     * @param flashcardId Flashcard identifier
     * @return Updated UserProgress
     */
    fun calculateNewProgress(
        currentProgress: UserProgress?,
        quality: Int,
        userId: String,
        flashcardId: Long
    ): UserProgress {
        require(quality in 0..5) { "Quality must be between 0 and 5" }

        val today = LocalDate.now()

        // If no existing progress, create initial progress
        if (currentProgress == null) {
            return createInitialProgress(
                userId = userId,
                flashcardId = flashcardId,
                quality = quality,
                today = today
            )
        }

        // Calculate new ease factor
        val newEaseFactor = calculateEaseFactor(currentProgress.easeFactor, quality)

        // Determine if recall was successful
        val successful = quality >= 3

        // Calculate new interval and repetitions
        val (newInterval, newRepetitions) = if (successful) {
            calculateSuccessfulInterval(
                currentInterval = currentProgress.interval,
                currentRepetitions = currentProgress.repetitions,
                easeFactor = newEaseFactor
            )
        } else {
            // Failed: reset progress
            Pair(1, 0)
        }

        // Calculate next review date
        val nextReviewDate = today.plusDays(newInterval.toLong())

        // Update review counts
        val newTotalReviews = currentProgress.totalReviews + 1
        val newCorrectReviews = if (successful) currentProgress.correctReviews + 1 else currentProgress.correctReviews
        val newIncorrectReviews = if (!successful) currentProgress.incorrectReviews + 1 else currentProgress.incorrectReviews

        return currentProgress.copy(
            easeFactor = newEaseFactor,
            interval = newInterval,
            repetitions = newRepetitions,
            nextReviewDate = nextReviewDate.format(dateFormatter),
            lastReviewedAt = today.format(dateFormatter),
            totalReviews = newTotalReviews,
            correctReviews = newCorrectReviews,
            incorrectReviews = newIncorrectReviews,
            updatedAt = today.format(dateFormatter)
        )
    }

    /**
     * Create initial progress for a new card
     */
    private fun createInitialProgress(
        userId: String,
        flashcardId: Long,
        quality: Int,
        today: LocalDate
    ): UserProgress {
        val successful = quality >= 3
        val easeFactor = calculateEaseFactor(2.5f, quality)
        val interval = if (successful) 1 else 1
        val repetitions = if (successful) 1 else 0
        val nextReviewDate = today.plusDays(interval.toLong())

        return UserProgress(
            id = 0, // Will be set by database
            userId = userId,
            flashcardId = flashcardId,
            totalReviews = 1,
            correctReviews = if (successful) 1 else 0,
            incorrectReviews = if (!successful) 1 else 0,
            easeFactor = easeFactor,
            interval = interval,
            repetitions = repetitions,
            nextReviewDate = nextReviewDate.format(dateFormatter),
            lastReviewedAt = today.format(dateFormatter),
            createdAt = today.format(dateFormatter),
            updatedAt = today.format(dateFormatter)
        )
    }

    /**
     * Calculate new ease factor based on quality of recall
     *
     * Formula: EF' = EF + (0.1 - (5 - q) * (0.08 + (5 - q) * 0.02))
     * Minimum EF: 1.3
     */
    private fun calculateEaseFactor(currentEF: Float, quality: Int): Float {
        val adjustment = 0.1f - (5 - quality) * (0.08f + (5 - quality) * 0.02f)
        val newEF = currentEF + adjustment
        return max(1.3f, newEF)
    }

    /**
     * Calculate interval for successful recall
     *
     * - First repetition: 1 day
     * - Second repetition: 6 days
     * - Subsequent: previous interval * ease factor
     */
    private fun calculateSuccessfulInterval(
        currentInterval: Int,
        currentRepetitions: Int,
        easeFactor: Float
    ): Pair<Int, Int> {
        return when (currentRepetitions) {
            0 -> Pair(1, 1)        // First successful review: 1 day
            1 -> Pair(6, 2)        // Second successful review: 6 days
            else -> {
                // Subsequent reviews: multiply by ease factor
                val newInterval = (currentInterval * easeFactor).roundToInt()
                Pair(newInterval, currentRepetitions + 1)
            }
        }
    }

    /**
     * Convert difficulty string to quality score (0-5)
     *
     * @param difficulty "EASY", "MEDIUM", or "HARD"
     * @param isCorrect Whether the answer was correct
     * @return Quality score (0-5)
     */
    fun difficultyToQuality(difficulty: String, isCorrect: Boolean): Int {
        return if (!isCorrect) {
            0 // Failed
        } else {
            when (difficulty.uppercase()) {
                "EASY" -> 5      // Perfect recall
                "MEDIUM" -> 4    // Correct with some thought
                "HARD" -> 3      // Correct with difficulty
                else -> 4        // Default to medium
            }
        }
    }

    /**
     * Check if a card is due for review
     *
     * @param progress User progress
     * @param today Today's date
     * @return True if card is due
     */
    fun isDue(progress: UserProgress?, today: LocalDate = LocalDate.now()): Boolean {
        if (progress == null) return true // New cards are always due

        val nextReviewDate = progress.nextReviewDate ?: return true
        val reviewDate = LocalDate.parse(nextReviewDate, dateFormatter)

        return !reviewDate.isAfter(today)
    }

    /**
     * Calculate retention rate for a user
     *
     * @param totalReviews Total number of reviews
     * @param correctReviews Number of correct reviews
     * @return Retention rate (0.0 to 1.0)
     */
    fun calculateRetentionRate(totalReviews: Int, correctReviews: Int): Float {
        if (totalReviews == 0) return 0f
        return correctReviews.toFloat() / totalReviews.toFloat()
    }

    /**
     * Determine if a card is "mature"
     * A card is mature if it has interval >= 21 days and repetitions >= 3
     */
    fun isMature(progress: UserProgress): Boolean {
        return progress.interval >= 21 && progress.repetitions >= 3
    }

    /**
     * Determine if a card is in "learning" phase
     * A card is learning if interval < 7 days or repetitions < 2
     */
    fun isLearning(progress: UserProgress): Boolean {
        return progress.interval < 7 || progress.repetitions < 2
    }

    /**
     * Get review status text
     */
    fun getReviewStatus(progress: UserProgress?): String {
        if (progress == null) return "New"

        return when {
            isMature(progress) -> "Mature"
            isLearning(progress) -> "Learning"
            else -> "Review"
        }
    }
}
