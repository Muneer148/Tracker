package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = AccentBlue,
    background = DeepBlue,
    surface = SlateGray,
    onPrimary = DeepBlue,
    onBackground = TextHighContrast,
    onSurface = TextHighContrast,
    surfaceVariant = LightSlate,
    secondary = NeonIndigo,
    onSecondary = DeepBlue,
    secondaryContainer = SlateGray,
    onSecondaryContainer = TextHighContrast,
    tertiary = BrightPurple,
    onTertiary = DeepBlue,
    outline = LightSlate,
    outlineVariant = SlateGray,
    error = RoseDanger,
    onError = DeepBlue
)

@Composable
fun GatePrepTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    GatePrepTheme(content = content)
}

