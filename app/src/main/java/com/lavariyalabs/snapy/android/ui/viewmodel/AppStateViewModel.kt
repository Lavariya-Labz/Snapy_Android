package com.lavariyalabs.snapy.android.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import kotlinx.coroutines.launch
import com.lavariyalabs.snapy.android.data.FlashcardRepository
import com.lavariyalabs.snapy.android.data.model.Grade
import com.lavariyalabs.snapy.android.data.model.Subject
import com.lavariyalabs.snapy.android.data.model.Term
import com.lavariyalabs.snapy.android.data.model.StudyUnit
import com.lavariyalabs.snapy.android.data.remote.SupabaseDataSource

/**
 * AppStateViewModel - Shared app state
 *
 * Manages:
 * - User selections (grade, subject, term, language)
 * - Lists of data (grades, subjects, terms, units)
 */
class AppStateViewModel : ViewModel() {

    // ========== DATA LAYER ==========
    private val dataSource = SupabaseDataSource()
    private val repository = FlashcardRepository(dataSource)

    // ========== USER STATE ==========
    private val _userName = mutableStateOf("")
    val userName: State<String> = _userName

    private val _selectedLanguage = mutableStateOf("en")
    val selectedLanguage: State<String> = _selectedLanguage

    private val _selectedGrade = mutableStateOf<Grade?>(null)
    val selectedGrade: State<Grade?> = _selectedGrade

    private val _selectedSubject = mutableStateOf<Subject?>(null)
    val selectedSubject: State<Subject?> = _selectedSubject

    private val _selectedTerm = mutableStateOf<Term?>(null)
    val selectedTerm: State<Term?> = _selectedTerm

    // ========== DATA LISTS ==========
    private val _grades = mutableStateOf<List<Grade>>(emptyList())
    val grades: State<List<Grade>> = _grades

    private val _subjects = mutableStateOf<List<Subject>>(emptyList())
    val subjects: State<List<Subject>> = _subjects

    private val _terms = mutableStateOf<List<Term>>(emptyList())
    val terms: State<List<Term>> = _terms

    private val _units = mutableStateOf<List<StudyUnit>>(emptyList())
    val units: State<List<StudyUnit>> = _units

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    // ========== METHODS ==========

    fun setUserName(name: String) {
        _userName.value = name
    }

    fun setLanguage(language: String) {
        _selectedLanguage.value = language
    }

    fun setSelectedGrade(grade: Grade) {
        _selectedGrade.value = grade
        _selectedSubject.value = null
        _selectedTerm.value = null
        loadSubjectsForGrade(grade.id)
    }

    fun setSelectedSubject(subject: Subject) {
        _selectedSubject.value = subject
        _selectedTerm.value = null
        loadTermsForSubject(subject.id)
    }

    fun setSelectedTerm(term: Term) {
        _selectedTerm.value = term
        loadUnitsForTerm(term.id)
    }

    /**
     * Load all grades from Supabase
     */
    fun loadGrades() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val gradesList = repository.getAllGrades()
                _grades.value = gradesList
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Load subjects for selected grade
     */
    private fun loadSubjectsForGrade(gradeId: Long) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _subjects.value = emptyList() 
                val subjectsList = repository.getSubjectsByGrade(gradeId)
                _subjects.value = subjectsList
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Load terms for selected subject
     */
    fun loadTermsForSubject(subjectId: Long) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _terms.value = emptyList()
                _units.value = emptyList() // Clear units until term is selected

                val termsList = repository.getTermsBySubject(subjectId)
                _terms.value = termsList
                
                // Auto-select first term if available
                if (termsList.isNotEmpty()) {
                    setSelectedTerm(termsList.first())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Load units for a specific term
     */
    fun loadUnitsForTerm(termId: Long) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _units.value = emptyList()

                val unitsList = repository.getUnitsByTerm(termId)
                _units.value = unitsList
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
