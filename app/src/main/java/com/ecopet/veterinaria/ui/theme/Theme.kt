package com.ecopet.veterinaria.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val EcoColorScheme = lightColorScheme(
    primary = EcoPrimary,
    onPrimary = Color.White,
    primaryContainer = EcoMint,
    onPrimaryContainer = EcoForest,
    secondary = EcoLeaf,
    onSecondary = Color.White,
    secondaryContainer = EcoMint,
    onSecondaryContainer = EcoForest,
    tertiary = EcoSand,
    background = EcoSky,
    onBackground = EcoForest,
    surface = Color.White,
    onSurface = EcoForest,
    surfaceVariant = EcoMint,
    onSurfaceVariant = EcoMuted,
    outline = EcoOutline,
    error = EcoError,
    surfaceTint = EcoPrimary,
    // Los diálogos (calendario y reloj) usan estos tonos: se mantienen verdes.
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color(0xFFF4FAF6),
    surfaceContainer = Color(0xFFEEF7F1),
    surfaceContainerHigh = Color(0xFFE8F4EC),
    surfaceContainerHighest = EcoMint
)

@Composable
fun EcoPetTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = EcoColorScheme,
        typography = EcoTypography,
        content = content
    )
}
