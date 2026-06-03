package com.mensis.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope

/**
 * Stylized fetus drawn with Canvas. The figure grows with [growth] (0..1, from
 * BabyDevelopment.growthFraction), giving a sense of week-by-week development —
 * a lightweight stand-in for a 3D model, fully offline.
 */
@Composable
fun BabyVisual(
    growth: Float,
    modifier: Modifier = Modifier,
    womb: Color = MaterialTheme.colorScheme.primaryContainer,
    skin: Color = MaterialTheme.colorScheme.primary,
    eye: Color = MaterialTheme.colorScheme.onPrimary
) {
    val animated by animateFloatAsState(
        targetValue = growth.coerceIn(0.04f, 1f),
        animationSpec = tween(700),
        label = "babyGrowth"
    )
    Canvas(modifier) {
        val w = size.minDimension
        val c = Offset(size.width / 2f, size.height / 2f)
        val innerR = w * 0.47f

        // Womb (soft radial halo)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(womb.copy(alpha = 0.95f), womb.copy(alpha = 0.35f)),
                center = c,
                radius = innerR
            ),
            radius = innerR,
            center = c
        )

        drawFetus(center = c, unit = w * (0.16f + 0.27f * animated), skin = skin, eye = eye)
    }
}

private fun DrawScope.drawFetus(center: Offset, unit: Float, skin: Color, eye: Color) {
    val s = unit
    // Torso (largest blob)
    val torso = Offset(center.x + s * 0.25f, center.y + s * 0.45f)
    drawCircle(skin, radius = s * 1.15f, center = torso)
    // Bent legs / lower body
    drawCircle(skin, radius = s * 0.8f, center = Offset(center.x - s * 0.35f, center.y + s * 1.05f))
    // Head
    val headCenter = Offset(center.x - s * 0.25f, center.y - s * 0.95f)
    val headR = s * 0.92f
    drawCircle(skin, radius = headR, center = headCenter)
    // Cheek highlight
    drawCircle(skin.copy(alpha = 0.6f), radius = s * 0.28f, center = Offset(headCenter.x - headR * 0.5f, headCenter.y + headR * 0.35f))
    // Eye
    drawCircle(eye, radius = s * 0.13f, center = Offset(headCenter.x - headR * 0.35f, headCenter.y))
}
