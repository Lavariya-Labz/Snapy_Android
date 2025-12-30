package com.lavariyalabs.snapy.android.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * ContinueButton - Reusable button for onboarding
 *
 * Features:
 * - Color changes based on enabled state
 * - Smooth animation
 * - Disabled when no selection made
 */
@Composable
fun ContinueButton(
    isEnabled: Boolean,
    onClick: () -> Unit
) {

    val backgroundColor: Color by animateColorAsState(
        targetValue = if (isEnabled) Color(0xFF6366F1) else Color(0xFFCBD5E1),
        animationSpec = tween(durationMillis = 300),
        label = "continueButtonColor"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(enabled = isEnabled) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Continue",
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp
        )
    }
}
