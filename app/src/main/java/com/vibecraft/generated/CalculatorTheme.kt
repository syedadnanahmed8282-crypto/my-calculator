package com.vibecraft.generated

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/* --------------------------------------------------------------------- */
/* Palette: clean white paper with a forest green ink.                   */
/* --------------------------------------------------------------------- */

/** Main forest green used for operators, the equals key and accents. */
val ForestGreen = Color(0xFF1B5E20)

/** Deeper green for pressed states and the sign/percent labels. */
val ForestGreenDeep = Color(0xFF0E3D13)

/** Very light green wash used for containers and key highlights. */
val ForestGreenSoft = Color(0xFFE4F1E5)

/** Neutral key background for digits. */
val KeySurface = Color(0xFFF4F7F4)

/** Slightly stronger neutral for the function column. */
val KeySurfaceAlt = Color(0xFFEAEFEA)

/** Main display text colour. */
val DisplayText = Color(0xFF16211A)

/** Muted grey green for the small preview line. */
val MutedText = Color(0xFF6B756D)

/** Error red kept harmonious with the green theme. */
val ErrorRed = Color(0xFFB3261E)

private val CalculatorColorScheme = lightColorScheme(
    primary = ForestGreen,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFC8E6C9),
    onPrimaryContainer = Color(0xFF08290B),
    inversePrimary = Color(0xFF8ED694),
    secondary = Color(0xFF2E7D32),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFDDF2DE),
    onSecondaryContainer = Color(0xFF0C2E11),
    tertiary = ForestGreenDeep,
    onTertiary = Color.White,
    tertiaryContainer = ForestGreenSoft,
    onTertiaryContainer = Color(0xFF08290B),
    background = Color.White,
    onBackground = DisplayText,
    surface = Color.White,
    onSurface = DisplayText,
    surfaceVariant = KeySurface,
    onSurfaceVariant = MutedText,
    surfaceTint = ForestGreen,
    inverseSurface = Color(0xFF2F312F),
    inverseOnSurface = Color(0xFFF0F1EE),
    outline = Color(0xFF7A857B),
    outlineVariant = Color(0xFFD9E1DA),
    error = ErrorRed,
    onError = Color.White,
    errorContainer = Color(0xFFF9DEDC),
    onErrorContainer = Color(0xFF410E0B),
    scrim = Color(0xFF000000)
)

/* --------------------------------------------------------------------- */
/* Type: a light, wide display line and sturdy keypad labels.            */
/* --------------------------------------------------------------------- */

private val CalculatorTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Light,
        fontSize = 56.sp,
        lineHeight = 62.sp,
        letterSpacing = (-1).sp
    ),
    displayMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Light,
        fontSize = 46.sp,
        lineHeight = 52.sp,
        letterSpacing = (-0.5).sp
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp,
        lineHeight = 30.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.1.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.3.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.2.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 26.sp,
        lineHeight = 30.sp,
        letterSpacing = 0.sp
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.4.sp
    )
)

private val CalculatorShapes = Shapes(
    extraSmall = RoundedCornerShape(10.dp),
    small = RoundedCornerShape(16.dp),
    medium = RoundedCornerShape(22.dp),
    large = RoundedCornerShape(28.dp),
    extraLarge = RoundedCornerShape(36.dp)
)

/**
 * White and forest green Material 3 theme for the calculator.
 */
@Composable
fun CalculatorTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = CalculatorColorScheme,
        typography = CalculatorTypography,
        shapes = CalculatorShapes,
        content = content
    )
}
