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
 * OnboardingLanguageScreen - Step 1 of onboarding
 *
 * User selects preferred language
 * Stores selection in AppStateViewModel
 */
@Composable
fun OnboardingLanguageScreen(
    navController: NavController,
    appStateViewModel: AppStateViewModel
) {

    val selectedLanguage = remember { mutableStateOf<String?>(null) }

    val languages = listOf(
        Language(code = "en", name = "English"),
        Language(code = "si", name = "Sinhala (සිංහල)"),
        Language(code = "ta", name = "Tamil (தமிழ்)")
    )

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

        // ========== LANGUAGE SELECTION ==========
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            languages.forEach { language ->
                LanguageSelectionCard(
                    language = language,
                    isSelected = selectedLanguage.value == language.code,
                    onClick = {
                        selectedLanguage.value = language.code
                    }
                )
            }
        }

        // ========== CONTINUE BUTTON ==========
        ContinueButton(
            isEnabled = selectedLanguage.value != null,
            onClick = {
                if (selectedLanguage.value != null) {
                    appStateViewModel.setLanguage(selectedLanguage.value!!)
                    navController.navigate(NavRoutes.ONBOARDING_NAME)
                }
            }
        )
    }
}

/**
 * LanguageSelectionCard - Individual language option
 */
@Composable
private fun LanguageSelectionCard(
    language: Language,
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = language.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isSelected) Color.White else Color(0xFF1F2937)
            )
        }
    }
}

data class Language(
    val code: String,
    val name: String
)
