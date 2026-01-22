package com.lavariyalabs.snapy.android.ui.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lavariyalabs.snapy.android.data.FlashcardRepository
import com.lavariyalabs.snapy.android.data.model.UserProgress
import com.lavariyalabs.snapy.android.utils.SM2Algorithm
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * StudyStats - Statistics data class
 */
data class StudyStats(
    val totalCards: Int = 0,
    val newCards: Int = 0,
    val learningCards: Int = 0,
    val reviewCards: Int = 0,
    val matureCards: Int = 0,
    val cardsDueToday: Int = 0,
    val totalReviews: Int = 0,
    val correctReviews: Int = 0,
    val incorrectReviews: Int = 0,
    val retentionRate: Float = 0f,
    val averageEaseFactor: Float = 0f,
    val averageInterval: Int = 0
) {
    val retentionPercentage: Int
        get() = (retentionRate * 100).toInt()
}

/**
 * ProfileViewModel - Manages profile screen state
 *
 * Shows:
 * - User study statistics
 * - Spaced repetition metrics
 * - Progress tracking
 */
class ProfileViewModel(
    private val repository: FlashcardRepository
) : ViewModel() {

    // ========== STATE ==========
    private val _studyStats = mutableStateOf(StudyStats())
    val studyStats: State<StudyStats> = _studyStats

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _cardsStudied = mutableStateOf(0)
    val cardsStudied: State<Int> = _cardsStudied

    // ========== USER ID ==========
    var userId: String = "default_user"

    // ========== METHODS ==========

    /**
     * Load study statistics for the user
     */
    fun loadStudyStats() {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                // Get all user progress
                val allProgress = repository.getAllUserProgress(userId)

                // Get due cards for today
                val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
                val dueCards = repository.getDueCards(userId, today)

                // Get quiz responses
                val totalResponses = repository.getTotalQuizResponses(userId)
                val correctResponses = repository.getQuizResponsesByType(userId, "CORRECT")
                val incorrectResponses = repository.getQuizResponsesByType(userId, "INCORRECT")

                // Calculate statistics
                val stats = calculateStats(
                    allProgress = allProgress,
                    dueCards = dueCards.size,
                    totalResponses = totalResponses,
                    correctResponses = correctResponses,
                    incorrectResponses = incorrectResponses
                )

                _studyStats.value = stats
                _cardsStudied.value = allProgress.size

            } catch (e: Exception) {
                e.printStackTrace()
                _studyStats.value = StudyStats()
                _cardsStudied.value = 0
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Calculate statistics from progress data
     */
    private fun calculateStats(
        allProgress: List<UserProgress>,
        dueCards: Int,
        totalResponses: Int,
        correctResponses: Int,
        incorrectResponses: Int
    ): StudyStats {
        if (allProgress.isEmpty()) {
            return StudyStats(
                totalReviews = totalResponses,
                correctReviews = correctResponses,
                incorrectReviews = incorrectResponses,
                retentionRate = if (totalResponses > 0) correctResponses.toFloat() / totalResponses.toFloat() else 0f
            )
        }

        // Categorize cards
        var newCards = 0
        var learningCards = 0
        var reviewCards = 0
        var matureCards = 0

        var totalEaseFactor = 0f
        var totalInterval = 0

        allProgress.forEach { progress ->
            when {
                SM2Algorithm.isMature(progress) -> matureCards++
                SM2Algorithm.isLearning(progress) -> learningCards++
                else -> reviewCards++
            }

            totalEaseFactor += progress.easeFactor
            totalInterval += progress.interval
        }

        val averageEaseFactor = if (allProgress.isNotEmpty()) {
            totalEaseFactor / allProgress.size
        } else {
            2.5f
        }

        val averageInterval = if (allProgress.isNotEmpty()) {
            totalInterval / allProgress.size
        } else {
            0
        }

        val retentionRate = if (totalResponses > 0) {
            correctResponses.toFloat() / totalResponses.toFloat()
        } else {
            0f
        }

        return StudyStats(
            totalCards = allProgress.size,
            newCards = newCards,
            learningCards = learningCards,
            reviewCards = reviewCards,
            matureCards = matureCards,
            cardsDueToday = dueCards,
            totalReviews = totalResponses,
            correctReviews = correctResponses,
            incorrectReviews = incorrectResponses,
            retentionRate = retentionRate,
            averageEaseFactor = averageEaseFactor,
            averageInterval = averageInterval
        )
    }

    /**
     * Refresh statistics
     */
    fun refresh() {
        loadStudyStats()
    }
}
