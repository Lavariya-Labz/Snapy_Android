package com.lavariyalabs.snapy.android.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import com.lavariyalabs.snapy.android.ui.viewmodel.AppStateViewModel

/**
 * HomeScreen - Main study screen
 *
 * Features:
 * - Subject selector dropdown
 * - Grid of units for selected subject
 * - Tab bar (Study/Profile)
 * - Click unit to study
 */
@Composable
fun HomeScreen(
    navController: NavController,
    appStateViewModel: AppStateViewModel
) {
    val selectedSubject = remember { mutableStateOf(appStateViewModel.selectedSubject.value) }
    val selectedTab = remember { mutableStateOf("study") }

    // Sample data
    val subjects = listOf("Biology", "Chemistry", "Physics")
    val units = listOf(
        StudyUnit(id = 1, name = "Unit 1: Cell Biology", color = Color(0xFF3B82F6)),
        StudyUnit(id = 2, name = "Unit 2: Genetics", color = Color(0xFF8B5CF6)),
        StudyUnit(id = 3, name = "Unit 3: Evolution", color = Color(0xFF10B981)),
        StudyUnit(id = 4, name = "Unit 4: Ecology", color = Color(0xFFF59E0B))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        // ========== HEADER WITH SUBJECT SELECTOR ==========
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color(0xFF6366F1),
                    shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                )
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = "Hi, ${appStateViewModel.userName.value} 👋",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Subject Selector
                SubjectSelector(
                    subjects = subjects,
                    selectedSubject = selectedSubject.value,
                    onSubjectSelected = { subject ->
                        selectedSubject.value = subject
                        appStateViewModel.setSubject(subject)
                    }
                )
            }
        }

        // ========== UNITS GRID ==========
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(units) { unit ->
                UnitCard(
                    unit = unit,
                    onClick = {
                        //navController.navigate(NavRoutes.flashcardRoute(unit.id))
                        navController.navigate(NavRoutes.FLASHCARD)
                    }
                )
            }
        }

        // ========== TAB BAR ==========
        TabBar(
            selectedTab = selectedTab.value,
            onStudyClick = { selectedTab.value = "study" },
            onProfileClick = {
                selectedTab.value = "profile"
                navController.navigate(NavRoutes.PROFILE)
            }
        )
    }
}

/**
 * SubjectSelector - Dropdown for subject selection
 */
@Composable
private fun SubjectSelector(
    subjects: List<String>,
    selectedSubject: String,
    onSubjectSelected: (String) -> Unit
) {
    val isExpanded = remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(8.dp))
                .clickable { isExpanded.value = !isExpanded.value }
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = selectedSubject,
                fontSize = 14.sp,
                color = Color(0xFF1F2937),
                fontWeight = FontWeight.Medium
            )
            Text("▼", fontSize = 12.sp)
        }

        // Dropdown options
        if (isExpanded.value) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                subjects.forEach { subject ->
                    Text(
                        text = subject,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSubjectSelected(subject)
                                isExpanded.value = false
                            }
                            .padding(12.dp),
                        fontSize = 14.sp,
                        color = Color(0xFF1F2937)
                    )
                }
            }
        }
    }
}

/**
 * UnitCard - Individual unit card in grid
 */
@Composable
private fun UnitCard(
    unit: StudyUnit,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .background(unit.color, RoundedCornerShape(16.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = unit.name,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            modifier = Modifier.padding(12.dp)
        )
    }
}

/**
 * TabBar - Bottom navigation
 */
@Composable
private fun TabBar(
    selectedTab: String,
    onStudyClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            TabItem(
                icon = "📚",
                label = "Study",
                isSelected = selectedTab == "study",
                onClick = onStudyClick
            )

            TabItem(
                icon = "👤",
                label = "Profile",
                isSelected = selectedTab == "profile",
                onClick = onProfileClick
            )
        }
    }
}

/**
 * TabItem - Individual tab in tab bar
 */
@Composable
private fun TabItem(
    icon: String,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable { onClick() }
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(icon, fontSize = 24.sp)
        Text(
            label,
            fontSize = 12.sp,
            color = if (isSelected) Color(0xFF6366F1) else Color(0xFF94A3B8),
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

data class StudyUnit(
    val id: Long,
    val name: String,
    val color: Color
)
