package com.vibecraft.generated

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/* --------------------------------------------------------------------- */
/* Palette: polished paper white with deep forest green ink.             */
/* --------------------------------------------------------------------- */

/** Primary forest green: deep, saturated and expensive looking. */
val ForestGreen: Color = Color(0xFF14532D)

/** Even deeper green for the ends of gradients and pressed states. */
val ForestGreenDeep: Color = Color(0xFF0A2E18)

/** Rich mid green used as the bright stop of the accent gradient. */
val ForestGreenRich: Color = Color(0xFF1E6B3A)

/** Luminous green for small highlights and the sign accents. */
val ForestGreenBright: Color = Color(0xFF2E9E5B)

/** Very light green wash used for containers and highlighted keys. */
val ForestGreenSoft: Color = Color(0xFFE8F3EB)

/** Neutral key background for digits. */
val KeySurface: Color = Color(0xFFF7FAF7)

/** Slightly stronger neutral for the function column. */
val KeySurfaceAlt: Color = Color(0xFFEDF3EE)

/** Main display text colour. */
val DisplayText: Color = Color(0xFF0F1A13)

/** Muted grey green for the small preview line. */
val MutedText: Color = Color(0xFF6E7B71)

/** Hairline border used for the subtle premium outlines. */
val HairlineBorder: Color = Color(0xFFE1EAE2)

/** Error red kept harmonious with the green theme. */
val ErrorRed: Color = Color(0xFFB3261E)

/* --------------------------------------------------------------------- */
/* Dark palette: night forest. Same green soul, much lower light.        */
/* --------------------------------------------------------------------- */

/** Deep night background with a green undertone. */
val DarkForestBackground: Color = Color(0xFF06120B)

/** Card / panel surface for the dark theme. */
val DarkForestSurface: Color = Color(0xFF0C1A11)

/** Elevated key surface for digits in the dark theme. */
val DarkKeySurface: Color = Color(0xFF14251A)

/** Slightly stronger elevated surface for the function column. */
val DarkKeySurfaceAlt: Color = Color(0xFF1B2F21)

/** Main display text colour in the dark theme (soft mint white). */
val DarkDisplayText: Color = Color(0xFFEAF5EC)

/** Muted grey green for the small preview line in the dark theme. */
val DarkMutedText: Color = Color(0xFF94A89A)

/** Hairline border for the dark theme. */
val DarkHairlineBorder: Color = Color(0xFF223528)

/** Bright mint green used as the primary accent on dark surfaces. */
val ForestGreenGlow: Color = Color(0xFF6FDD93)

/** Soft mint wash used for highlighted containers in the dark theme. */
val ForestGreenSoftDark: Color = Color(0xFF16301F)

/* --------------------------------------------------------------------- */
/* Gradients: soft page wash plus the accent gradient for key surfaces.  */
/* --------------------------------------------------------------------- */

/** Very soft white-to-green page wash that makes surfaces feel layered. */
val PremiumBackground: Brush = Brush.verticalGradient(
    colors = listOf(
        Color(0xFFFFFFFF),
        Color(0xFFF4F9F5),
        Color(0xFFE8F2EA)
    )
)

/** Deep forest gradient for the equals key and other primary accents. */
val AccentGradient: Brush = Brush.linearGradient(
    colors = listOf(
        ForestGreenRich,
        ForestGreen,
        ForestGreenDeep
    )
)

/** Lighter accent gradient for secondary highlights and chips. */
val AccentGradientSoft: Brush = Brush.verticalGradient(
    colors = listOf(
        Color(0xFFF3FAF4),
        ForestGreenSoft
    )
)

/** Glassy lift for the display panel. */
val DisplayGradient: Brush = Brush.verticalGradient(
    colors = listOf(
        Color(0xFFFFFFFF),
        Color(0xFFFBFDFB)
    )
)

/** Night version of the page wash. */
val PremiumBackgroundDark: Brush = Brush.verticalGradient(
    colors = listOf(
        Color(0xFF07150D),
        Color(0xFF0A1A10),
        Color(0xFF06120B)
    )
)

/** Brighter accent gradient so the equals key pops in the dark theme. */
val AccentGradientDark: Brush = Brush.linearGradient(
    colors = listOf(
        ForestGreenGlow,
        ForestGreenBright,
        ForestGreenRich
    )
)

/** Lighter accent gradient for chips and highlights in the dark theme. */
val AccentGradientSoftDark: Brush = Brush.verticalGradient(
    colors = listOf(
        Color(0xFF12241A),
        ForestGreenSoftDark
    )
)

