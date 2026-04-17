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
    primary = PocketTeal,
    onPrimary = BgCard,
    primaryContainer = PocketTealDark,
    onPrimaryContainer = BgCard,
    secondary = PocketCoral,
    onSecondary = BgCard,
    tertiary = StarYellow,
    onTertiary = TextMain,
    background = Color(0xFF101517),
    onBackground = BgCard,
    surface = Color(0xFF172026),
    onSurface = BgCard,
    surfaceVariant = Color(0xFF273039),
    onSurfaceVariant = Color(0xFFB8C0C7),
    outline = Color(0xFF3B4752),
    error = DangerRed,
    scrim = Color(0x66000000),
)

private val LightColorScheme = lightColorScheme(
    primary = PocketTeal,
    onPrimary = BgCard,
    primaryContainer = PocketTealSoft,
    onPrimaryContainer = PocketTealDark,
    secondary = PocketCoral,
    onSecondary = BgCard,
    secondaryContainer = SegmentedActiveBg,
    onSecondaryContainer = SegmentedActiveText,
    tertiary = StarYellow,
    onTertiary = TextMain,
    background = BgApp,
    onBackground = TextMain,
    surface = BgCard,
    onSurface = TextMain,
    surfaceVariant = SearchSurface,
    onSurfaceVariant = TextMuted,
    outline = BorderLight,
    outlineVariant = TextSubtle,
    error = DangerRed,
    scrim = Color(0x66000000),
)

@Composable
fun SaveXTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
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
