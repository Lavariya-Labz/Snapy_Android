package com.lavariyalabs.snapy.android.ui.components

// File: com/lavariyalabs/snapy/android/ui/components/AnswerButtonsSection.kt

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.runtime.*
import androidx.compose.ui.draw.scale
import androidx.compose.animation.core.animateFloatAsState

/**
 * AnswerButtonsSection
 *
 * Two action buttons for user to indicate if they knew the answer or not
 * - Left button: "I didn't know" (negative response)
 * - Right button: "I knew" (positive response)
 */
@Composable
fun AnswerButtonsSection(
    onDidntKnow: () -> Unit,   // Callback for "I didn't know" button
    onKnew: () -> Unit          // Callback for "I knew" button
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // ========== "I DIDN'T KNOW" BUTTON ==========
        AnswerButton(
            text = "I didn't know",
            backgroundColor = Color(0xFFEF4444),      // Red
            pressedColor = Color(0xFFDC2626),         // Darker red
            modifier = Modifier.weight(1f),
            onClick = onDidntKnow
        )

        // ========== "I KNEW" BUTTON ==========
        AnswerButton(
            text = "I knew",
            backgroundColor = Color(0xFF10B981),      // Green
            pressedColor = Color(0xFF059669),         // Darker green
            modifier = Modifier.weight(1f),
            onClick = onKnew
        )
    }
}

/**
 * AnswerButton - Reusable button component
 */
@Composable
private fun AnswerButton(
    text: String,
    backgroundColor: Color,
    pressedColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // ========== INTERACTION STATE ==========
    /**
     * MutableInteractionSource tracks user interactions
     * WHY? Detect when button is pressed
     */
    val interactionSource = remember { MutableInteractionSource() }
    var isPressed by remember { mutableStateOf(false) }

    // ========== COLLECT PRESS STATE ==========
    /**
     * LaunchedEffect listens for press/release events
     * isPressed = true when user holds button down
     */
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

    // ========== ANIMATED COLOR ==========
    /**
     * Color smoothly transitions when pressed
     * animateColorAsState (specialized for colors)
     */
    val animatedColor: Color by animateColorAsState(
        targetValue = if (isPressed) pressedColor else backgroundColor,
        animationSpec = tween(durationMillis = 200),
        label = "buttonColor"
    )

    // ========== ANIMATED SCALE ==========
    /**
     * Button scales down slightly when pressed (0.96 = 4% smaller)
     * Creates tactile feedback sensation
     */
    val scale: Float by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = tween(durationMillis = 200),
        label = "buttonScale"
    )

    // ========== BUTTON UI ==========
    Box(
        modifier = modifier
            .background(
                color = animatedColor,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null  // Disable default ripple (using custom animations)
            ) { onClick() }
            .padding(vertical = 14.dp, horizontal = 16.dp)
            .scale(scale),  // Apply scale animation
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