/** Glassy lift for the display panel in the dark theme. */
val DisplayGradientDark: Brush = Brush.verticalGradient(
    colors = listOf(
        Color(0xFF0F2015),
        Color(0xFF0A1710)
    )
)

private val CalculatorColorScheme = lightColorScheme(
    primary = ForestGreen,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFCDE9D3),
    onPrimaryContainer = Color(0xFF06220F),
    inversePrimary = Color(0xFF8FD9A1),
    secondary = ForestGreenRich,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFDDF1E1),
    onSecondaryContainer = Color(0xFF0A2B14),
    tertiary = ForestGreenDeep,
    onTertiary = Color.White,
    tertiaryContainer = ForestGreenSoft,
    onTertiaryContainer = Color(0xFF06220F),
    background = Color.White,
    onBackground = DisplayText,
    surface = Color.White,
    onSurface = DisplayText,
    surfaceVariant = KeySurface,
    onSurfaceVariant = MutedText,
    surfaceTint = ForestGreen,
    inverseSurface = Color(0xFF2C332E),
    inverseOnSurface = Color(0xFFF1F4F0),
    outline = Color(0xFF7C897E),
    outlineVariant = HairlineBorder,
    error = ErrorRed,
    onError = Color.White,
    errorContainer = Color(0xFFF9DEDC),
    onErrorContainer = Color(0xFF410E0B),
    scrim = Color(0xFF000000)
)

private val CalculatorDarkColorScheme = darkColorScheme(
    primary = ForestGreenGlow,
    onPrimary = Color(0xFF06220F),
    primaryContainer = ForestGreenRich,
    onPrimaryContainer = Color(0xFFD7F5E0),
    inversePrimary = ForestGreen,
    secondary = Color(0xFF9BD8AE),
    onSecondary = Color(0xFF08290F),
    secondaryContainer = ForestGreenSoftDark,
    onSecondaryContainer = Color(0xFFCDEBD5),
    tertiary = Color(0xFFB7E5C4),
    onTertiary = Color(0xFF0A2B14),
    tertiaryContainer = Color(0xFF1B3A24),
    onTertiaryContainer = Color(0xFFDCF3E2),
    background = DarkForestBackground,
    onBackground = DarkDisplayText,
    surface = DarkForestSurface,
    onSurface = DarkDisplayText,
    surfaceVariant = DarkKeySurface,
    onSurfaceVariant = DarkMutedText,
    surfaceTint = ForestGreenGlow,
    inverseSurface = Color(0xFFE3EFE6),
    inverseOnSurface = Color(0xFF12211A),
    outline = Color(0xFF4A6152),
    outlineVariant = DarkHairlineBorder,
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    scrim = Color(0xFF000000)
)

/* --------------------------------------------------------------------- */
/* Type: a light, wide display line and sturdy keypad labels.            */
/* --------------------------------------------------------------------- */

private val CalculatorTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Light,
        fontSize = 58.sp,
        lineHeight = 64.sp,
        letterSpacing = (-1.6).sp
    ),
    displayMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Light,
        fontSize = 46.sp,
        lineHeight = 52.sp,
        letterSpacing = (-0.8).sp
    ),
    displaySmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Light,
        fontSize = 36.sp,
        lineHeight = 42.sp,
        letterSpacing = (-0.4).sp
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp,
        lineHeight = 30.sp,
        letterSpacing = (-0.2).sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = (-0.2).sp
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
        letterSpacing = 0.2.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.2.sp
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.3.sp
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
        letterSpacing = 0.6.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.8.sp
    )
)

private val CalculatorShapes = Shapes(
    extraSmall = RoundedCornerShape(12.dp),
    small = RoundedCornerShape(18.dp),
    medium = RoundedCornerShape(24.dp),
    large = RoundedCornerShape(30.dp),
    extraLarge = RoundedCornerShape(38.dp)
)

/** Round-square shape used by the keypad keys. */
val KeyShape: RoundedCornerShape = RoundedCornerShape(22.dp)

/** Softer, wider shape for the wide zero / equals keys. */
val WideKeyShape: RoundedCornerShape = RoundedCornerShape(26.dp)

/** Shape of the frosted display panel. */
val DisplayShape: RoundedCornerShape = RoundedCornerShape(32.dp)

/* --------------------------------------------------------------------- */
/* Motion: one shared language of springs and fades for the whole app.   */
/* Edge-to-edge screens animate, they never jump.                        */
/* --------------------------------------------------------------------- */

