package com.lavariyalabs.snapy.android.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import kotlinx.coroutines.launch
import com.lavariyalabs.snapy.android.data.FlashcardRepository
import com.lavariyalabs.snapy.android.data.model.Grade
import com.lavariyalabs.snapy.android.data.model.Subject

/**
 * OnboardingViewModel - Manages onboarding flow state
 *
 * Handles:
 * - Loading grades and subjects
 * - Managing onboarding selections
 * - UI state for onboarding screens
 */
class OnboardingViewModel(
    private val repository: FlashcardRepository
) : ViewModel() {

    // ========== UI STATE ==========
    private val _grades = mutableStateOf<List<Grade>>(emptyList())
    val grades: State<List<Grade>> = _grades

    private val _subjects = mutableStateOf<List<Subject>>(emptyList())
    val subjects: State<List<Subject>> = _subjects

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage

    // ========== METHODS ==========

    /**
     * Load all grades from repository
     */
    fun loadGrades() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                val gradesList = repository.getAllGrades()
                _grades.value = gradesList
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load grades: ${e.message}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Load subjects for a grade
     */
    fun loadSubjectsForGrade(gradeId: Long) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                _subjects.value = emptyList()
                val subjectsList = repository.getSubjectsByGrade(gradeId)
                _subjects.value = subjectsList
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load subjects: ${e.message}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Clear error message
     */
    fun clearError() {
        _errorMessage.value = null
    }
}
