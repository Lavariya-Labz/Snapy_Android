package com.lavariyalabs.snapy.android.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import kotlinx.coroutines.launch
import com.lavariyalabs.snapy.android.data.FlashcardRepository
import com.lavariyalabs.snapy.android.data.model.Subject
import com.lavariyalabs.snapy.android.data.model.Term
import com.lavariyalabs.snapy.android.data.model.StudyUnit

/**
 * HomeViewModel - Manages home screen state
 *
 * Handles:
 * - Loading subjects, terms, and units
 * - Managing dropdown selections
 * - UI state for home screen
 */
class HomeViewModel(
    private val repository: FlashcardRepository
) : ViewModel() {

    // ========== UI STATE ==========
    private val _subjects = mutableStateOf<List<Subject>>(emptyList())
    val subjects: State<List<Subject>> = _subjects

    private val _terms = mutableStateOf<List<Term>>(emptyList())
    val terms: State<List<Term>> = _terms

    private val _units = mutableStateOf<List<StudyUnit>>(emptyList())
    val units: State<List<StudyUnit>> = _units

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage

    // ========== METHODS ==========

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
     * Load terms for a subject
     */
    fun loadTermsForSubject(subjectId: Long) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                _terms.value = emptyList()
                _units.value = emptyList() // Clear units until term is selected

                val termsList = repository.getTermsBySubject(subjectId)
                _terms.value = termsList
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load terms: ${e.message}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Load units for a term
     */
    fun loadUnitsForTerm(termId: Long) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                _units.value = emptyList()

                val unitsList = repository.getUnitsByTerm(termId)
                _units.value = unitsList
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load units: ${e.message}"
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
