package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = GoalNeonGreen,
    onPrimary = Color(0xFF003918),
    primaryContainer = Color(0xFF005327),
    onPrimaryContainer = Color(0xFF6CFFA0),
    secondary = TrophyGold,
    onSecondary = Color(0xFF422C00),
    secondaryContainer = Color(0xFF5F4100),
    onSecondaryContainer = Color(0xFFFFE088),
    tertiary = ElectricCyan,
    onTertiary = Color(0xFF00363F),
    background = StadiumPitchDark,
    onBackground = TextPrimary,
    surface = StadiumSurface,
    onSurface = TextPrimary,
    surfaceVariant = StadiumSurfaceElevated,
    onSurfaceVariant = TextSecondary,
    outline = StadiumBorder
)

private val LightColorScheme = DarkColorScheme // Always use immersive dark stadium mode for optimal scoreboard contrast

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep consistent stadium theme
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
