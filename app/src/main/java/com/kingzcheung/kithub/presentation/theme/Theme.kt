package com.kingzcheung.kithub.presentation.theme

import android.app.Activity
import android.content.Context
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

enum class ThemeColor(val displayName: String, val seedColor: Color) {
    DYNAMIC("Dynamic (Material You)", Color.Unspecified),
    GREEN("GitHub Green", Color(0xFF2EA043)),
    BLUE("Ocean Blue", Color(0xFF2563EB)),
    PURPLE("Purple", Color(0xFF7C3AED)),
    RED("Red", Color(0xFFDC2626)),
    TEAL("Teal", Color(0xFF0891B2)),
    ORANGE("Orange", Color(0xFFEA580C)),
    PINK("Pink", Color(0xFFDB2777))
}

private val GreenLightColorScheme = lightColorScheme(
    primary = Color(0xFF2EA043),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFC8F7D0),
    onPrimaryContainer = Color(0xFF002108),
    secondary = Color(0xFF4F6352),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD1E8D3),
    onSecondaryContainer = Color(0xFF0D1F12),
    tertiary = Color(0xFF3D6373),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFC1E8FB),
    onTertiaryContainer = Color(0xFF001F29),
    error = Color(0xFFBA1A1A),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    background = Color(0xFFF5F5F5),
    onBackground = Color(0xFF1A1C1E),
    surface = Color.White,
    onSurface = Color(0xFF1A1C1E),
    surfaceVariant = Color.White,
    onSurfaceVariant = Color(0xFF42474E),
    outline = Color(0xFF72777F),
    outlineVariant = Color(0xFFC2C6CF),
    inverseSurface = Color(0xFF2F3033),
    inverseOnSurface = Color(0xFFF1F0F4),
    inversePrimary = Color(0xFF9FD6A6),
    surfaceTint = Color(0xFF2EA043),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFF9F9F9),
    surfaceContainer = Color(0xFFF3F3F3),
    surfaceContainerHigh = Color(0xFFEDEDED),
    surfaceContainerHighest = Color(0xFFE7E7E7)
)

private val GreenDarkColorScheme = darkColorScheme(
    primary = Color(0xFF9FD6A6),
    onPrimary = Color(0xFF003914),
    primaryContainer = Color(0xFF00531F),
    onPrimaryContainer = Color(0xFFC8F7D0),
    secondary = Color(0xFFB5CCB8),
    onSecondary = Color(0xFF213528),
    secondaryContainer = Color(0xFF374B3D),
    onSecondaryContainer = Color(0xFFD1E8D3),
    tertiary = Color(0xFFA5CCE0),
    onTertiary = Color(0xFF073543),
    tertiaryContainer = Color(0xFF244C5A),
    onTertiaryContainer = Color(0xFFC1E8FB),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF121212),
    onBackground = Color(0xFFE3E2E6),
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFE3E2E6),
    surfaceVariant = Color(0xFF1F1F1F),
    onSurfaceVariant = Color(0xFFC2C6CF),
    outline = Color(0xFF8C9199),
    outlineVariant = Color(0xFF42474E),
    inverseSurface = Color(0xFFE3E2E6),
    inverseOnSurface = Color(0xFF1A1C1E),
    inversePrimary = Color(0xFF2EA043),
    surfaceTint = Color(0xFF9FD6A6),
    surfaceContainerLowest = Color(0xFF0F0F0F),
    surfaceContainerLow = Color(0xFF1A1A1A),
    surfaceContainer = Color(0xFF1E1E1E),
    surfaceContainerHigh = Color(0xFF232323),
    surfaceContainerHighest = Color(0xFF282828)
)

@Composable
fun KithubTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    themeMode: com.kingzcheung.kithub.data.store.ThemeMode = com.kingzcheung.kithub.data.store.ThemeMode.SYSTEM,
    themeColor: ThemeColor = ThemeColor.GREEN,
    content: @Composable () -> Unit
) {
    val useDarkTheme = when (themeMode) {
        com.kingzcheung.kithub.data.store.ThemeMode.LIGHT -> false
        com.kingzcheung.kithub.data.store.ThemeMode.DARK -> true
        com.kingzcheung.kithub.data.store.ThemeMode.SYSTEM -> darkTheme
    }
    
    val context = LocalContext.current
    val colorScheme = when {
        themeColor == ThemeColor.DYNAMIC && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (useDarkTheme) dynamicDarkColorScheme(context) 
            else dynamicLightColorScheme(context)
        }
        themeColor == ThemeColor.GREEN -> {
            if (useDarkTheme) GreenDarkColorScheme else GreenLightColorScheme
        }
        else -> {
            generateColorSchemeFromSeed(themeColor.seedColor, useDarkTheme)
        }
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !useDarkTheme
        }
    }
    
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}

