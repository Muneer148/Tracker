package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val TrackerLightColors = lightColorScheme(
    primary = AppPrimary,
    onPrimary = Color.White,
    primaryContainer = AppPrimarySoft,
    onPrimaryContainer = AppInk,
    secondary = AppMint,
    onSecondary = Color.White,
    secondaryContainer = AppMintSoft,
    onSecondaryContainer = AppInk,
    tertiary = AppAmber,
    onTertiary = AppInk,
    tertiaryContainer = AppAmberSoft,
    onTertiaryContainer = AppInk,
    background = AppBackground,
    onBackground = AppInk,
    surface = AppSurface,
    onSurface = AppInk,
    surfaceVariant = AppSurfaceAlt,
    onSurfaceVariant = AppMuted,
    outline = AppBorder,
    outlineVariant = AppBorder,
    error = AppRose,
    onError = Color.White,
    errorContainer = AppRoseSoft,
    onErrorContainer = AppInk
)

private val TrackerDarkColors = darkColorScheme(
    primary = Color(0xFF9A8FFF),
    onPrimary = DeepBlue,
    primaryContainer = Color(0xFF332B6E),
    onPrimaryContainer = Color(0xFFECE9FF),
    secondary = Color(0xFF54D9B2),
    onSecondary = DeepBlue,
    secondaryContainer = Color(0xFF164E40),
    onSecondaryContainer = Color(0xFFD8F9EE),
    tertiary = Color(0xFFFFC66B),
    onTertiary = DeepBlue,
    background = DeepBlue,
    onBackground = TextHighContrast,
    surface = Slate900,
    onSurface = TextHighContrast,
    surfaceVariant = Slate850,
    onSurfaceVariant = TextMuted,
    outline = Slate700,
    outlineVariant = Slate800,
    error = Color(0xFFFF8095),
    onError = DeepBlue
)

@Composable
fun GatePrepTheme(content: @Composable () -> Unit) {
    val colors = if (isSystemInDarkTheme()) TrackerDarkColors else TrackerLightColors
    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    GatePrepTheme(content = content)
}
