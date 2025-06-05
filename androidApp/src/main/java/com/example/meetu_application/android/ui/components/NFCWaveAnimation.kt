package com.example.meetu_application.android.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun NfcWaveAnimation(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), // meno impattante
    maxRadius: Dp = 80.dp,
    waveCount: Int = 2,
    durationMillis: Int = 4000
) {
    val infiniteTransition = rememberInfiniteTransition(label = "nfc_wave")
    val animations = List(waveCount) { index ->
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = durationMillis,
                    easing = androidx.compose.animation.core.FastOutSlowInEasing
                ),
                repeatMode = RepeatMode.Restart,
                initialStartOffset = StartOffset((durationMillis / waveCount) * index)
            ),
            label = "wave_$index"
        )
    }

    val radiusPx = with(LocalDensity.current) { maxRadius.toPx() }

    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = modifier
            .size(maxRadius * 2)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            animations.forEach { anim ->
                val alpha = (1f - anim.value) * 0.5f // più trasparente
                drawCircle(
                    color = color.copy(alpha = alpha),
                    radius = radiusPx * anim.value,
                    center = center
                )
            }
        }
    }
}
