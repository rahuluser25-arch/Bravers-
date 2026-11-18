package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val BraversLightColorScheme = lightColorScheme(
    primary = ObsidianInk,
    onPrimary = BoneBackground,
    primaryContainer = SandSurfaceVariant,
    onPrimaryContainer = ObsidianInk,
    secondary = BraversCrimson,
    onSecondary = Color.White,
    secondaryContainer = BraversCrimsonSoft,
    onSecondaryContainer = BraversCrimson,
    tertiary = TerracottaGold,
    onTertiary = Color.White,
    background = BoneBackground,
    onBackground = ObsidianInk,
    surface = AlabasterSurface,
    onSurface = ObsidianInk,
    surfaceVariant = SandSurfaceVariant,
    onSurfaceVariant = MutedSlate,
    outline = WarmOutline
)

private val BraversDarkColorScheme = darkColorScheme(
    primary = BoneInk,
    onPrimary = DarkObsidianBg,
    primaryContainer = DarkElevatedVariant,
    onPrimaryContainer = BoneInk,
    secondary = DarkCrimsonAccent,
    onSecondary = Color.White,
    secondaryContainer = DarkCrimsonContainer,
    onSecondaryContainer = Color(0xFFFFB4AC),
    tertiary = DarkGoldAccent,
    onTertiary = DarkObsidianBg,
    background = DarkObsidianBg,
    onBackground = BoneInk,
    surface = DarkCharcoalSurface,
    onSurface = BoneInk,
    surfaceVariant = DarkElevatedVariant,
    onSurfaceVariant = DarkMutedInk,
    outline = DarkOutline
)

val BraversShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun BraversTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) BraversDarkColorScheme else BraversLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = BraversTypography,
        shapes = BraversShapes,
        content = content
    )
}
