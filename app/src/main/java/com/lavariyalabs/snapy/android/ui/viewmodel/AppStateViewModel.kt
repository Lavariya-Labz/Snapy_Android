package com.lavariyalabs.snapy.android.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State

/**
 * AppStateViewModel - Shared state across all screens
 *
 * WHY shared state?
 * - User selections persist across screens
 * - Home screen needs to know selected subject
 * - Profile screen displays user info
 * - Survives navigation
 *
 * Think of it as "app memory"
 */
class AppStateViewModel : ViewModel() {

    // ========== USER DATA ==========
    private val _userName = mutableStateOf("")
    val userName: State<String> = _userName

    private val _selectedLanguage = mutableStateOf("en")
    val selectedLanguage: State<String> = _selectedLanguage

    private val _selectedGrade = mutableStateOf("")
    val selectedGrade: State<String> = _selectedGrade

    private val _selectedSubject = mutableStateOf("")
    val selectedSubject: State<String> = _selectedSubject

    // ========== METHODS ==========

    fun setUserName(name: String) {
        _userName.value = name
    }

    fun setLanguage(language: String) {
        _selectedLanguage.value = language
    }

    fun setGrade(grade: String) {
        _selectedGrade.value = grade
    }

    fun setSubject(subject: String) {
        _selectedSubject.value = subject
    }
}
