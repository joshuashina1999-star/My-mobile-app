package com.example.ui.theme

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
    primary = NeonGreen,
    secondary = ElectricBlue,
    tertiary = BrightCyan,
    background = SlateBackground,
    surface = SlateSurface,
    onBackground = OffWhite,
    onSurface = OffWhite,
    primaryContainer = SlateCard,
    onPrimaryContainer = OffWhite,
    outline = SlateBorder
)

private val LightColorScheme = lightColorScheme(
    primary = NeonGreen,
    secondary = ElectricBlue,
    tertiary = BrightCyan,
    background = SlateBackground, // We keep the athletic dark ambient theme active across both modes for maximum consistency
    surface = SlateSurface,
    onBackground = OffWhite,
    onSurface = OffWhite,
    primaryContainer = SlateCard,
    onPrimaryContainer = OffWhite,
    outline = SlateBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // We disable dynamic color to preserve our custom premium athletic branding on all devices
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
