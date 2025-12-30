package com.lavariyalabs.snapy.android.navigation

/**
 * NavRoutes - All app routes in one place
 *
 * WHY this pattern?
 * - Single source of truth for navigation
 * - Type-safe route references
 * - Easy to find all screens
 */
object NavRoutes {

    // ========== ONBOARDING ==========
    const val SPLASH = "splash"
    const val ONBOARDING_LANGUAGE = "onboarding/language"
    const val ONBOARDING_NAME = "onboarding/name"
    const val ONBOARDING_GRADE = "onboarding/grade"
    const val ONBOARDING_SUBJECT = "onboarding/subject"

    // ========== APP ==========
    const val HOME = "home"
    const val PROFILE = "profile"
    const val FLASHCARD = "flashcard/{unitId}"

    /**
     * Helper to create flashcard route with ID
     */
    fun flashcardRoute(unitId: Long): String = "flashcard/$unitId"
}
