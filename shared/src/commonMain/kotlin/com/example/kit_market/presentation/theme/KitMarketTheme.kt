package com.example.kit_market.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val KitLightColorScheme = lightColorScheme(
    primary = KitBlue,
    onPrimary = KitWhite,
    primaryContainer = KitBlueLight,
    onPrimaryContainer = KitBlueDark,
    secondary = KitBlue,
    onSecondary = KitWhite,
    background = KitWhite,
    onBackground = KitTextPrimary,
    surface = KitWhite,
    onSurface = KitTextPrimary,
    surfaceVariant = KitGrayLight,
    onSurfaceVariant = KitTextSecondary,
    outline = KitGray
)

@Composable
fun KitMarketTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = KitLightColorScheme,
        typography = KitTypography,
        content = content
    )
}
