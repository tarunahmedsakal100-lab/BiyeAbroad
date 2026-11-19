package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val BiyeAbroadLightScheme = lightColorScheme(
    primary = BurgundyPrimary,
    onPrimary = PureWhite,
    primaryContainer = GoldLight,
    onPrimaryContainer = BurgundyDeep,
    secondary = GoldAccent,
    onSecondary = BurgundyDeep,
    secondaryContainer = WarmIvory,
    onSecondaryContainer = DarkText,
    background = WarmIvory,
    onBackground = DarkText,
    surface = SurfaceCard,
    onSurface = Color.Black,
    surfaceVariant = WarmIvory,
    onSurfaceVariant = Color.Black,
    outline = GoldBorder,
    error = ErrorRed,
    onError = PureWhite
)

private val BiyeAbroadDarkScheme = darkColorScheme(
    primary = GoldAccent,
    onPrimary = BurgundyDeep,
    primaryContainer = BurgundyPrimary,
    onPrimaryContainer = GoldLight,
    secondary = GoldLight,
    onSecondary = BurgundyDeep,
    background = BurgundyDeep,
    onBackground = WarmIvory,
    surface = Color(0xFF2A040E),
    onSurface = PureWhite,
    outline = GoldAccent
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false, // Keep bespoke comfortable light theme across all devices
    dynamicColor: Boolean = false, // Preserve bespoke premium matrimony brand identity
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) BiyeAbroadDarkScheme else BiyeAbroadLightScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun biyeAbroadTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Color.Black,
    unfocusedTextColor = Color.Black,
    cursorColor = BurgundyPrimary,
    focusedBorderColor = BurgundyPrimary,
    unfocusedBorderColor = GoldBorder,
    focusedLabelColor = BurgundyPrimary,
    unfocusedLabelColor = Color(0xFF555555),
    focusedPlaceholderColor = Color(0xFF777777),
    unfocusedPlaceholderColor = Color(0xFF777777)
)
