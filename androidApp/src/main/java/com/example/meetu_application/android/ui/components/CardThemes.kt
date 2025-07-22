package com.example.meetu_application.android.ui.components

import androidx.compose.ui.graphics.Color

data class CardColorTheme(
    val topColor: Color,
    val bottomColor: Color,
    val radialColorsTop: List<Color>,
    val radialColorsBottom: List<Color>
)

val cardColorThemes = listOf(
    CardColorTheme(
        topColor = Color(0xFF5B84FF),
        bottomColor = Color(0xFF67BFFF),
        radialColorsTop = listOf(Color(0xFF3C6DFF), Color(0xFF4791FF)),
        radialColorsBottom = listOf(Color(0xFF4975FD), Color(0xFF5498FF))
    ),
    CardColorTheme(
        topColor = Color(0xFF4CAF50),
        bottomColor = Color(0xFF81C784),
        radialColorsTop = listOf(Color(0xFF008D06), Color(0xFF35B43A)),
        radialColorsBottom = listOf(Color(0xFF007C02), Color(0xFF1FA81F))
    ),
    CardColorTheme(
        topColor = Color(0xFFF44336),
        bottomColor = Color(0xFFE57373),
        radialColorsTop = listOf(Color(0xFFD32F2F), Color(0xFFFF6867)),
        radialColorsBottom = listOf(Color(0xFFC22929), Color(0xFFF56157))
    ),
    CardColorTheme(
        topColor = Color(0xFF9C27B0),
        bottomColor = Color(0xFFBA68C8),
        radialColorsTop = listOf(Color(0xFF7B1FA2), Color(0xFFC150D3)),
        radialColorsBottom = listOf(Color(0xFF7A1BB6), Color(0xFFB624DE))
    )
)
