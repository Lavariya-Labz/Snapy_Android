package com.lavariyalabs.snapy.android.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.lavariyalabs.snapy.android.data.FlashcardRepository

/**
 * ProfileViewModel - Manages profile screen state
 *
 * Currently displays user information from AppStateViewModel
 * Can be extended for profile-specific features like statistics
 */
class ProfileViewModel(
    private val repository: FlashcardRepository
) : ViewModel() {

    // ========== UI STATE ==========
    private val _cardsStudied = mutableStateOf(0)
    val cardsStudied: State<Int> = _cardsStudied

    // Future: Add more profile statistics here
    // - Total study time
    // - Streak days
    // - Mastered cards
    // etc.
}
