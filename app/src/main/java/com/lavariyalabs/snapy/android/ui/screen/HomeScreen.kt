package com.lavariyalabs.snapy.android.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.lavariyalabs.snapy.android.data.model.StudyUnit
import com.lavariyalabs.snapy.android.navigation.NavRoutes
import com.lavariyalabs.snapy.android.ui.viewmodel.AppStateViewModel
import com.lavariyalabs.snapy.android.ui.viewmodel.HomeViewModel

/**
 * HomeScreen - Main app screen
 *
 * Features:
 * - Subject selector dropdown
 * - Term selector dropdown
 * - Units (flashcard libraries) in a grid
 * - Bottom navigation bar (Study & Profile)
 */
@Composable
fun HomeScreen(
    navController: NavController,
    appStateViewModel: AppStateViewModel,
    homeViewModel: HomeViewModel
) {

    val selectedSubject by appStateViewModel.selectedSubject
    val selectedTerm by appStateViewModel.selectedTerm
    val selectedGrade by appStateViewModel.selectedGrade
    val subjects by homeViewModel.subjects
    val terms by homeViewModel.terms
    val units by homeViewModel.units
    val isLoading by homeViewModel.isLoading

    var showSubjectDropdown by remember { mutableStateOf(false) }
    var showTermDropdown by remember { mutableStateOf(false) }

    // Load subjects when grade is selected
    LaunchedEffect(selectedGrade) {
        selectedGrade?.let { grade ->
            homeViewModel.loadSubjectsForGrade(grade.id)
        }
    }

    // Load terms when subject is selected
    LaunchedEffect(selectedSubject) {
        selectedSubject?.let { subject ->
            homeViewModel.loadTermsForSubject(subject.id)
        }
    }

    // Load units when term is selected
    LaunchedEffect(selectedTerm) {
        selectedTerm?.let { term ->
            homeViewModel.loadUnitsForTerm(term.id)
        }
    }

    Scaffold(
        bottomBar = {
            BottomAppBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Study Tab (Active)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable { /* Already on Home */ }
                            .padding(8.dp)
                    ) {
                        Text("📚", fontSize = 24.sp)
                        Text(
                            "Study",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF6366F1)
                        )
                    }

                    // Profile Tab (Inactive)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable { navController.navigate(NavRoutes.PROFILE) }
                            .padding(8.dp)
                    ) {
                        Text("👤", fontSize = 24.sp)
                        Text(
                            "Profile",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC))
                .padding(innerPadding)
        ) {

            // ========== SELECTORS SECTION ==========
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // SUBJECT SELECTOR
                Text(
                    text = "Subject",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF64748B),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(2.dp, RoundedCornerShape(12.dp))
                            .background(Color.White, RoundedCornerShape(12.dp))
                            .border(
                                width = 1.dp, 
                                color = if (showSubjectDropdown) Color(0xFF6366F1) else Color(0xFFE2E8F0),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { showSubjectDropdown = !showSubjectDropdown }
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = selectedSubject?.name ?: "Select a subject",
                                fontSize = 16.sp,
                                color = if (selectedSubject != null) Color(0xFF1F2937) else Color(0xFF64748B),
                                fontWeight = if (selectedSubject != null) FontWeight.Medium else FontWeight.Normal
                            )
                            Icon(
                                imageVector = if (showSubjectDropdown) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = "Dropdown",
                                tint = Color(0xFF64748B)
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = showSubjectDropdown,
                        onDismissRequest = { showSubjectDropdown = false },
                        modifier = Modifier
                            .background(Color.White)
                            .fillMaxWidth(0.9f) // Approximate width
                    ) {
                        subjects.forEach { subject ->
                            DropdownMenuItem(
                                text = { Text(subject.name) },
                                onClick = {
                                    appStateViewModel.setSelectedSubject(subject)
                                    showSubjectDropdown = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // TERM SELECTOR
                if (selectedSubject != null) {
                    Text(
                        text = "Term",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF64748B),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(2.dp, RoundedCornerShape(12.dp))
                                .background(Color.White, RoundedCornerShape(12.dp))
                                .border(
                                    width = 1.dp,
                                    color = if (showTermDropdown) Color(0xFF6366F1) else Color(0xFFE2E8F0),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { showTermDropdown = !showTermDropdown }
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = selectedTerm?.name ?: "Select a term",
                                    fontSize = 16.sp,
                                    color = if (selectedTerm != null) Color(0xFF1F2937) else Color(0xFF64748B),
                                    fontWeight = if (selectedTerm != null) FontWeight.Medium else FontWeight.Normal
                                )
                                Icon(
                                    imageVector = if (showTermDropdown) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Dropdown",
                                    tint = Color(0xFF64748B)
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = showTermDropdown,
                            onDismissRequest = { showTermDropdown = false },
                            modifier = Modifier
                                .background(Color.White)
                                .fillMaxWidth(0.9f)
                        ) {
                            terms.forEach { term ->
                                DropdownMenuItem(
                                    text = { Text(term.name) },
                                    onClick = {
                                        appStateViewModel.setSelectedTerm(term)
                                        showTermDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // ========== UNITS GRID ==========
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF6366F1))
                }
            } else if (units.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = when {
                            selectedSubject == null -> "Please select a subject."
                            selectedTerm == null -> "Please select a term."
                            else -> "No units found for this term."
                        },
                        color = Color(0xFF64748B)
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp), // Increased spacing
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(units) { unit ->
                        UnitCard(
                            unit = unit,
                            onClick = {
                                navController.navigate(NavRoutes.flashcardRoute(unit.id))
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * UnitCard - Flashcard library card
 */
@Composable
private fun UnitCard(
    unit: StudyUnit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            // Decorative colored strip on the left
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .fillMaxHeight()
                    .background(Color(0xFF6366F1))
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = unit.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F2937),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 22.sp
                    )

                    if (unit.description.isNotEmpty()) {
                        Text(
                            text = unit.description,
                            fontSize = 12.sp,
                            color = Color(0xFF64748B),
                            modifier = Modifier.padding(top = 8.dp),
                            maxLines = 4,
                            overflow = TextOverflow.Ellipsis,
                            lineHeight = 16.sp
                        )
                    }
                }

                Text(
                    text = "Study →",
                    fontSize = 12.sp,
                    color = Color(0xFF6366F1),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
