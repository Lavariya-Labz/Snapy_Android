// File: com/lavariyalabs/snapy/android/ui/screen/ProfileScreen.kt

package com.lavariyalabs.snapy.android.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.lavariyalabs.snapy.android.ui.viewmodel.AppStateViewModel
import com.lavariyalabs.snapy.android.ui.viewmodel.ProfileViewModel

/**
 * ProfileScreen - User profile information
 *
 * Shows:
 * - User name
 * - Selected grade and subject
 * - Language preference
 * - Statistics
 */
@Composable
fun ProfileScreen(
    navController: NavController,
    appStateViewModel: AppStateViewModel,
    profileViewModel: ProfileViewModel
) {

    val userName by appStateViewModel.userName
    val selectedGrade by appStateViewModel.selectedGrade
    val selectedSubject by appStateViewModel.selectedSubject
    val language by appStateViewModel.selectedLanguage

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {

        // ========== HEADER ==========
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color(0xFF6366F1),
                    shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                )
                .padding(16.dp)
        ) {
            Text(
                text = "← Back",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .clickable {
                        navController.popBackStack()
                    }
            )

            Text(
                text = "Profile",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // ========== PROFILE INFO ==========
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Name Card
            ProfileCard(
                title = "Name",
                value = userName.ifEmpty { "Not set" }
            )

            // Grade Card
            ProfileCard(
                title = "Grade",
                value = selectedGrade?.name ?: "Not selected"
            )

            // Subject Card
            ProfileCard(
                title = "Current Subject",
                value = selectedSubject?.name ?: "Not selected"
            )

            // Language Card
            ProfileCard(
                title = "Language",
                value = when (language) {
                    "en" -> "English"
                    "si" -> "Sinhala"
                    "ta" -> "Tamil"
                    else -> language
                }
            )

            // Stats Card
            val cardsStudied by profileViewModel.cardsStudied
            ProfileCard(
                title = "Cards Studied",
                value = cardsStudied.toString()
            )
        }
    }
}

/**
 * ProfileCard - Information display card
 */
@Composable
private fun ProfileCard(
    title: String,
    value: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color.White,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF64748B)
            )

            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
