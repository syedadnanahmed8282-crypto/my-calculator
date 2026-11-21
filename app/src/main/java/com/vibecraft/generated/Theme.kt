package com.vibecraft.generated

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Forest green + clean white palette used across the habit tracker.
 */
object HabitPalette {
    val Forest = Color(0xFF1B5E20)
    val ForestLight = Color(0xFF2E7D32)
    val Leaf = Color(0xFF4CAF50)
    val Mist = Color(0xFFE8F5E9)
    val SoftGreen = Color(0xFFC8E6C9)
    val Ink = Color(0xFF1A1C1A)
    val Muted = Color(0xFF5A615A)
    val Line = Color(0xFFE1E7E1)
    val White = Color(0xFFFFFFFF)
    val Surface = Color(0xFFF7FAF7)
}

private val HabitColorScheme = lightColorScheme(
    primary = HabitPalette.Forest,
    onPrimary = HabitPalette.White,
    primaryContainer = HabitPalette.SoftGreen,
    onPrimaryContainer = Color(0xFF0B2E0E),
    inversePrimary = Color(0xFF8FD694),

    secondary = HabitPalette.ForestLight,
    onSecondary = HabitPalette.White,
    secondaryContainer = HabitPalette.Mist,
    onSecondaryContainer = Color(0xFF12300F),

    tertiary = HabitPalette.Leaf,
    onTertiary = HabitPalette.White,
    tertiaryContainer = Color(0xFFDDF2DE),
    onTertiaryContainer = Color(0xFF14361A),

    background = HabitPalette.White,
    onBackground = HabitPalette.Ink,

    surface = HabitPalette.White,
    onSurface = HabitPalette.Ink,
    surfaceVariant = HabitPalette.Surface,
    onSurfaceVariant = HabitPalette.Muted,
    surfaceTint = HabitPalette.Forest,
    inverseSurface = Color(0xFF2C312C),
    inverseOnSurface = Color(0xFFEFF2EE),

    outline = Color(0xFFBFC9BF),
    outlineVariant = HabitPalette.Line,

    error = Color(0xFFB3261E),
    onError = HabitPalette.White,
    errorContainer = Color(0xFFF9DEDC),
    onErrorContainer = Color(0xFF410E0B),

    scrim = Color(0xFF000000)
)

private val HabitTypography = Typography(
    displaySmall = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 34.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 26.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp
    ),
    titleLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 19.sp,
        lineHeight = 24.sp
    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.1.sp
    ),
    titleSmall = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.3.sp
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.2.sp
    ),
    bodySmall = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.3.sp
    ),
    labelLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.2.sp
    ),
    labelMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    labelSmall = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.4.sp
    )
)

private val HabitShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun VibecraftTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = HabitColorScheme,
        typography = HabitTypography,
        shapes = HabitShapes,
        content = content
    )
}
