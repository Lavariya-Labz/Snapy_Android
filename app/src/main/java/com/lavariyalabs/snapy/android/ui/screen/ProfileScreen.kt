package com.lavariyalabs.snapy.android.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.lavariyalabs.snapy.android.navigation.NavRoutes
import com.lavariyalabs.snapy.android.ui.viewmodel.AppStateViewModel

/**
 * ProfileScreen - User profile information
 *
 * Shows:
 * - User name
 * - Grade
 * - Subject
 * - Language
 * - Back to study button
 */
@Composable
fun ProfileScreen(
    navController: NavController,
    appStateViewModel: AppStateViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // ========== HEADER ==========
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF6366F1), RoundedCornerShape(16.dp))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "👤",
                fontSize = 64.sp
            )
        }

        // ========== PROFILE INFO ==========
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            ProfileInfoItem(
                label = "Name",
                value = appStateViewModel.userName.value
            )

            ProfileInfoItem(
                label = "Grade",
                value = appStateViewModel.selectedGrade.value
            )

            ProfileInfoItem(
                label = "Subject",
                value = appStateViewModel.selectedSubject.value
            )

            ProfileInfoItem(
                label = "Language",
                value = appStateViewModel.selectedLanguage.value.uppercase()
            )
        }

        // ========== BACK BUTTON ==========
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(
                    color = Color(0xFF6366F1),
                    shape = RoundedCornerShape(12.dp)
                )
                .clickable {
                    navController.navigate(NavRoutes.HOME)
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Back to Study",
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
private fun ProfileInfoItem(
    label: String,
    value: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(12.dp))
            .padding(16.dp)
            .padding(bottom = 12.dp)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color(0xFF64748B),
            fontWeight = FontWeight.SemiBold
        )

        Text(
            text = value,
            fontSize = 18.sp,
            color = Color(0xFF1F2937),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
