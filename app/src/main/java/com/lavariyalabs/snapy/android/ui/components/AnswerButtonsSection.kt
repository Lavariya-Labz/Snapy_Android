// File: com/lavariyalabs/snapy/android/ui/components/AnswerButtonsSection.kt

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

/**
 * AnswerButtonsSection - Self-evaluation answer buttons
 *
 * Features:
 * - Two buttons: I knew / I didn't know
 * - Press animation and color feedback
 * - Visual indication of selection
 */
@Composable
fun AnswerButtonsSection(
    onDidntKnow: () -> Unit,
    onKnew: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        AnswerButton(
            text = "I didn't know",
            backgroundColor = Color(0xFFEF4444),
            pressedColor = Color(0xFFDC2626),
            modifier = Modifier.weight(1f),
            onClick = onDidntKnow
        )

        AnswerButton(
            text = "I knew",
            backgroundColor = Color(0xFF10B981),
            pressedColor = Color(0xFF059669),
            modifier = Modifier.weight(1f),
            onClick = onKnew
        )
    }
}

/**
 * AnswerButton - Individual answer button
 */
@Composable
private fun AnswerButton(
    text: String,
    backgroundColor: Color,
    pressedColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

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
        targetValue = if (isPressed) pressedColor else backgroundColor,
        animationSpec = tween(durationMillis = 200),
        label = "buttonColor"
    )

    val scale: Float by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = tween(durationMillis = 200),
        label = "buttonScale"
    )

    Box(
        modifier = modifier
            .background(
                color = animatedColor,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() }
            .padding(vertical = 14.dp, horizontal = 16.dp)
            .scale(scale),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
