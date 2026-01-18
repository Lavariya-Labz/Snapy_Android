// File: com/lavariyalabs/snapy/android/ui/screen/OnboardingSubjectScreen.kt

package com.lavariyalabs.snapy.android.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.LaunchedEffect
import com.lavariyalabs.snapy.android.data.model.Subject
import com.lavariyalabs.snapy.android.ui.components.ContinueButton
import com.lavariyalabs.snapy.android.ui.viewmodel.AppStateViewModel
import com.lavariyalabs.snapy.android.ui.viewmodel.OnboardingViewModel
import com.lavariyalabs.snapy.android.utils.SoundManager

/**
 * OnboardingSubjectScreen - Step 4 of onboarding
 *
 * User selects subject to study
 * Loads subjects based on selected grade
 * After this, onboarding complete → Home screen
 */
@Composable
fun OnboardingSubjectScreen(
    onNavigateToHome: () -> Unit,
    onNavigateBack: () -> Unit,
    appStateViewModel: AppStateViewModel,
    onboardingViewModel: OnboardingViewModel
) {

    val selectedSubject = remember { mutableStateOf<Subject?>(null) }
    val subjects by onboardingViewModel.subjects
    val isLoading by onboardingViewModel.isLoading
    val selectedGrade by appStateViewModel.selectedGrade

    // Load subjects when grade is selected
    LaunchedEffect(selectedGrade) {
        selectedGrade?.let { grade ->
            onboardingViewModel.loadSubjectsForGrade(grade.id)
        }
    }

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
                text = "What subject do you want to learn?",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937),
                modifier = Modifier.padding(top = 32.dp, bottom = 8.dp)
            )

            Text(
                text = "You can change this later",
                fontSize = 14.sp,
                color = Color(0xFF64748B),
                modifier = Modifier.padding(bottom = 32.dp)
            )
        }

        // ========== SUBJECT SELECTION ==========
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
                Text("Loading subjects...", color = Color(0xFF64748B))
            } else if (subjects.isEmpty()) {
                Text("No subjects available for this grade.", color = Color(0xFF64748B))
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(subjects) { subject ->
                        SubjectSelectionCard(
                            subject = subject,
                            isSelected = selectedSubject.value?.id == subject.id,
                            onClick = {
                                SoundManager.playClickSound()
                                selectedSubject.value = subject
                                appStateViewModel.setSelectedSubject(subject)
                            }
                        )
                    }
                }
            }
        }

        // ========== CONTINUE BUTTON ==========
        ContinueButton(
            isEnabled = selectedSubject.value != null,
            onClick = {
                if (selectedSubject.value != null) {
                    // Onboarding complete - go to home
                    onNavigateToHome()
                }
            }
        )
    }
}

/**
 * SubjectSelectionCard - Individual subject option
 */
@Composable
private fun SubjectSelectionCard(
    subject: Subject,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = if (isSelected) Color(0xFF6366F1) else Color.White,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 16.dp, horizontal = 20.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = subject.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.White else Color(0xFF1F2937)
                    )

                    Text(
                        text = subject.code,
                        fontSize = 12.sp,
                        color = if (isSelected) Color.White.copy(alpha = 0.7f) else Color(0xFF94A3B8),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                if (isSelected) {
                    Text(
                        text = "✓",
                        fontSize = 24.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}
