package com.lavariyalabs.snapy.android.ui.components

// File: com/lavariyalabs/snapy/android/ui/components/FlashcardComponent.kt

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * FlashcardComponent - The main interactive flashcard
 *
 * KEY FEATURES:
 * - Flips between question (front) and answer (back)
 * - Colorful gradient backgrounds
 * - Smooth animation
 * - Clickable to trigger flip
 */
@Composable
fun FlashcardComponent(
    isFlipped: Boolean,         // true = showing answer, false = showing question
    question: String,           // Question text (front side)
    answer: String,             // Answer text (back side)
    onCardClick: () -> Unit,     // Callback when user clicks to flip
    cardIndex: Int = 0  // Used for color variation
) {
    // ========== ANIMATION SETUP ==========
    /**
     * animateFloatAsState():
     * - Smoothly animates from current value to target value
     * - WHY use this?
     *   - Provides smooth transition (not instant jump)
     *   - Handled by Compose, no manual animation code needed
     *   - tween() = linear animation over 500ms
     *
     * rotationY will be:
     * - 0f when isFlipped = false (front side, normal)
     * - 180f when isFlipped = true (back side, rotated)
     */
    val cardRotation: Float by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 500),
        label = "cardFlip"
    )

    // ========== ANIMATION 2: SCALE ANIMATION (Entrance) ==========
    /**
     * Card scales from 0.95 to 1.0 when displayed
     * Makes appearance feel more dynamic
     */
    val scale: Float by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 400),
        label = "cardScale"
    )

    // ========== COLOR GRADIENTS (Multiple options) ==========
    /**
     * WHY multiple colors?
     * - Each card can have different color to feel more colorful
     * - Uses cardIndex to cycle through colors
     * - Creates visual variety
     */
    data class CardColorScheme(
        val questionColor: Color,
        val answerColor: Color
    )

    val colorSchemes = listOf(
        CardColorScheme(
            questionColor = Color(0xFF3B82F6),      // Blue
            answerColor = Color(0xFFEC4899)         // Pink
        ),
        CardColorScheme(
            questionColor = Color(0xFF8B5CF6),      // Purple
            answerColor = Color(0xFF06B6D4)         // Cyan
        ),
        CardColorScheme(
            questionColor = Color(0xFF10B981),      // Green
            answerColor = Color(0xFFF59E0B)         // Amber
        ),
        CardColorScheme(
            questionColor = Color(0xFFEF4444),      // Red
            answerColor = Color(0xFF06B6D4)         // Cyan
        ),
        CardColorScheme(
            questionColor = Color(0xFF6366F1),      // Indigo
            answerColor = Color(0xFFF97316)         // Orange
        )
    )

    val currentColorScheme = colorSchemes[cardIndex % colorSchemes.size]
    val cardBackgroundColor = if (isFlipped)
        currentColorScheme.answerColor
    else
        currentColorScheme.questionColor

    // ========== FLASHCARD UI ==========
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .aspectRatio(0.65f)  // Taller card (width:height = 0.65:1)
            .clip(RoundedCornerShape(24.dp))
            .graphicsLayer {
                // 3D rotation effect - rotate around Y axis
                rotationY = cardRotation
                cameraDistance = 12f * density  // Depth perception

                // ===== SCALE ANIMATION =====
                scaleX = scale
                scaleY = scale
            }
            .background(color = cardBackgroundColor)
            .clickable(enabled = true) { onCardClick() },
        contentAlignment = Alignment.Center
    ) {
        // ========== DECORATIVE ELEMENTS (Visual Appeal) ==========
        /**
         * These decorative corners add sophistication
         * They appear on the card background
         */

        // Top-right corner decoration
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(60.dp)
                .background(
                    color = Color.White.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(bottomStart = 20.dp)
                )
        )

        // Bottom-left corner decoration
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .size(50.dp)
                .background(
                    color = Color.White.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(topEnd = 20.dp)
                )
        )
        // ========== CARD CONTENT ==========
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Side indicator text
            Text(
                text = if (isFlipped) "ANSWER" else "QUESTION",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.5.sp,
                modifier = Modifier
                    .padding(bottom = 12.dp)
                    .graphicsLayer {
                        rotationY = -cardRotation  // Opposite of card rotation
                    }
            )

            // Main content (question or answer)
            Text(
                text = if (isFlipped) answer else question,
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 32.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize(animationSpec = tween(300))
                    .graphicsLayer {
                        rotationY = -cardRotation  // Opposite of card rotation
                    }
            )

            // Hint text
            Text(
                text = "Tap to ${if (isFlipped) "see question" else "see answer"}",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 12.sp,
                modifier = Modifier
                    .padding(top = 20.dp)
                    .graphicsLayer {
                        rotationY = -cardRotation  // Opposite of card rotation
                    }
            )
        }
    }
}
