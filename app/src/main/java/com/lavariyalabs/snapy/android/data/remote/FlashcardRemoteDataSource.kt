package com.lavariyalabs.snapy.android.data.remote

import com.lavariyalabs.snapy.android.config.SupabaseConfig
import com.lavariyalabs.snapy.android.data.model.Flashcard
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * FlashcardRemoteDataSource - Fetches flashcard data from Supabase
 *
 * SINGLE RESPONSIBILITY: Only talk to Supabase
 */

class FlashcardRemoteDataSource {

    /**
     * fetchAllFlashcards() - Get all flashcards from database
     *
     * HOW IT WORKS:
     * 1. Uses Supabase Postgrest module
     * 2. Queries "flashcards" table
     * 3. Converts database rows to Flashcard objects
     * 4. Returns list to caller
     *
     * SUSPEND FUNCTION:
     * - Marked with 'suspend' keyword
     * - Can only be called from coroutine context
     * - Allows waiting for network response without blocking
     */
    suspend fun fetchAllFlashcards(): List<Flashcard> {
        return try {
            // Execute query to Supabase
            val response = SupabaseConfig.supabaseClient
                .from("flashcards")  // Table name
                .select()            // Get all columns
                .decodeList<FlashcardDto>()  // Convert to DTO objects

            // Convert DTO to Flashcard (map transformation)
            response.map { it.toFlashcard() }
        } catch (e: Exception) {
            // Handle errors - for now, print and return empty list
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * fetchFlashcardsByTopic() - Get flashcards by specific topic
     *
     * @param topic: The topic to filter by (e.g., "Biology")
     * @return List of flashcards matching that topic
     *
     * This uses filtering on the database side
     */
    suspend fun fetchFlashcardsByTopic(topic: String): List<Flashcard> {
        return try {
            val response = SupabaseConfig.supabaseClient
                .from("flashcards")
                .select()
                //.eq("topic", topic)  // WHERE topic = 'Biology'
                .decodeList<FlashcardDto>()

            response.map { it.toFlashcard() }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}

/**
 * FlashcardDto - Data Transfer Object
 *
 * WHY different from Flashcard model?
 * - Database tables have different structure than app models
 * - DTO matches exactly what Supabase returns
 * - Flashcard is what app uses internally
 * - This separation is clean architecture
 *
 * @Serializable: Makes this class JSON-serializable
 * @SerialName: Maps Kotlin property to database column name
 */
@Serializable
data class FlashcardDto(
    val id: Long,
    val question: String,
    val answer: String,
    val topic: String,
    val difficulty: String,
    @SerialName("created_at")
    val createdAt: String
) {
    /**
     * Convert DTO to Flashcard model
     * This is where mapping happens
     */
    fun toFlashcard(): Flashcard {
        return Flashcard(
            id = id.toInt(),
            question = question,
            answer = answer,
            topic = topic,
            difficulty = difficulty
        )
    }
}