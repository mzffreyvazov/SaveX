package com.example.savex.ui.theme

import android.os.Build
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Teal500,
    onPrimary = White,
    secondary = Coral500,
    background = Slate900,
    surface = Slate700,
    surfaceVariant = Slate700,
    onSurface = White,
    onSurfaceVariant = Slate100,
)

private val LightColorScheme = lightColorScheme(
    primary = Teal500,
    onPrimary = White,
    primaryContainer = Color(0xFFD8F3EE),
    onPrimaryContainer = Teal700,
    secondary = Coral500,
    background = Slate050,
    onBackground = Slate900,
    surface = White,
    onSurface = Slate900,
    surfaceVariant = Color(0xFFF0F4F8),
    onSurfaceVariant = Slate500,
    outline = Slate100,
)

@Composable
fun SaveXTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
