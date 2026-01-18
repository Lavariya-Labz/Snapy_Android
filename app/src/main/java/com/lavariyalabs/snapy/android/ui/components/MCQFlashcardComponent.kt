// File: com/lavariyalabs/snapy/android/ui/components/MCQFlashcardComponent.kt

package com.lavariyalabs.snapy.android.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import com.lavariyalabs.snapy.android.data.model.Flashcard

/**
 * MCQFlashcardComponent - Multiple choice flashcard
 *
 * Features:
 * - Static display (no flip)
 * - Shows explanation after answer
 * - Color schemes
 * - Decorative elements
 */
@Composable
fun MCQFlashcardComponent(
    flashcard: Flashcard,
    cardIndex: Int = 0,
    isAnswered: Boolean = false,
    selectedOptionLetter: String? = null
) {

    val scale: Float by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 400),
        label = "mcqCardScale"
    )

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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(color = currentColorScheme.questionColor)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        contentAlignment = Alignment.TopCenter
    ) {

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

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "QUESTION",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.5.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Text(
                text = flashcard.question,
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 32.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize(animationSpec = tween(300))
            )

            if (isAnswered) {
                Spacer(modifier = Modifier.height(20.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = Color.White.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(12.dp)
                ) {
                    Column {
                        Text(
                            text = "Explanation",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Text(
                            text = flashcard.explanation,
                            color = Color.White,
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        )
                    }
                }
            } else {
                Text(
                    text = "Select the correct answer",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 20.dp)
                )
            }
        }
    }
}
