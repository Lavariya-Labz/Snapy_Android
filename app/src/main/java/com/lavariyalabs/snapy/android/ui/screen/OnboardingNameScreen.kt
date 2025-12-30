package com.lavariyalabs.snapy.android.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
 * OnboardingNameScreen - Step 2 of onboarding
 *
 * User enters their name
 * Validates non-empty input
 */
@Composable
fun OnboardingNameScreen(
    navController: NavController,
    appStateViewModel: AppStateViewModel
) {

    val nameInput = remember { mutableStateOf("") }

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
                text = "What's your name?",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937),
                modifier = Modifier.padding(top = 32.dp, bottom = 8.dp)
            )

            Text(
                text = "We'd love to know your name",
                fontSize = 14.sp,
                color = Color(0xFF64748B),
                modifier = Modifier.padding(bottom = 32.dp)
            )
        }

        // ========== NAME INPUT ==========
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            TextField(
                value = nameInput.value,
                onValueChange = { nameInput.value = it },
                placeholder = {
                    Text("Enter your full name", color = Color(0xFF94A3B8))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(12.dp)
                    ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedTextColor = Color(0xFF1F2937),
                    unfocusedTextColor = Color(0xFF1F2937),
                    focusedIndicatorColor = Color(0xFF6366F1),
                    unfocusedIndicatorColor = Color(0xFFE2E8F0)
                ),
                singleLine = true
            )
        }

        // ========== CONTINUE BUTTON ==========
        ContinueButton(
            isEnabled = nameInput.value.isNotEmpty(),
            onClick = {
                if (nameInput.value.isNotEmpty()) {
                    appStateViewModel.setUserName(nameInput.value)
                    navController.navigate(NavRoutes.ONBOARDING_GRADE)
                }
            }
        )
    }
}
