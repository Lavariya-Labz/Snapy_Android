package com.lavariyalabs.snapy.android.data.remote

import com.lavariyalabs.snapy.android.config.SupabaseConfig
import com.lavariyalabs.snapy.android.data.model.*
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order


/**
 * SupabaseDataSource - Handles all API calls to Supabase
 *
 * Methods for fetching:
 * - Grades & Subjects
 * - Terms & Units
 * - Flashcards & Options
 * - User Progress & Responses
 */
class SupabaseDataSource {

    // ========== GRADES & SUBJECTS ==========

    /**
     * Get all grades
     */
    suspend fun getAllGrades(): List<Grade> {
        return try {
            SupabaseConfig.supabaseClient
                .from("grades")
                .select()
                .decodeList<Grade>()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * Get subjects by grade
     */
    suspend fun getSubjectsByGrade(gradeId: Long): List<Subject> {
        return try {
            SupabaseConfig.supabaseClient
                .from("subjects")
                .select {
                    filter {
                        eq("grade_id", gradeId)
                    }
                }
                .decodeList<Subject>()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
    // ========== TERMS & UNITS ==========

    /**
     * Get terms by subject
     */
    suspend fun getTermsBySubject(subjectId: Long): List<Term> {
        return try {
            SupabaseConfig.supabaseClient
                .from("terms")
                .select {
                    filter {
                        eq("subject_id", subjectId)
                    }
                    order("term_number", Order.ASCENDING)
                }
                .decodeList<Term>()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * Get units by term
     */
    suspend fun getUnitsByTerm(termId: Long): List<StudyUnit> {
        return try {
            SupabaseConfig.supabaseClient
                .from("units")
                .select {
                    filter {
                        eq("term_id", termId)
                    }
                    order("order_index", Order.ASCENDING)
                }
                .decodeList<StudyUnit>()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }


    // ========== FLASHCARDS & OPTIONS ==========

    /**
     * Get flashcards by unit with options
     */
    suspend fun getFlashcardsByUnit(unitId: Long): List<Flashcard> {
        return try {
            val flashcards = SupabaseConfig.supabaseClient
                .from("flashcards")
                .select {
                    filter {
                        eq("unit_id", unitId)
                    }
                    order("order_index", Order.ASCENDING)
                }
                .decodeList<Flashcard>()

            // For MCQ flashcards, fetch quiz options
            flashcards.map { card ->
                if (card.type == "MCQ") {
                    val options = getQuizOptionsByFlashcard(card.id)
                    card.copy(quizOptions = options)
                } else {
                    card
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * Get quiz options for a flashcard
     */
    private suspend fun getQuizOptionsByFlashcard(flashcardId: Long): List<QuizOption> {
        return try {
            SupabaseConfig.supabaseClient
                .from("quiz_options")
                .select {
                    filter {
                        eq("flashcard_id", flashcardId)
                    }
                    // FIX: Use Order.ASCENDING
                    order("order_index", Order.ASCENDING)
                }
                .decodeList<QuizOption>()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * Get single flashcard by ID
     */
    suspend fun getFlashcardById(flashcardId: Long): Flashcard? {
        return try {
            val card = SupabaseConfig.supabaseClient
                .from("flashcards")
                .select {
                    filter {
                        eq("id", flashcardId)
                    }
                }
                .decodeSingle<Flashcard>()

            if (card.type == "MCQ") {
                val options = getQuizOptionsByFlashcard(card.id)
                card.copy(quizOptions = options)
            } else {
                card
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // ========== USER PROGRESS ==========

    /**
     * Get or create user progress
     */
    suspend fun getUserProgress(
        userId: String,
        flashcardId: Long
    ): UserProgress? {
        return try {
            SupabaseConfig.supabaseClient
                .from("user_progress")
                .select {
                    filter {
                        eq("user_id", userId)
                        eq("flashcard_id", flashcardId)
                    }
                }
                .decodeSingle<UserProgress>()
        } catch (e: Exception) {
            null  // Will create new if doesn't exist
        }
    }

    /**
     * Save user progress
     */
    suspend fun saveUserProgress(progress: UserProgress) {
        try {
            SupabaseConfig.supabaseClient
                .from("user_progress")
                .upsert(progress)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // ========== QUIZ RESPONSES ==========

    /**
     * Save quiz response
     */
    suspend fun saveQuizResponse(response: QuizResponse) {
        try {
            SupabaseConfig.supabaseClient
                .from("quiz_responses")
                .insert(response)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // ========== STATISTICS & ANALYTICS ==========

    /**
     * Get all user progress for a user
     */
    suspend fun getAllUserProgress(userId: String): List<UserProgress> {
        return try {
            SupabaseConfig.supabaseClient
                .from("user_progress")
                .select {
                    filter {
                        eq("user_id", userId)
                    }
                }
                .decodeList<UserProgress>()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * Get cards due for review today
     */
    suspend fun getDueCards(userId: String, today: String): List<UserProgress> {
        return try {
            SupabaseConfig.supabaseClient
                .from("user_progress")
                .select {
                    filter {
                        eq("user_id", userId)
                        lte("next_review_date", today)
                    }
                }
                .decodeList<UserProgress>()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * Get total quiz responses for a user
     */
    suspend fun getTotalQuizResponses(userId: String): Int {
        return try {
            val responses = SupabaseConfig.supabaseClient
                .from("quiz_responses")
                .select {
                    filter {
                        eq("user_id", userId)
                    }
                }
                .decodeList<QuizResponse>()
            responses.size
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }

    /**
     * Get quiz responses count by type
     */
    suspend fun getQuizResponsesByType(userId: String, responseType: String): Int {
        return try {
            val responses = SupabaseConfig.supabaseClient
                .from("quiz_responses")
                .select {
                    filter {
                        eq("user_id", userId)
                        eq("response_type", responseType)
                    }
                }
                .decodeList<QuizResponse>()
            responses.size
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }
}