/** Quick fade for text swaps in the display preview line. */
const val HisabDurationFast: Int = 150

/** Standard fade / slide for panels and chips. */
const val HisabDurationMedium: Int = 280

/** Slower, luxurious entrance for large blocks of the keypad. */
const val HisabDurationSlow: Int = 440

/** Springy press feedback used by every key. */
val HisabPressSpring = spring<Float>(
    dampingRatio = Spring.DampingRatioMediumBouncy,
    stiffness = Spring.StiffnessMediumLow
)

/** Tight spring for scale pops, such as the equals key lighting up. */
val HisabScaleSpring = spring<Float>(
    dampingRatio = 0.58f,
    stiffness = 880f
)

/** Loose spring for sliding blocks in from the edges of the screen. */
val HisabSlideSpring = spring<Float>(
    dampingRatio = Spring.DampingRatioLowBouncy,
    stiffness = Spring.StiffnessLow
)

/** Smooth fade used with text and colour changes. */
val HisabFade = tween<Float>(
    durationMillis = HisabDurationMedium,
    easing = FastOutSlowInEasing
)

/** Long, gentle entrance tween for staggered elements. */
val HisabEnter = tween<Float>(
    durationMillis = HisabDurationSlow,
    easing = LinearOutSlowInEasing
)

/** Extra breathing room so content never hides behind system bars. */
val HisabEdgePadding = 18.dp

/** True when the screen is drawn edge to edge behind system bars. */
val LocalHisabEdgeToEdge = staticCompositionLocalOf { true }

/* --------------------------------------------------------------------- */
/* Theme aware accessors.                                                */
/* --------------------------------------------------------------------- */

/** True while the dark theme is active, available anywhere in the tree. */
val LocalHisabDark = staticCompositionLocalOf { false }

/** Page wash for the active theme. */
val hisabPageBackground: Brush
    @Composable get() = if (LocalHisabDark.current) PremiumBackgroundDark else PremiumBackground

/** Accent gradient for the equals key and other primary surfaces. */
val hisabAccentGradient: Brush
    @Composable get() = if (LocalHisabDark.current) AccentGradientDark else AccentGradient

/** Soft accent gradient for chips and highlights. */
val hisabAccentGradientSoft: Brush
    @Composable get() = if (LocalHisabDark.current) AccentGradientSoftDark else AccentGradientSoft

/** Glassy gradient for the display panel. */
val hisabDisplayGradient: Brush
    @Composable get() = if (LocalHisabDark.current) DisplayGradientDark else DisplayGradient

/** Neutral digit key background for the active theme. */
val hisabKeySurface: Color
    @Composable get() = if (LocalHisabDark.current) DarkKeySurface else KeySurface

/** Slightly stronger key background (functions, memory row). */
val hisabKeySurfaceAlt: Color
    @Composable get() = if (LocalHisabDark.current) DarkKeySurfaceAlt else KeySurfaceAlt

/** Main ink colour for numbers and operators. */
val hisabInk: Color
    @Composable get() = if (LocalHisabDark.current) DarkDisplayText else DisplayText

/** Muted colour for the small preview line and captions. */
val hisabMutedInk: Color
    @Composable get() = if (LocalHisabDark.current) DarkMutedText else MutedText

/** Hairline outline colour for the premium borders. */
val hisabHairline: Color
    @Composable get() = if (LocalHisabDark.current) DarkHairlineBorder else HairlineBorder

/** Accent green that stays readable on the current background. */
val hisabAccent: Color
    @Composable get() = if (LocalHisabDark.current) ForestGreenGlow else ForestGreen

/** Soft green container wash for highlighted keys. */
val hisabAccentSoft: Color
    @Composable get() = if (LocalHisabDark.current) ForestGreenSoftDark else ForestGreenSoft

/**
 * Premium white and forest green Material 3 theme for the calculator.
 *
 * The theme also publishes the motion and edge-to-edge hints so every screen
 * shares the same spring language and draws under the system bars.
 *
 * @param darkTheme true for the night forest palette, false for paper white.
 */
@Composable
fun CalculatorTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalHisabDark provides darkTheme,
        LocalHisabEdgeToEdge provides true
    ) {
        MaterialTheme(
            colorScheme = if (darkTheme) CalculatorDarkColorScheme else CalculatorColorScheme,
            typography = CalculatorTypography,
            shapes = CalculatorShapes,
            content = content
        )
    }
}
