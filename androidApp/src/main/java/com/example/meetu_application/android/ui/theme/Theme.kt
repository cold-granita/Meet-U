package com.example.meetu_application.android.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val colorMeetU = Color(0xFF0060F1)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        darkColorScheme(
            primary = Color(0xFF86D7FC),
            secondary = Color(0xFF0364DA),
            tertiary = Color(0xFF0057B3)
        )
    } else {
        lightColorScheme(
            primary = Color(0xFF009BEE),
            secondary = Color(0xFF0339DA),
            tertiary = Color(0xFF030085)
        )
    }

    MaterialTheme(
        colorScheme = colors,
        typography = Typography(
            bodyMedium = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp)
        ),
        shapes = Shapes(
            small = RoundedCornerShape(4.dp),
            medium = RoundedCornerShape(4.dp),
            large = RoundedCornerShape(0.dp)
        ),
        content = content
    )
}
