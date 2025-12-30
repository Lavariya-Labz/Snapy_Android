// File: com/lavariyalabs/snapy/android/ui/screen/OnboardingGradeScreen.kt

package com.lavariyalabs.snapy.android.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.lavariyalabs.snapy.android.data.model.Grade
import com.lavariyalabs.snapy.android.navigation.NavRoutes
import com.lavariyalabs.snapy.android.ui.components.ContinueButton
import com.lavariyalabs.snapy.android.ui.viewmodel.AppStateViewModel

/**
 * OnboardingGradeScreen - Step 3 of onboarding
 *
 * User selects their grade (O/L or A/L)
 * Loads grades from Supabase
 */
@Composable
fun OnboardingGradeScreen(
    navController: NavController,
    appStateViewModel: AppStateViewModel
) {

    // Load grades from Supabase
    LaunchedEffect(Unit) {
        appStateViewModel.loadGrades()
    }

    val selectedGrade = remember { mutableStateOf<Grade?>(null) }
    val grades by appStateViewModel.grades
    val isLoading by appStateViewModel.isLoading

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
                text = "What is your grade?",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937),
                modifier = Modifier.padding(top = 32.dp, bottom = 8.dp)
            )

            Text(
                text = "Select your current grade level",
                fontSize = 14.sp,
                color = Color(0xFF64748B),
                modifier = Modifier.padding(bottom = 32.dp)
            )
        }

        // ========== GRADE SELECTION ==========
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color(0xFF6366F1))
                Spacer(modifier = Modifier.height(16.dp))
                Text("Loading grades...", color = Color(0xFF64748B))
            } else if (grades.isEmpty()) {
                Text("No grades found. Please check internet connection.", color = Color(0xFF64748B))
            } else {
                grades.forEach { grade ->
                    GradeSelectionCard(
                        grade = grade,
                        isSelected = selectedGrade.value?.id == grade.id,
                        onClick = {
                            selectedGrade.value = grade
                            appStateViewModel.setSelectedGrade(grade)
                        }
                    )
                }
            }
        }

        // ========== CONTINUE BUTTON ==========
        ContinueButton(
            isEnabled = selectedGrade.value != null,
            onClick = {
                if (selectedGrade.value != null) {
                    navController.navigate(NavRoutes.ONBOARDING_SUBJECT)
                }
            }
        )
    }
}

/**
 * GradeSelectionCard - Individual grade option
 */
@Composable
private fun GradeSelectionCard(
    grade: Grade,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .background(
                color = if (isSelected) Color(0xFF6366F1) else Color.White,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 16.dp, horizontal = 20.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Column {
            Text(
                text = grade.name,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.White else Color(0xFF1F2937)
            )

            Text(
                text = grade.description,
                fontSize = 12.sp,
                color = if (isSelected) Color.White.copy(alpha = 0.8f) else Color(0xFF64748B),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