private fun generateColorSchemeFromSeed(seedColor: Color, darkTheme: Boolean): androidx.compose.material3.ColorScheme {
    return if (darkTheme) {
        darkColorScheme(
            primary = seedColor.copy(alpha = 0.8f),
            onPrimary = Color.Black,
            primaryContainer = seedColor.copy(alpha = 0.3f),
            onPrimaryContainer = seedColor,
            secondary = seedColor.copy(alpha = 0.6f),
            onSecondary = Color.Black,
            secondaryContainer = seedColor.copy(alpha = 0.2f),
            onSecondaryContainer = seedColor,
            tertiary = seedColor.copy(alpha = 0.5f),
            onTertiary = Color.Black,
            tertiaryContainer = seedColor.copy(alpha = 0.15f),
            onTertiaryContainer = seedColor,
            error = Color(0xFFFFB4AB),
            onError = Color(0xFF690005),
            errorContainer = Color(0xFF93000A),
            onErrorContainer = Color(0xFFFFDAD6),
            background = Color(0xFF121212),
            onBackground = Color(0xFFE3E2E6),
            surface = Color(0xFF1E1E1E),
            onSurface = Color(0xFFE3E2E6),
            surfaceVariant = Color(0xFF1F1F1F),
            onSurfaceVariant = Color(0xFFC2C6CF),
            outline = Color(0xFF8C9199),
            outlineVariant = Color(0xFF42474E),
            inverseSurface = Color(0xFFE3E2E6),
            inverseOnSurface = Color(0xFF1A1C1E),
            inversePrimary = seedColor,
            surfaceTint = seedColor.copy(alpha = 0.8f),
            surfaceContainerLowest = Color(0xFF0F0F0F),
            surfaceContainerLow = Color(0xFF1A1A1A),
            surfaceContainer = Color(0xFF1E1E1E),
            surfaceContainerHigh = Color(0xFF232323),
            surfaceContainerHighest = Color(0xFF282828)
        )
    } else {
        lightColorScheme(
            primary = seedColor,
            onPrimary = Color.White,
            primaryContainer = seedColor.copy(alpha = 0.15f),
            onPrimaryContainer = seedColor,
            secondary = seedColor.copy(alpha = 0.7f),
            onSecondary = Color.White,
            secondaryContainer = seedColor.copy(alpha = 0.1f),
            onSecondaryContainer = seedColor,
            tertiary = seedColor.copy(alpha = 0.5f),
            onTertiary = Color.White,
            tertiaryContainer = seedColor.copy(alpha = 0.08f),
            onTertiaryContainer = seedColor,
            error = Color(0xFFBA1A1A),
            onError = Color.White,
            errorContainer = Color(0xFFFFDAD6),
            onErrorContainer = Color(0xFF410002),
            background = Color(0xFFF5F5F5),
            onBackground = Color(0xFF1A1C1E),
            surface = Color.White,
            onSurface = Color(0xFF1A1C1E),
            surfaceVariant = Color.White,
            onSurfaceVariant = Color(0xFF42474E),
            outline = Color(0xFF72777F),
            outlineVariant = Color(0xFFC2C6CF),
            inverseSurface = Color(0xFF2F3033),
            inverseOnSurface = Color(0xFFF1F0F4),
            inversePrimary = seedColor.copy(alpha = 0.8f),
            surfaceTint = seedColor,
            surfaceContainerLowest = Color(0xFFFFFFFF),
            surfaceContainerLow = Color(0xFFF9F9F9),
            surfaceContainer = Color(0xFFF3F3F3),
            surfaceContainerHigh = Color(0xFFEDEDED),
            surfaceContainerHighest = Color(0xFFE7E7E7)
        )
    }
}