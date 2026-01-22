// File: com/lavariyalabs/snapy/android/ui/screen/ProfileScreen.kt

package com.lavariyalabs.snapy.android.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lavariyalabs.snapy.android.ui.viewmodel.AppStateViewModel
import com.lavariyalabs.snapy.android.ui.viewmodel.ProfileViewModel
import com.lavariyalabs.snapy.android.utils.SoundManager

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
    onNavigateBack: () -> Unit,
    appStateViewModel: AppStateViewModel,
    profileViewModel: ProfileViewModel
) {

    // Load statistics when screen appears
    LaunchedEffect(Unit) {
        profileViewModel.loadStudyStats()
    }

    val userName by appStateViewModel.userName
    val selectedGrade by appStateViewModel.selectedGrade
    val selectedSubject by appStateViewModel.selectedSubject
    val language by appStateViewModel.selectedLanguage
    val studyStats by profileViewModel.studyStats
    val isLoading by profileViewModel.isLoading

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
                        onNavigateBack()
                        SoundManager.playClickSound()
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
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFF6366F1))
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // ========== USER INFO SECTION ==========
                Text(
                    text = "Personal Info",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937)
                )

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

                Spacer(modifier = Modifier.height(8.dp))

                // ========== STUDY STATISTICS SECTION ==========
                Text(
                    text = "Study Statistics",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937)
                )

                // Cards Overview
                StatsGrid(
                    items = listOf(
                        StatItem("Total Cards", studyStats.totalCards.toString(), Color(0xFF6366F1)),
                        StatItem("Due Today", studyStats.cardsDueToday.toString(), Color(0xFFEF4444)),
                        StatItem("Learning", studyStats.learningCards.toString(), Color(0xFFF59E0B)),
                        StatItem("Mature", studyStats.matureCards.toString(), Color(0xFF10B981))
                    )
                )

                // Review Statistics
                Text(
                    text = "Review Performance",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937),
                    modifier = Modifier.padding(top = 8.dp)
                )

                ProfileCard(
                    title = "Total Reviews",
                    value = studyStats.totalReviews.toString()
                )

                StatsGrid(
                    items = listOf(
                        StatItem("Correct", studyStats.correctReviews.toString(), Color(0xFF10B981)),
                        StatItem("Incorrect", studyStats.incorrectReviews.toString(), Color(0xFFEF4444))
                    )
                )

                // Retention Rate with visual indicator
                RetentionRateCard(
                    retentionPercentage = studyStats.retentionPercentage
                )

                // Algorithm Metrics
                Text(
                    text = "Learning Metrics",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937),
                    modifier = Modifier.padding(top = 8.dp)
                )

                StatsGrid(
                    items = listOf(
                        StatItem("Avg Ease Factor", String.format("%.2f", studyStats.averageEaseFactor), Color(0xFF8B5CF6)),
                        StatItem("Avg Interval", "${studyStats.averageInterval} days", Color(0xFF3B82F6))
                    )
                )
            }
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

/**
 * StatItem - Data class for grid items
 */
data class StatItem(
    val title: String,
    val value: String,
    val color: Color
)

/**
 * StatsGrid - Grid layout for statistics
 */
@Composable
private fun StatsGrid(
    items: List<StatItem>
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items.forEach { item ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(16.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = item.value,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = item.color
                    )

                    Text(
                        text = item.title,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF64748B),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

/**
 * RetentionRateCard - Shows retention rate with visual indicator
 */
@Composable
private fun RetentionRateCard(
    retentionPercentage: Int
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
                text = "Retention Rate",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF64748B)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$retentionPercentage%",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        retentionPercentage >= 80 -> Color(0xFF10B981)
                        retentionPercentage >= 60 -> Color(0xFFF59E0B)
                        else -> Color(0xFFEF4444)
                    },
                    modifier = Modifier.weight(1f)
                )

                // Visual bar
                Box(
                    modifier = Modifier
                        .weight(2f)
                        .height(12.dp)
                        .background(
                            color = Color(0xFFE5E7EB),
                            shape = RoundedCornerShape(6.dp)
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(retentionPercentage / 100f)
                            .background(
                                color = when {
                                    retentionPercentage >= 80 -> Color(0xFF10B981)
                                    retentionPercentage >= 60 -> Color(0xFFF59E0B)
                                    else -> Color(0xFFEF4444)
                                },
                                shape = RoundedCornerShape(6.dp)
                            )
                    )
                }
            }

            Text(
                text = when {
                    retentionPercentage >= 80 -> "Excellent! Keep it up!"
                    retentionPercentage >= 60 -> "Good progress, room for improvement"
                    else -> "Practice more to improve retention"
                },
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF64748B),
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
