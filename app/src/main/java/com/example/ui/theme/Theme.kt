package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = AccentBlue,
    onPrimary = DeepBlue,
    primaryContainer = Slate800,
    onPrimaryContainer = TextHighContrast,
    secondary = NeonIndigo,
    onSecondary = DeepBlue,
    secondaryContainer = Slate800,
    onSecondaryContainer = TextHighContrast,
    tertiary = BrightPurple,
    onTertiary = Color.White,
    background = Slate950,
    onBackground = TextHighContrast,
    surface = Slate900,
    onSurface = TextHighContrast,
    surfaceVariant = Slate800,
    onSurfaceVariant = Slate300,
    outline = Slate600,
    outlineVariant = Slate700,
    error = RoseDanger,
    onError = DeepBlue
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF4549B8),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE5E7FF),
    onPrimaryContainer = Color(0xFF191B54),
    secondary = Color(0xFF5C4A9C),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFEDE7FF),
    onSecondaryContainer = Color(0xFF27165D),
    tertiary = Color(0xFF8A3D95),
    onTertiary = Color.White,
    background = Color(0xFFF7F8FC),
    onBackground = Color(0xFF171A2B),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF171A2B),
    surfaceVariant = Color(0xFFEEF0F7),
    onSurfaceVariant = Color(0xFF50566A),
    outline = Color(0xFF777E91),
    outlineVariant = Color(0xFFD4D8E4),
    error = Color(0xFFB3261E),
    onError = Color.White
)

@Composable
fun GatePrepTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
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
    GatePrepTheme(darkTheme = darkTheme, content = content)
}
