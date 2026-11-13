
package com.griffith.truckflowapp.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Defines the colours used when the app is in dark mode. Amber is used as the primary colour to stand out against
// dark backgrounds, while lighter text improves readability.
private val DarkColorScheme = darkColorScheme(
    primary = AmberSecondary,
    onPrimary = NavyDark,
    secondary = AmberDark,
    onSecondary = NavyDark,
    tertiary = SteelTertiary,
    background = NavyDark,
    onBackground = OnSurfaceDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    error = ErrorRed
)

// Defines the colours used when the app is in light mode.
// Navy is the primary colour, amber is used for secondary elements, and light surfaces separate different UI sections.
private val LightColorScheme = lightColorScheme(
    primary = NavyPrimary,
    onPrimary = Color.White,
    secondary = AmberSecondary,
    onSecondary = NavyDark,
    tertiary = SteelTertiary,
    background = SurfaceLight,
    onBackground = OnSurfaceLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    error = ErrorRed
)

// Applies the appropriate colour scheme and typography throughout TruckFlow.
// The theme follows the device's light or dark mode setting unless a value is provided.
@Composable
fun TruckFlowAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Selects the colour scheme based on the current theme.
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    // Updates the status bar to match the selected theme. The icon appearance is adjusted to remain readable.
    // This is skipped in Android Studio's preview mode.
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window

            window.statusBarColor = colorScheme.primary.toArgb()

            WindowCompat.getInsetsController(window, view)
                .isAppearanceLightStatusBars = !darkTheme
        }
    }

    // Makes the selected colours and shared typography available to all composables inside the application.
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
