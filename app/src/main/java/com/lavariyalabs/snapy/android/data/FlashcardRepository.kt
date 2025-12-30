package com.lavariyalabs.snapy.android.data

import com.lavariyalabs.snapy.android.data.model.*
import com.lavariyalabs.snapy.android.data.remote.SupabaseDataSource

/**
 * FlashcardRepository - Single source of truth for data
 *
 * Abstracts data access
 * Can switch between Supabase and mock data
 */
class FlashcardRepository(
    private val dataSource: SupabaseDataSource
) {

    // ========== GRADES & SUBJECTS ==========
    suspend fun getAllGrades(): List<Grade> = dataSource.getAllGrades()

    suspend fun getSubjectsByGrade(gradeId: Long): List<Subject> =
        dataSource.getSubjectsByGrade(gradeId)

    // ========== TERMS & UNITS ==========
    suspend fun getTermsBySubject(subjectId: Long): List<Term> =
        dataSource.getTermsBySubject(subjectId)

    suspend fun getUnitsByTerm(termId: Long): List<StudyUnit> =
        dataSource.getUnitsByTerm(termId)

    // ========== FLASHCARDS ==========
    suspend fun getFlashcardsByUnit(unitId: Long): List<Flashcard> =
        dataSource.getFlashcardsByUnit(unitId)

    suspend fun getFlashcardById(flashcardId: Long): Flashcard? =
        dataSource.getFlashcardById(flashcardId)

    // ========== PROGRESS & RESPONSES ==========
    suspend fun getUserProgress(userId: String, flashcardId: Long): UserProgress? =
        dataSource.getUserProgress(userId, flashcardId)

    suspend fun saveUserProgress(progress: UserProgress) =
        dataSource.saveUserProgress(progress)

    suspend fun saveQuizResponse(response: QuizResponse) =
        dataSource.saveQuizResponse(response)
}
