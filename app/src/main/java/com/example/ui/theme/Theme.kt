package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = KarivaCharcoal,
    onPrimary = KarivaSurfaceLight,
    primaryContainer = KarivaGoldContainer,
    onPrimaryContainer = KarivaGoldDark,
    secondary = KarivaGold,
    onSecondary = KarivaSurfaceLight,
    secondaryContainer = KarivaGoldContainer,
    onSecondaryContainer = KarivaCharcoal,
    tertiary = KarivaGoldLight,
    onTertiary = KarivaCharcoal,
    background = KarivaCreamBg,
    onBackground = KarivaTextPrimary,
    surface = KarivaSurfaceLight,
    onSurface = KarivaTextPrimary,
    surfaceVariant = KarivaSurfaceCard,
    onSurfaceVariant = KarivaTextSecondary,
    outline = KarivaBorder,
    error = KarivaAccentRed
)

private val DarkColorScheme = darkColorScheme(
    primary = KarivaGoldLight,
    onPrimary = KarivaCharcoal,
    primaryContainer = KarivaCharcoalSurface,
    onPrimaryContainer = KarivaGoldLight,
    secondary = KarivaGold,
    onSecondary = KarivaCharcoal,
    background = KarivaCharcoal,
    onBackground = KarivaCreamBg,
    surface = KarivaCharcoalSurface,
    onSurface = KarivaCreamBg,
    surfaceVariant = KarivaCharcoalSurface,
    onSurfaceVariant = KarivaTextMuted,
    outline = KarivaCharcoalSurface,
    error = KarivaAccentRed
)

@Composable
fun KarivaTheme(
    darkTheme: Boolean = false, // Default to bright luxury cream aesthetic matching reference
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = colorScheme.background.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
