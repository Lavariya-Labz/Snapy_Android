package com.lavariyalabs.snapy.android.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.lavariyalabs.snapy.android.navigation.NavRoutes
import com.lavariyalabs.snapy.android.ui.components.ContinueButton
import com.lavariyalabs.snapy.android.ui.viewmodel.AppStateViewModel

/**
 * OnboardingSubjectScreen - Step 4: Subject Selection
 *
 * User selects their first subject to study
 * This determines what appears on home screen
 */
@Composable
fun OnboardingSubjectScreen(
    navController: NavController,
    appStateViewModel: AppStateViewModel
) {
    val selectedSubject = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ========== HEADER ==========
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "What subject are we going to learn first",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937),
                modifier = Modifier.padding(top = 32.dp, bottom = 8.dp)
            )

            Text(
                text = "You can change this anytime",
                fontSize = 14.sp,
                color = Color(0xFF64748B),
                modifier = Modifier.padding(bottom = 32.dp)
            )
        }

        // ========== SUBJECT OPTIONS ==========
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            listOf("Biology", "Chemistry").forEach { subject ->
                SelectionOption(
                    text = subject,
                    isSelected = selectedSubject.value == subject,
                    onClick = { selectedSubject.value = subject }
                )
            }
        }

        // ========== CONTINUE BUTTON ==========
        ContinueButton(
            enabled = selectedSubject.value.isNotEmpty(),
            onClick = {
                appStateViewModel.setSubject(selectedSubject.value)
                navController.navigate(NavRoutes.HOME) {
                    // Remove all onboarding screens from back stack
                    popUpTo(NavRoutes.SPLASH) { inclusive = true }
                }
            }
        )
    }
}
