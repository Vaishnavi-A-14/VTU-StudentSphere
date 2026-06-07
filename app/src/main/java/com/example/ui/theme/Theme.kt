package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CustomDarkColorScheme = darkColorScheme(
    primary = TechPrimary,
    onPrimary = Color.White,
    secondary = TechSecondary,
    onSecondary = Color.Black,
    tertiary = TechTertiary,
    onTertiary = Color.White,
    background = TechDarkBg,
    onBackground = TechTextPrimary,
    surface = TechCardBg,
    onSurface = TechTextPrimary,
    surfaceVariant = TechBorder,
    onSurfaceVariant = TechTextSecondary,
    outline = TechBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force dark theme for beautiful cinematic tech experience
    dynamicColor: Boolean = false, // Disable dynamic colors so our customized tech palette shines consistently
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = CustomDarkColorScheme,
        typography = Typography,
        content = content
    )
}
