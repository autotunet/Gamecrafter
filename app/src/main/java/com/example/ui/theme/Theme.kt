package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = ButtonPurple,
    onPrimary = Color.White,
    primaryContainer = ButtonPurpleDark,
    secondary = BlockCyan,
    onSecondary = Color.Black,
    tertiary = BlockMagenta,
    background = NeonBg,
    surface = NeonCardBg,
    onBackground = TextLight,
    onSurface = TextLight
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
