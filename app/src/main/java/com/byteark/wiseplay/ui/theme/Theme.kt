package com.byteark.wiseplay.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = DarkBluePurple,
    onPrimary = White,
    primaryContainer = MediumBlue,
    onPrimaryContainer = White,
    
    secondary = LightBlueCyan,
    onSecondary = White,
    secondaryContainer = Cyan,
    onSecondaryContainer = DarkGrey,
    
    tertiary = TealGreen,
    onTertiary = White,
    tertiaryContainer = TealGreenAlt,
    onTertiaryContainer = White,
    
    error = Purple,
    onError = White,
    errorContainer = PurpleAlt,
    onErrorContainer = White,
    
    background = DarkGrey,
    onBackground = White,
    
    surface = MediumDarkGrey,
    onSurface = White,
    surfaceVariant = MediumGrey,
    onSurfaceVariant = LightGrey,
    
    outline = MediumGrey,
    outlineVariant = MediumDarkGrey
)

private val LightColorScheme = lightColorScheme(
    primary = DarkBluePurple,
    onPrimary = White,
    primaryContainer = LightBlue,
    onPrimaryContainer = DarkBluePurple,
    
    secondary = MediumBlue,
    onSecondary = White,
    secondaryContainer = LightBlueAlt,
    onSecondaryContainer = MediumBlue,
    
    tertiary = TealGreen,
    onTertiary = White,
    tertiaryContainer = LightBlue,
    onTertiaryContainer = TealGreen,
    
    error = ErrorRed,
    onError = White,
    errorContainer = LightBlue,
    onErrorContainer = ErrorRed,
    
    background = White,
    onBackground = DarkGrey,
    
    surface = White,
    onSurface = DarkGrey,
    surfaceVariant = LightGrey,
    onSurfaceVariant = MediumDarkGrey,
    
    outline = MediumGrey,
    outlineVariant = MediumLightGrey
)

@Composable
fun WisePlayDRMTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}