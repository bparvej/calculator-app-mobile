package com.parvej.simplecalculator.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFF6750A4),
    secondary = androidx.compose.ui.graphics.Color(0xFF625B71),
    background = androidx.compose.ui.graphics.Color(0xFFF5F5F5),
    surface = androidx.compose.ui.graphics.Color(0xFFFFFFFF),
    surfaceVariant = androidx.compose.ui.graphics.Color(0xFFE7E0EC),
    primaryContainer = androidx.compose.ui.graphics.Color(0xFFEADDFF),
    secondaryContainer = androidx.compose.ui.graphics.Color(0xFFE8DEF8),
    onPrimaryContainer = androidx.compose.ui.graphics.Color(0xFF21005D),
    onSecondaryContainer = androidx.compose.ui.graphics.Color(0xFF1D192B),
    onSurfaceVariant = androidx.compose.ui.graphics.Color(0xFF49454F)
)

private val DarkColors = darkColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFFD0BCFF),
    secondary = androidx.compose.ui.graphics.Color(0xFFCCC2DC),
    background = androidx.compose.ui.graphics.Color(0xFF121212),
    surface = androidx.compose.ui.graphics.Color(0xFF1E1E1E),
    surfaceVariant = androidx.compose.ui.graphics.Color(0xFF2B2B2B),
    primaryContainer = androidx.compose.ui.graphics.Color(0xFF4F378B),
    secondaryContainer = androidx.compose.ui.graphics.Color(0xFF3B2F4E),
    onPrimaryContainer = androidx.compose.ui.graphics.Color(0xFFEADDFF),
    onSecondaryContainer = androidx.compose.ui.graphics.Color(0xFFE8DEF8),
    onSurfaceVariant = androidx.compose.ui.graphics.Color(0xFFE6E1E5)
)

@Composable
fun SimpleCalculatorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
