package com.lavariyalabs.snapy.android.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.lavariyalabs.snapy.android.data.FlashcardRepository
import com.lavariyalabs.snapy.android.data.remote.SupabaseDataSource

/**
 * ViewModelFactory - Provides ViewModels with dependencies
 *
 * This follows the dependency injection pattern for MVVM architecture
 */
class ViewModelFactory : ViewModelProvider.Factory {
    
    private val dataSource = SupabaseDataSource()
    private val repository = FlashcardRepository(dataSource)
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AppStateViewModel::class.java) -> {
                AppStateViewModel(repository) as T
            }
            modelClass.isAssignableFrom(FlashcardViewModel::class.java) -> {
                FlashcardViewModel(repository) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(repository) as T
            }
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(repository) as T
            }
            modelClass.isAssignableFrom(OnboardingViewModel::class.java) -> {
                OnboardingViewModel(repository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
