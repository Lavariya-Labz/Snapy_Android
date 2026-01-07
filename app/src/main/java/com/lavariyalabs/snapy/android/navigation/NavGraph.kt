package com.lavariyalabs.snapy.android.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.lavariyalabs.snapy.android.ui.screen.*
import com.lavariyalabs.snapy.android.ui.viewmodel.AppStateViewModel
import com.lavariyalabs.snapy.android.ui.viewmodel.ViewModelFactory

/**
 * NavGraph - Defines all routes and screens
 *
 * This is the app's navigation blueprint
 */
@Composable
fun NavGraph(
    navController: NavHostController,
    appStateViewModel: AppStateViewModel,
    viewModelFactory: ViewModelFactory
) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.SPLASH
    ) {
        // SPLASH
        composable(NavRoutes.SPLASH) {
            SplashScreen(navController = navController)
        }

        // ONBOARDING
        composable(NavRoutes.ONBOARDING_LANGUAGE) {
            OnboardingLanguageScreen(
                navController = navController,
                appStateViewModel = appStateViewModel,
                onboardingViewModel = viewModel(factory = viewModelFactory)
            )
        }

        composable(NavRoutes.ONBOARDING_NAME) {
            OnboardingNameScreen(
                navController = navController,
                appStateViewModel = appStateViewModel
            )
        }

        composable(NavRoutes.ONBOARDING_GRADE) {
            OnboardingGradeScreen(
                navController = navController,
                appStateViewModel = appStateViewModel,
                onboardingViewModel = viewModel(factory = viewModelFactory)
            )
        }

        composable(NavRoutes.ONBOARDING_SUBJECT) {
            OnboardingSubjectScreen(
                navController = navController,
                appStateViewModel = appStateViewModel,
                onboardingViewModel = viewModel(factory = viewModelFactory)
            )
        }

        // APP SCREENS
        composable(NavRoutes.HOME) {
            HomeScreen(
                navController = navController,
                appStateViewModel = appStateViewModel,
                homeViewModel = viewModel(factory = viewModelFactory)
            )
        }

        composable(NavRoutes.PROFILE) {
            ProfileScreen(
                navController = navController,
                appStateViewModel = appStateViewModel,
                profileViewModel = viewModel(factory = viewModelFactory)
            )
        }

        // ========== FLASHCARD ==========
        composable(
            route = NavRoutes.FLASHCARD,
            arguments = listOf(navArgument("unitId") { type = NavType.LongType })
        ) { backStackEntry ->
            val unitId = backStackEntry.arguments?.getLong("unitId") ?: return@composable

            FlashcardStudyScreen(
                navController = navController,
                unitId = unitId,
                viewModel = viewModel(factory = viewModelFactory)
            )
        }

    }
}
