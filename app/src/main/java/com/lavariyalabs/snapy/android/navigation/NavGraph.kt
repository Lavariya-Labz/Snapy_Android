package com.lavariyalabs.snapy.android.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lavariyalabs.snapy.android.ui.screen.*
import com.lavariyalabs.snapy.android.ui.viewmodel.AppStateViewModel
import com.lavariyalabs.snapy.android.ui.viewmodel.ViewModelFactory
import com.lavariyalabs.snapy.android.ui.viewmodel.FlashcardViewModel
import com.lavariyalabs.snapy.android.ui.viewmodel.ProfileViewModel

/**
 * NavGraph - State-based navigation without routes
 *
 * Uses sealed class Screen instead of string routes
 */
@Composable
fun NavGraph(
    navigationState: NavigationState,
    appStateViewModel: AppStateViewModel,
    viewModelFactory: ViewModelFactory
) {
    // Navigate based on current screen state
    when (val screen = navigationState.currentScreen) {
        is Screen.Splash -> {
            SplashScreen(
                onNavigateToOnboarding = {
                    navigationState.navigateTo(Screen.OnboardingLanguage)
                }
            )
        }
        
        is Screen.OnboardingLanguage -> {
            OnboardingLanguageScreen(
                onNavigateNext = {
                    navigationState.navigateTo(Screen.OnboardingName)
                },
                appStateViewModel = appStateViewModel,
                onboardingViewModel = viewModel(factory = viewModelFactory)
            )
        }
        
        is Screen.OnboardingName -> {
            OnboardingNameScreen(
                onNavigateNext = {
                    navigationState.navigateTo(Screen.OnboardingGrade)
                },
                appStateViewModel = appStateViewModel
            )
        }
        
        is Screen.OnboardingGrade -> {
            OnboardingGradeScreen(
                onNavigateNext = {
                    navigationState.navigateTo(Screen.OnboardingSubject)
                },
                onNavigateBack = {
                    navigationState.navigateBack()
                },
                appStateViewModel = appStateViewModel,
                onboardingViewModel = viewModel(factory = viewModelFactory)
            )
        }
        
        is Screen.OnboardingSubject -> {
            OnboardingSubjectScreen(
                onNavigateToHome = {
                    navigationState.navigateToAndClearStack(Screen.Home)
                },
                onNavigateBack = {
                    navigationState.navigateBack()
                },
                appStateViewModel = appStateViewModel,
                onboardingViewModel = viewModel(factory = viewModelFactory)
            )
        }
        
        is Screen.Home -> {
            HomeScreen(
                onNavigateToProfile = {
                    navigationState.navigateTo(Screen.Profile)
                },
                onNavigateToFlashcard = { unitId ->
                    navigationState.navigateTo(Screen.Flashcard(unitId))
                },
                appStateViewModel = appStateViewModel,
                homeViewModel = viewModel(factory = viewModelFactory)
            )
        }
        
        is Screen.Profile -> {
            val profileViewModel: ProfileViewModel = viewModel(factory = viewModelFactory)
            val userId by appStateViewModel.userId
            profileViewModel.userId = userId

            ProfileScreen(
                onNavigateBack = {
                    navigationState.navigateBack()
                },
                appStateViewModel = appStateViewModel,
                profileViewModel = profileViewModel
            )
        }

        is Screen.Flashcard -> {
            val flashcardViewModel: FlashcardViewModel = viewModel(factory = viewModelFactory)
            val userId by appStateViewModel.userId
            flashcardViewModel.userId = userId

            FlashcardStudyScreen(
                unitId = screen.unitId,
                onNavigateBack = {
                    navigationState.navigateBack()
                },
                viewModel = flashcardViewModel
            )
        }
    }
}
