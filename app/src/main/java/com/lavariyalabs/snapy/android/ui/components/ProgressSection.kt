// File: com/lavariyalabs/snapy/android/ui/components/ProgressSection.kt

package com.lavariyalabs.snapy.android.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * ProgressSection - Study progress indicator
 *
 * Features:
 * - Card counter (X / Total)
 * - Animated progress bar
 * - Percentage completion
 * - Gradient background
 */
@Composable
fun ProgressSection(
    currentCard: Int,
    totalCards: Int,
    progressPercent: Float
) {

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
                        Color(0xFF6366F1),
                        Color(0xFF8B5CF6)
                    )
                )
            )
            .padding(16.dp)
    ) {

        Text(
            text = "$currentCard / $totalCards",
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = Color(0xFF10B981),
            trackColor = Color.White.copy(alpha = 0.25f),
        )

        Text(
            text = "${progressPercent.toInt()}% Complete",
            color = Color.White.copy(alpha = 0.9f),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(top = 10.dp)
        )
    }
}
