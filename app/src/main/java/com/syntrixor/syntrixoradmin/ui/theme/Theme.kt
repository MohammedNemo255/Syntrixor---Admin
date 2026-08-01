package com.syntrixor.syntrixoradmin.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.core.view.WindowCompat
import com.syntrixor.syntrixoradmin.utils.LocaleHelper
import com.syntrixor.syntrixoradmin.utils.ThemeManager

private val SyntrixorLightColorScheme = lightColorScheme(
    primary = Violet600,
    onPrimary = White,
    primaryContainer = Violet400,
    onPrimaryContainer = Navy,
    secondary = Slate500,
    onSecondary = White,
    background = SlateLight,
    onBackground = TextPrimary,
    surface = White,
    onSurface = TextPrimary,
    surfaceVariant = Slate100,
    onSurfaceVariant = TextSecondary,
    outline = Border,
    error = Error,
    onError = White,
)

private val SyntrixorDarkColorScheme = darkColorScheme(
    primary = Violet600,
    onPrimary = White,
    primaryContainer = Violet500,
    onPrimaryContainer = White,
    secondary = Violet400,
    onSecondary = Navy,
    background = Color(0xFF0F172A),
    onBackground = Color(0xFFF1F5F9),
    surface = Color(0xFF1E293B),
    onSurface = Color(0xFFF1F5F9),
    surfaceVariant = Color(0xFF334155),
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = Color(0xFF475569),
    error = Error,
    onError = White,
)

@Composable
fun SyntrixorAdminTheme(content: @Composable () -> Unit) {
    val context = LocalContext.current
    val view = LocalView.current
    val isDarkMode by ThemeManager.isDarkMode

    val layoutDirection = if (LocaleHelper.getLanguage(context) == "ar")
        LayoutDirection.Rtl else LayoutDirection.Ltr

    val colorScheme = if (isDarkMode) SyntrixorDarkColorScheme else SyntrixorLightColorScheme

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = !isDarkMode
            controller.isAppearanceLightNavigationBars = !isDarkMode
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

/**
 * Preview-only theme wrapper that bypasses the [ThemeManager] singleton so
 * @Preview functions can force a genuine light/dark pair regardless of the
 * app's persisted theme preference.
 */
@Composable
fun SyntrixorAdminPreviewTheme(darkTheme: Boolean, content: @Composable () -> Unit) {
    val colorScheme = if (darkTheme) SyntrixorDarkColorScheme else SyntrixorLightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
