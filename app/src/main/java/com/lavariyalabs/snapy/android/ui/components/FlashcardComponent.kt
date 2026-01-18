// File: com/lavariyalabs/snapy/android/ui/components/FlashcardComponent.kt

package com.lavariyalabs.snapy.android.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.lavariyalabs.snapy.android.utils.SoundManager

/**
 * FlashcardComponent - Self-evaluation flashcard with flip animation
 *
 * Features:
 * - Card flip animation (3D effect)
 * - Text counter-rotation (readable on both sides)
 * - Color schemes based on card index
 * - Decorative elements
 */
@Composable
fun FlashcardComponent(
    isFlipped: Boolean,
    question: String,
    answer: String,
    onCardClick: () -> Unit,
    cardIndex: Int = 0
) {

    // ========== ANIMATION: CARD ROTATION ==========
    val cardRotation: Float by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 500),
        label = "cardFlip"
    )

    // ========== ANIMATION: SCALE ==========
    val scale: Float by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 400),
        label = "cardScale"
    )

    // ========== COLOR SCHEMES ==========
    data class CardColorScheme(
        val questionColor: Color,
        val answerColor: Color
    )

    val colorSchemes = listOf(
        CardColorScheme(Color(0xFF3B82F6), Color(0xFFEC4899)),
        CardColorScheme(Color(0xFF8B5CF6), Color(0xFF06B6D4)),
        CardColorScheme(Color(0xFF10B981), Color(0xFFF59E0B)),
        CardColorScheme(Color(0xFFEF4444), Color(0xFF06B6D4)),
        CardColorScheme(Color(0xFF6366F1), Color(0xFFF97316))
    )

    val currentColorScheme = colorSchemes[cardIndex % colorSchemes.size]
    val cardBackgroundColor = if (isFlipped)
        currentColorScheme.answerColor
    else
        currentColorScheme.questionColor

    // ========== MAIN CARD ==========
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .clip(RoundedCornerShape(24.dp))
            .graphicsLayer {
                rotationY = cardRotation
                cameraDistance = 12f * density
                scaleX = scale
                scaleY = scale
            }
            .background(color = cardBackgroundColor)
            .clickable(enabled = true) { 
                SoundManager.playClickSound()
                onCardClick() 
            },
        contentAlignment = Alignment.Center
    ) {

        // ========== DECORATIVE ELEMENTS ==========
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(60.dp)
                .background(
                    color = Color.White.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(bottomStart = 20.dp)
                )
        )

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

            // Side indicator
            Text(
                text = if (isFlipped) "ANSWER" else "QUESTION",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.5.sp,
                modifier = Modifier
                    .padding(bottom = 12.dp)
                    .graphicsLayer {
                        rotationY = -cardRotation
                    }
            )

            // Main text
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
                        rotationY = -cardRotation
                    }
            )

            // Hint text
            Text(
                text = if (isFlipped) "Tap to see question" else "Tap to see answer",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 12.sp,
                modifier = Modifier
                    .padding(top = 20.dp)
                    .graphicsLayer {
                        rotationY = -cardRotation
                    }
            )
        }
    }
}
