package com.example.taskmanager111.ui.theme.themefiles

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.edit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Stable
class ThemeManager(context: Context) {
    private val sharedPrefs = context.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)

    private val _themeState = MutableStateFlow(
        ThemeState(
            isDarkTheme = sharedPrefs.getBoolean("is_dark_theme", false),
            useSystemTheme = sharedPrefs.getBoolean("use_system_theme", true),
            useDynamicColor = sharedPrefs.getBoolean("use_dynamic_color", true),
            customColor = try {
                // Try to read as Long first (new format)
                Color(sharedPrefs.getLong("custom_color", 0xFF6750A4))
            } catch (e: ClassCastException) {
                // Fallback to reading as Int (legacy format) if Long fails
                val colorInt = sharedPrefs.getInt("custom_color", 0xFF6750A4.toInt())
                // Convert to proper Long format and save it for future
                sharedPrefs.edit { putLong("custom_color", colorInt.toLong()) }
                Color(colorInt.toLong())
            }
        )
    )

    val themeState: StateFlow<ThemeState> = _themeState

    fun setDarkTheme(enabled: Boolean) {
        sharedPrefs.edit {
            putBoolean("is_dark_theme", enabled)
            putBoolean("use_system_theme", false)
        }
        _themeState.value = _themeState.value.copy(
            isDarkTheme = enabled,
            useSystemTheme = false
        )
    }

    fun setSystemTheme(enabled: Boolean) {
        sharedPrefs.edit { putBoolean("use_system_theme", enabled) }
        _themeState.value = _themeState.value.copy(useSystemTheme = enabled)
    }

    fun setDynamicColor(enabled: Boolean) {
        sharedPrefs.edit { putBoolean("use_dynamic_color", enabled) }
        _themeState.value = _themeState.value.copy(useDynamicColor = enabled)
    }

    fun setCustomColor(color: Color) {
        sharedPrefs.edit { putLong("custom_color", color.value.toLong()) }
        _themeState.value = _themeState.value.copy(customColor = color)
    }

    fun isDynamicColorAvailable(): Boolean {
        return android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S
    }
}

data class ThemeState(
    val isDarkTheme: Boolean,
    val useSystemTheme: Boolean,
    val useDynamicColor: Boolean,
    val customColor: Color
)

val LocalThemeManager = staticCompositionLocalOf<ThemeManager> {
    error("No ThemeManager provided")
}

@Composable
fun rememberThemeManager(): ThemeManager {
    val context = LocalContext.current
    return remember { ThemeManager(context) }
}