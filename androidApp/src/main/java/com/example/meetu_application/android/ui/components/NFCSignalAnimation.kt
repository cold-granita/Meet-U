package com.example.meetu_application.android.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.meetu_application.android.ui.theme.colorMeetU

@Composable
fun NFCSignalAnimation(modifier: Modifier = Modifier, color: Color = colorMeetU) {
    val infiniteTransition = rememberInfiniteTransition(label = "walletWave")

    val wave1Scale by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave1"
    )

    val wave2Scale by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, delayMillis = 500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave2"
    )

    val wave3Scale by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, delayMillis = 800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave3"
    )

    Canvas(modifier = modifier.size(64.dp)) {
        val center = Offset(size.width / 2, size.height / 2)
        val baseRadius = size.minDimension / 6

        listOf(wave1Scale, wave2Scale, wave3Scale).forEachIndexed { index, scale ->
            val alpha = 1f - scale
            drawArc(
                color = color.copy(alpha = alpha.coerceIn(0f, 1f)),
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset(
                    center.x - baseRadius * scale,
                    center.y - baseRadius * scale
                ),
                size = Size(
                    baseRadius * 2 * scale,
                    baseRadius * 2 * scale
                ),
                style = Stroke(width = 3f)
            )
        }
    }
}
