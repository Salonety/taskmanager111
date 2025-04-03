package com.example.taskmanager111.ui.theme.themefiles

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

@Composable
fun TaskManagerTheme(
    darkTheme: Boolean? = null,  // null means use system default
    content: @Composable () -> Unit
) {
    val themeManager = rememberThemeManager()
    val themeState by themeManager.themeState.collectAsState()

    val isDarkTheme = when {
        darkTheme != null -> darkTheme
        themeState.useSystemTheme -> isSystemInDarkTheme()
        else -> themeState.isDarkTheme
    }

    val colorScheme = when {
        themeState.useDynamicColor && themeManager.isDynamicColorAvailable() -> {
            val context = LocalContext.current
            if (isDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        isDarkTheme -> DarkColors.copy(
            primary = themeState.customColor,
            secondary = themeState.customColor.copy(alpha = 0.8f),
            tertiary = themeState.customColor.copy(alpha = 0.6f)
        )
        else -> LightColors.copy(
            primary = themeState.customColor,
            secondary = themeState.customColor.copy(alpha = 0.8f),
            tertiary = themeState.customColor.copy(alpha = 0.6f)
        )
    }

    CompositionLocalProvider(LocalThemeManager provides themeManager) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

private val LightColors = lightColorScheme(
    primary = Color(0xFF6750A4),
    secondary = Color(0xFF625B71),
    tertiary = Color(0xFF7D5260)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFD0BCFF),
    secondary = Color(0xFFCCC2DC),
    tertiary = Color(0xFFEFB8C8)
)