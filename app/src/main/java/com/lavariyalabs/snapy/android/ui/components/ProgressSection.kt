package com.lavariyalabs.snapy.android.ui.components

// File: com/lavariyalabs/snapy/android/ui/components/ProgressSection.kt

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Brush

/**
 * ProgressSection Composable
 *
 * WHAT IS A COMPOSABLE?
 * - A function marked with @Composable that describes UI
 * - Can accept parameters (like data)
 * - Returns UI elements (Compose components)
 * - Gets recomposed when state changes
 */
@Composable
fun ProgressSection(
    currentCard: Int,           // Example: card 3 of 10
    totalCards: Int,            // Total cards in session
    progressPercent: Float      // 0f to 100f
) {

    // ========== ANIMATED PROGRESS VALUE ==========
    /**
     * Smoothly animate the progress value
     * WHY? Makes progress bar fill animation feel organic
     */
    val animatedProgress: Float by animateFloatAsState(
        targetValue = progressPercent / 100f,
        animationSpec = tween(durationMillis = 600),
        label = "progressAnimation"
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF6366F1),  // Indigo
                        Color(0xFF8B5CF6)   // Purple
                    )
                )
            )
            .padding(16.dp)
    ) {
        // ========== CARD COUNTER TEXT ==========
        Text(
            text = "$currentCard / $totalCards",
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // ========== PROGRESS BAR ==========
        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = Color(0xFF10B981),              // Green progress
            trackColor = Color.White.copy(alpha = 0.25f),
        )

        // ========== PROGRESS TEXT (Added) ==========
        /**
         * Shows percentage and motivational text
         * Helps user understand progress visually and numerically
         */
        Text(
            text = "${progressPercent.toInt()}% Complete",
            color = Color.White.copy(alpha = 0.9f),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(top = 10.dp)
        )
    }
}
