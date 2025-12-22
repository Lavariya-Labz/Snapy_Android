package com.lavariyalabs.snapy.android.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun OnboardingScreen(onNextClick: () -> Unit) {
    val gradeOptions = listOf("Grade 10", "Grade 11", "Grade 12")
    val languageOptions = listOf("English", "Sinhala", "Tamil")

    var selectedGrade by remember { mutableStateOf("") }
    var selectedLanguage by remember { mutableStateOf("") }

    val isButtonEnabled = selectedGrade.isNotEmpty() && selectedLanguage.isNotEmpty()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF121212)
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(24.dp)) {

            // Central Content
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // This calls the function defined below
                DropdownSelectionField(
                    label = "what grade",
                    options = gradeOptions,
                    selectedValue = selectedGrade,
                    onValueChange = { selectedGrade = it }
                )

                Spacer(modifier = Modifier.height(40.dp))

                DropdownSelectionField(
                    label = "Language",
                    options = languageOptions,
                    selectedValue = selectedLanguage,
                    onValueChange = { selectedLanguage = it }
                )
            }

            // Bottom "Next" Button
            Row(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Next",
                    color = if (isButtonEnabled) Color.White else Color.Gray,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(end = 12.dp)
                )

                OutlinedIconButton(
                    onClick = onNextClick,
                    enabled = isButtonEnabled,
                    modifier = Modifier.size(56.dp),
                    shape = CircleShape,
                    border = BorderStroke(
                        width = 2.dp,
                        color = if (isButtonEnabled) Color.White else Color.Gray
                    ),
                    colors = IconButtonDefaults.outlinedIconButtonColors(
                        contentColor = Color.White,
                        disabledContentColor = Color.Gray
                    )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Next",
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}

// --- THIS IS THE MISSING FUNCTION THAT WAS CAUSING THE UNRESOLVED REFERENCE ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownSelectionField(
    label: String,
    options: List<String>,
    selectedValue: String,
    onValueChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            color = Color.White,
            fontSize = 22.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            OutlinedTextField(
                value = selectedValue,
                onValueChange = {},
                readOnly = true,
                placeholder = { Text("Select...", color = Color.Gray) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                ),
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(Color(0xFF1E1E1E))
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option, color = Color.White) },
                        onClick = {
                            onValueChange(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}