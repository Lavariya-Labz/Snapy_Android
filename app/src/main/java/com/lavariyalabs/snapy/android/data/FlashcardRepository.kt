package com.lavariyalabs.snapy.android.data

import com.lavariyalabs.snapy.android.data.model.Flashcard
import com.lavariyalabs.snapy.android.data.remote.FlashcardRemoteDataSource

/**
 * FlashcardRepository - Single source of truth for flashcard data
 *
 * WHY Repository pattern?
 * - ViewModel doesn't know WHERE data comes from
 * - Could be database, API, local cache, etc.
 * - Easy to swap data sources
 * - Clean separation of concerns
 *
 * DEPENDENCY INJECTION:
 * - Repository receives dataSource in constructor
 * - Makes testing easier (can inject mock data source)
 */

class FlashcardRepository(
    private val remoteDataSource: FlashcardRemoteDataSource
) {

    /**
     * getAllFlashcards() - Get all flashcards from data source
     *
     * Currently just delegates to remote source
     * In future, could add:
     * - Local caching (Room database)
     * - Offline support
     * - Data transformation
     */
    suspend fun getAllFlashcards(): List<Flashcard> {
        return remoteDataSource.fetchAllFlashcards()
    }

    /**
     * getFlashcardsByTopic() - Get filtered flashcards
     *
     * @param topic: Filter by this topic
     * @return Filtered list
     */
    suspend fun getFlashcardsByTopic(topic: String): List<Flashcard> {
        return remoteDataSource.fetchFlashcardsByTopic(topic)
    }
}
