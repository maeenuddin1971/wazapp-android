package com.maeen.mahfilhub.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = PrimaryTeal,
    onPrimary = Color.White,
    primaryContainer = PrimaryTealLight,
    onPrimaryContainer = PrimaryTealDark,
    
    secondary = SecondaryGreen,
    onSecondary = Color.White,
    secondaryContainer = SecondaryGreen,
    onSecondaryContainer = SecondaryGreenDark,
    
    tertiary = AccentOrange,
    onTertiary = Color.White,
    tertiaryContainer = AccentOrangeLight,
    onTertiaryContainer = AccentOrange,
    
    background = BackgroundCream,
    onBackground = TextPrimary,
    
    surface = SurfaceWhite,
    onSurface = TextPrimary,
    surfaceVariant = BackgroundCream,
    onSurfaceVariant = TextSecondary,
    
    error = ErrorRed,
    onError = Color.White,
    
    outline = DividerLight,
    outlineVariant = DividerLight.copy(alpha = 0.5f)
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryTealDarkMode,
    onPrimary = Color.Black,
    primaryContainer = PrimaryTealDarkModeVariant,
    onPrimaryContainer = PrimaryTealDarkMode,
    
    secondary = SecondaryGreen,
    onSecondary = Color.Black,
    secondaryContainer = SecondaryGreenDark,
    onSecondaryContainer = SecondaryGreen,
    
    tertiary = AccentOrange,
    onTertiary = Color.Black,
    tertiaryContainer = AccentOrangeLight,
    onTertiaryContainer = AccentOrange,
    
    background = BackgroundDark,
    onBackground = TextPrimaryDark,
    
    surface = SurfaceDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = SurfaceDarkElevated,
    onSurfaceVariant = TextSecondaryDark,
    
    error = ErrorRed,
    onError = Color.White,
    
    outline = DividerDark,
    outlineVariant = DividerDark.copy(alpha = 0.5f)
)

@Composable
fun MahfilHubTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    darkStatusBarIcons: Boolean = !darkTheme,
    // Disable dynamic color to maintain consistent branding
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
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkStatusBarIcons
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}