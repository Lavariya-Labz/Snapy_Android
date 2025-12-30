// File: com/lavariyalabs/snapy/android/ui/components/MCQAnswerButtonsSection.kt

package com.lavariyalabs.snapy.android.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.scale
import androidx.compose.animation.core.animateFloatAsState
import com.lavariyalabs.snapy.android.data.model.QuizOption

/**
 * MCQAnswerButtonsSection - Multiple choice answer options
 *
 * Features:
 * - 4 answer options (A, B, C, D)
 * - Color feedback (green for correct, red for incorrect)
 * - Disabled after selection
 * - Shows feedback icons (✓ or ✗)
 */
@Composable
fun MCQAnswerButtonsSection(
    options: List<QuizOption>,
    onOptionSelected: (option: QuizOption) -> Unit,
    isAnswered: Boolean = false
) {

    val selectedOptionId = remember { mutableStateOf<Long?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        options.sortedBy { it.orderIndex }.forEach { option ->
            MCQOptionButton(
                option = option,
                isSelected = selectedOptionId.value == option.id,
                isAnswered = isAnswered,
                onClick = {
                    selectedOptionId.value = option.id
                    onOptionSelected(option)
                }
            )
        }
    }
}

/**
 * MCQOptionButton - Individual MCQ option button
 */
@Composable
fun MCQOptionButton(
    option: QuizOption,
    isSelected: Boolean,
    isAnswered: Boolean,
    onClick: () -> Unit
) {

    val backgroundColor = if (!isAnswered) {
        if (isSelected) Color(0xFF3B82F6) else Color(0xFFEBF3FF)
    } else {
        when {
            option.isCorrect -> Color(0xFF10B981)
            isSelected -> Color(0xFFEF4444)
            else -> Color(0xFFEBF3FF)
        }
    }

    val textColor = if (!isAnswered) {
        if (isSelected) Color.White else Color(0xFF1F2937)
    } else {
        when {
            option.isCorrect -> Color.White
            isSelected -> Color.White
            else -> Color(0xFF1F2937)
        }
    }

    val interactionSource = remember { MutableInteractionSource() }
    var isPressed by remember { mutableStateOf(false) }

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is PressInteraction.Press -> isPressed = true
                is PressInteraction.Release -> isPressed = false
                is PressInteraction.Cancel -> isPressed = false
                else -> {}
            }
        }
    }

    val animatedColor: Color by animateColorAsState(
        targetValue = backgroundColor,
        animationSpec = tween(durationMillis = 300),
        label = "mcqOptionColor"
    )

    val scale: Float by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = tween(durationMillis = 200),
        label = "mcqOptionScale"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = animatedColor,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = !isAnswered
            ) { onClick() }
            .padding(vertical = 12.dp, horizontal = 16.dp)
            .scale(scale),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(
                        color = Color.White.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = option.optionLetter,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
            }

            Text(
                text = option.optionText,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = textColor,
                modifier = Modifier
                    .padding(start = 12.dp)
                    .weight(1f)
            )

            if (isAnswered) {
                Text(
                    text = if (isSelected && !option.isCorrect) "✗"
                    else if (option.isCorrect) "✓"
                    else "",
                    fontSize = 18.sp,
                    color = textColor,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}
