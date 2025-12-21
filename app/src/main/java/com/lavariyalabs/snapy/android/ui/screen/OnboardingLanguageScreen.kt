package com.lavariyalabs.snapy.android.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
 * OnboardingLanguageScreen - Step 1: Language Selection
 *
 * User selects app language from dropdown
 */
@Composable
fun OnboardingLanguageScreen(
    navController: NavController,
    appStateViewModel: AppStateViewModel
) {
    val selectedLanguage = remember { mutableStateOf("en") }

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
                text = "Welcome to Snap",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937),
                modifier = Modifier.padding(top = 32.dp, bottom = 8.dp)
            )

            Text(
                text = "Select your preferred language",
                fontSize = 14.sp,
                color = Color(0xFF64748B),
                modifier = Modifier.padding(bottom = 32.dp)
            )
        }

        // ========== LANGUAGE OPTIONS ==========
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            listOf(
                "en" to "🇬🇧 English",
                "si" to "🇱🇰 සිංහල"
            ).forEach { (code, name) ->
                LanguageOption(
                    name = name,
                    isSelected = selectedLanguage.value == code,
                    onClick = { selectedLanguage.value = code }
                )
            }
        }

        // ========== CONTINUE BUTTON ==========
        ContinueButton(
            enabled = true,
            onClick = {
                appStateViewModel.setLanguage(selectedLanguage.value)
                navController.navigate(NavRoutes.ONBOARDING_NAME)
            }
        )
    }
}

@Composable
private fun LanguageOption(
    name: String,
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
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isSelected) Color.White else Color(0xFF1F2937)
        )
    }
}
