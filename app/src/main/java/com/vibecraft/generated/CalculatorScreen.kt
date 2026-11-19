package com.vibecraft.generated

import android.widget.Toast
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/* --------------------------------------------------------------------- */
/* Layout metrics                                                        */
/*                                                                       */
/* Everything stays private and prefixed so it can never clash with the   */
/* shared pieces declared in CalcComponents.kt.                          */
/* --------------------------------------------------------------------- */

private val ScreenSide = 16.dp
private val PadGap = 8.dp
private val SciGap = 6.dp
private val KeyCorner = 20.dp
private val SciRowHeight = 34.dp
private val SciPadHeight: Dp = SciRowHeight * 2f + SciGap
private const val PadRows = 5

/** Crisp white key body for the light theme. */
private val ScreenKeyWhite = Color(0xFFFFFFFF)

/* --------------------------------------------------------------------- */
/* Gradients                                                             */
/* --------------------------------------------------------------------- */

/** Deep green gradient used on the active chips and the equals key. */
private val ScreenAccentGradient: Brush = Brush.linearGradient(
    colors = listOf(Color(0xFF1F7040), Color(0xFF0E3B23))
)

/** Soft paper wash inside the display card. */
private val ScreenLightWash: Brush = Brush.verticalGradient(
    colors = listOf(Color(0xFFFFFFFF), Color(0xFFF4FAF6))
)

/** Full page wash for the light theme. */
private val ScreenLightPage: Brush = Brush.verticalGradient(
    colors = listOf(Color(0xFFFFFFFF), Color(0xFFF7FCF8), Color(0xFFEEF8F1))
)

/** Full page wash for the night forest theme. */
private val ScreenDarkPage: Brush = Brush.verticalGradient(
    colors = listOf(DarkForestBackground, Color(0xFF091A10), Color(0xFF0C2114))
)

/** Display card wash for the night forest theme. */
private val ScreenDarkWash: Brush = Brush.verticalGradient(
    colors = listOf(Color(0xFF0A1A11), Color(0xFF102A19))
)

/** Gentle green wash behind the operator column. */
private val ScreenOperatorWash: Brush = Brush.verticalGradient(
    colors = listOf(Color(0xFFF3FAF5), Color(0xFFE3F1E8))
)

/* --------------------------------------------------------------------- */
/* Main screen                                                           */
/* --------------------------------------------------------------------- */

/**
 * The whole Hisab experience, drawn edge to edge: the page wash bleeds all
 * the way behind the status and navigation bars while the content itself
 * only respects the system insets.
 *
 * Motion: the whole page fades and lifts in, the display morphs every time
 * the value changes, keys squash with a spring, the equals key breathes,
 * the scientific panel grows out of the keypad and the history sheet slides
 * up over a fading scrim.
 */
@Composable
fun CalculatorScreen(
    modifier: Modifier = Modifier,
    viewModel: CalculatorViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    val haptics = LocalHapticFeedback.current
    val dark = state.isDark
    var historyOpen by remember { mutableStateOf(false) }

    // Wires the calculator to local storage: history, memory and theme.
    LaunchedEffect(Unit) { viewModel.attach(context) }

    val tick: () -> Unit = {
        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
    }

    // Page entrance.
    val entrance = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        entrance.animateTo(1f, tween(durationMillis = 480, easing = FastOutSlowInEasing))
    }

    // Scientific panel expansion.
    val sciProgress by animateFloatAsState(
        targetValue = if (state.scientific) 1f else 0f,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
    )

    val accent = if (dark) ForestGreenGlow else ForestGreen

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(if (dark) ScreenDarkPage else ScreenLightPage)
    ) {
        // Soft radial halo behind the header, bleeding off the top edge.
        Canvas(modifier = Modifier.fillMaxSize()) {
            val haloRadius = size.width * 0.95f
            val haloCenter = Offset(size.width * 0.88f, size.height * 0.01f)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        accent.copy(alpha = if (dark) 0.16f else 0.10f),
                        Color.Transparent
                    ),
                    center = haloCenter,
                    radius = haloRadius
                ),
                radius = haloRadius,
                center = haloCenter
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = ScreenSide, vertical = 10.dp)
                .graphicsLayer {
                    alpha = entrance.value
                    translationY = (1f - entrance.value) * 26f
                }
        ) {
            ScreenHeader(
                dark = dark,
                scientific = state.scientific,
                onToggleTheme = {
                    tick()
                    viewModel.onToggleTheme()
                },
                onToggleScientific = {
                    tick()
                    viewModel.onToggleScientific()
                },
                onOpenHistory = {
                    tick()
                    historyOpen = true
                }
            )

            Spacer(Modifier.height(10.dp))

            ScreenDisplay(
                state = state,
                dark = dark,
                onCopy = {
                    val text = state.copyText
                    if (text.isNotBlank()) {
                        clipboard.setText(AnnotatedString(text))
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        Toast.makeText(context, "Copied", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )

            ScreenMemoryBar(
                state = state,
                dark = dark,
                onRecall = {
                    tick()
                    viewModel.onMemoryRecall()
                },
                onClear = {
                    tick()
                    viewModel.onMemoryClear()
                },
                onAdd = {
                    tick()
                    viewModel.onMemoryAdd()
                },
                onSubtract = {
                    tick()
                    viewModel.onMemorySubtract()
                }
            )

            if (sciProgress > 0.01f) {
                Spacer(Modifier.height(7.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(SciPadHeight * sciProgress)
                        .clipToBounds()
                ) {
                    ScreenScientificPad(
                        state = state,
                        dark = dark,
                        onSin = { tick(); viewModel.onSin() },
                        onCos = { tick(); viewModel.onCos() },
                        onTan = { tick(); viewModel.onTan() },
                        onLn = { tick(); viewModel.onLn() },
                        onSqrt = { tick(); viewModel.onSqrt() },
                        onSquare = { tick(); viewModel.onSquare() },
                        onPi = { tick(); viewModel.onPi() },
                        onOpenParen = { tick(); viewModel.onOpenParen() },
                        onCloseParen = { tick(); viewModel.onCloseParen() },
                        onToggleDegrees = { tick(); viewModel.onToggleDegrees() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .graphicsLayer {
                                alpha = sciProgress
                                translationY = -(1f - sciProgress) * 28f
                            }
                    )
                }
            }

            Spacer(Modifier.height(PadGap))

            ScreenNumericPad(
                dark = dark,
                onDigit = { digit -> tick(); viewModel.onDigit(digit) },
                onDecimal = { tick(); viewModel.onDecimal() },
                onOperator = { symbol -> tick(); viewModel.onOperator(symbol) },
                onClear = { tick(); viewModel.onClear() },
                onToggleSign = { tick(); viewModel.onToggleSign() },
                onPercent = { tick(); viewModel.onPercent() },
                onEquals = { tick(); viewModel.onEquals() },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.45f)
            )
        }

        if (historyOpen) {
            ScreenHistoryOverlay(
                history = state.history,
                dark = dark,
                onDismiss = { historyOpen = false },
                onClear = { viewModel.onClearHistory() },
                onSelect = { entry ->
                    viewModel.onUseHistory(entry)
                    historyOpen = false
                }
            )
        }
    }
}

/* --------------------------------------------------------------------- */
/* Header                                                                */
/* --------------------------------------------------------------------- */

@Composable
private fun ScreenHeader(
    dark: Boolean,
    scientific: Boolean,
    onToggleTheme: () -> Unit,
    onToggleScientific: () -> Unit,
    onOpenHistory: () -> Unit
) {
    val ink = if (dark) DarkDisplayText else ForestGreenDeep
    val accent = if (dark) ForestGreenGlow else ForestGreen

    val transition = rememberInfiniteTransition()
    val breathe by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(9.dp)
                .graphicsLayer {
                    this.alpha = 0.6f + 0.4f * breathe
                    scaleX = 0.92f + 0.16f * breathe
                    scaleY = 0.92f + 0.16f * breathe
                }
                .clip(CircleShape)
                .background(ScreenAccentGradient)
        )
        Spacer(Modifier.width(9.dp))
        Text(
            text = "Hisab",
            color = ink,
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.4.sp,
            maxLines = 1
        )
        Spacer(Modifier.weight(1f))

        ScreenToolChip(
            dark = dark,
            active = scientific,
            onClick = onToggleScientific
        ) {
            Text(
                text = "fx",
                color = if (scientific) Color.White else accent,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )
        }
        Spacer(Modifier.width(7.dp))
        ScreenToolChip(dark = dark, onClick = onOpenHistory) {
            Text(
                text = "≡",
                color = accent,
                fontSize = 19.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )
        }
        Spacer(Modifier.width(7.dp))
        ScreenToolChip(dark = dark, onClick = onToggleTheme) {
            ScreenThemeGlyph(
                dark = dark,
                tint = accent,
                bite = if (dark) DarkKeySurface else ScreenKeyWhite,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun ScreenToolChip(
    dark: Boolean,
    onClick: () -> Unit,
    active: Boolean = false,
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(11.dp)
    val source = remember { MutableInteractionSource() }
    val pressed by source.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.88f else 1f,
        animationSpec = spring(dampingRatio = 0.42f, stiffness = Spring.StiffnessMediumLow)
    )

    Box(
        modifier = Modifier
            .size(34.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clip(shape)
            .then(
                if (active) Modifier.background(ScreenAccentGradient)
                else Modifier.background(if (dark) DarkKeySurface else ScreenKeyWhite)
            )
            .border(
                width = 1.dp,
                color = if (active) Color.Transparent else (if (dark) DarkHairlineBorder else HairlineBorder),
                shape = shape
            )
            .clickable(interactionSource = source, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

/**
 * Tiny hand drawn crescent / sun so the theme toggle needs no extra
 * dependency and never risks a missing glyph.
 */
@Composable
private fun ScreenThemeGlyph(
    dark: Boolean,
    tint: Color,
    bite: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val radius = size.minDimension / 2f
        val middle = center
        if (dark) {
            drawCircle(color = tint, radius = radius * 0.36f, center = middle)
            drawCircle(
                color = tint,
                radius = radius * 0.72f,
                center = middle,
                style = Stroke(width = 1.6.dp.toPx())
            )
        } else {
            drawCircle(color = tint, radius = radius, center = middle)
            drawCircle(
                color = bite,
                radius = radius * 0.86f,
                center = Offset(middle.x + radius * 0.44f, middle.y - radius * 0.34f)
            )
        }
    }
}

/* --------------------------------------------------------------------- */
/* Display                                                               */
/* --------------------------------------------------------------------- */

@Composable
private fun ScreenDisplay(
    state: CalculatorUiState,
    dark: Boolean,
    onCopy: () -> Unit,
    modifier: Modifier = Modifier
) {
    val ink = if (dark) DarkDisplayText else DisplayText
    val mute = if (dark) DarkMutedText else MutedText
    val shape = RoundedCornerShape(24.dp)
    val outline = if (dark) DarkHairlineBorder else HairlineBorder
    val scroll = rememberScrollState()
    val source = remember { MutableInteractionSource() }

    // Keeps the newest part of a long expression in view.
    LaunchedEffect(state.expression) { scroll.scrollTo(Int.MAX_VALUE) }

    Box(
        modifier = modifier
            .shadow(elevation = if (dark) 0.dp else 3.dp, shape = shape)
            .clip(shape)
            .background(if (dark) ScreenDarkWash else ScreenLightWash)
            .border(1.dp, outline, shape)
            .clickable(interactionSource = source, indication = null, onClick = onCopy)
            .padding(horizontal = 18.dp, vertical = 14.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (state.hasMemory) {
                    ScreenPill(text = "M " + state.memoryText, dark = dark)
                }
                Spacer(Modifier.weight(1f))
                if (state.scientific) {
                    ScreenPill(
                        text = if (state.degrees) "DEG" else "RAD",
                        dark = dark
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            ScreenValueText(
                text = state.secondaryText,
                color = if (state.hasError) ErrorRed else mute,
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                alignEnd = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(4.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scroll),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ScreenValueText(
                    text = state.primaryText,
                    color = ink,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Medium,
                    alignEnd = false
                )
            }
        }
    }
}

/**
 * Single line of text that fades and lifts in whenever the value changes,
 * so every key press feels answered.
 */
@Composable
private fun ScreenValueText(
    text: String,
    color: Color,
    fontSize: TextUnit,
    fontWeight: FontWeight,
    alignEnd: Boolean,
    modifier: Modifier = Modifier
) {
    val progress = remember { Animatable(1f) }
    LaunchedEffect(text) {
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing)
        )
    }

    Text(
        text = text,
        color = color,
        fontSize = fontSize,
        fontWeight = fontWeight,
        letterSpacing = 0.2.sp,
        maxLines = 1,
        softWrap = false,
        textAlign = if (alignEnd) TextAlign.End else TextAlign.Start,
        modifier = modifier.graphicsLayer {
            alpha = 0.25f + 0.75f * progress.value
            translationY = (1f - progress.value) * 12f
        }
    )
}

@Composable
private fun ScreenPill(text: String, dark: Boolean) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(7.dp))
            .background(if (dark) ForestGreenSoftDark else ForestGreenSoft)
            .padding(horizontal = 7.dp, vertical = 3.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (dark) ForestGreenGlow else ForestGreen,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            softWrap = false
        )
    }
}

/* --------------------------------------------------------------------- */
/* Memory strip                                                          */
/* --------------------------------------------------------------------- */

@Composable
private fun ScreenMemoryBar(
    state: CalculatorUiState,
    dark: Boolean,
    onRecall: () -> Unit,
    onClear: () -> Unit,
    onAdd: () -> Unit,
    onSubtract: () -> Unit,
    modifier: Modifier = Modifier
) {
    val stored = state.hasMemory
    val accent = if (dark) ForestGreenGlow else ForestGreen
    val valueAlpha by animateFloatAsState(
        targetValue = if (stored) 1f else 0f,
        animationSpec = tween(durationMillis = 240, easing = FastOutSlowInEasing)
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ScreenMemoryChip("MC", dark, enabled = stored, onClick = onClear)
        ScreenMemoryChip("MR", dark, enabled = stored, onClick = onRecall)
        ScreenMemoryChip("M+", dark, onClick = onAdd)
        ScreenMemoryChip("M−", dark, onClick = onSubtract)
        Spacer(Modifier.weight(1f))
        Text(
            text = state.memoryText,
            color = accent,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.End,
            modifier = Modifier.graphicsLayer {
                alpha = valueAlpha
                translationY = (1f - valueAlpha) * 6f
            }
        )
    }
}

@Composable
private fun ScreenMemoryChip(
    label: String,
    dark: Boolean,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(9.dp)
    val source = remember { MutableInteractionSource() }
    val pressed by source.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed && enabled) 0.9f else 1f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessMedium)
    )
    val accent = if (dark) ForestGreenGlow else ForestGreen

    Box(
        modifier = Modifier
            .height(28.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clip(shape)
            .background(if (dark) DarkKeySurface else ScreenKeyWhite)
            .border(1.dp, if (dark) DarkHairlineBorder else HairlineBorder, shape)
            .clickable(
                interactionSource = source,
                indication = null,
                enabled = enabled,
                onClick = onClick
            )
            .padding(horizontal = 11.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = accent.copy(alpha = if (enabled) 1f else 0.32f),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            softWrap = false
        )
    }
}

/* --------------------------------------------------------------------- */
/* Scientific panel                                                      */
/* --------------------------------------------------------------------- */

@Composable
private fun ScreenScientificPad(
    state: CalculatorUiState,
    dark: Boolean,
    onSin: () -> Unit,
    onCos: () -> Unit,
    onTan: () -> Unit,
    onLn: () -> Unit,
    onSqrt: () -> Unit,
    onSquare: () -> Unit,
    onPi: () -> Unit,
    onOpenParen: () -> Unit,
    onCloseParen: () -> Unit,
    onToggleDegrees: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(SciGap)
    ) {
        ScreenSciRow {
            ScreenSciKey("sin", dark, onClick = onSin)
            ScreenSciKey("cos", dark, onClick = onCos)
            ScreenSciKey("tan", dark, onClick = onTan)
            ScreenSciKey("ln", dark, onClick = onLn)
            ScreenSciKey("√", dark, onClick = onSqrt)
        }
        ScreenSciRow {
            ScreenSciKey("x²", dark, onClick = onSquare)
            ScreenSciKey("π", dark, onClick = onPi)
            ScreenSciKey("(", dark, onClick = onOpenParen)
            ScreenSciKey(")", dark, onClick = onCloseParen)
            ScreenSciKey(
                label = if (state.degrees) "DEG" else "RAD",
                dark = dark,
                active = true,
                onClick = onToggleDegrees
            )
        }
    }
}

@Composable
private fun ScreenSciRow(content: @Composable RowScope.() -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(SciRowHeight),
        horizontalArrangement = Arrangement.spacedBy(SciGap),
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )
}

@Composable
private fun RowScope.ScreenSciKey(
    label: String,
    dark: Boolean,
    active: Boolean = false,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(12.dp)
    val source = remember { MutableInteractionSource() }
    val pressed by source.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.92f else 1f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessMedium)
    )
    val accent = if (dark) ForestGreenGlow else ForestGreen

    val fill = when {
        active -> if (dark) ForestGreenSoftDark else ForestGreenSoft
        dark -> DarkKeySurface
        else -> ScreenKeyWhite
    }
    val ink = if (active) accent else (if (dark) DarkMutedText else MutedText)
    val stroke = if (active) accent.copy(alpha = 0.35f) else (if (dark) DarkHairlineBorder else HairlineBorder)

    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clip(shape)
            .background(fill)
            .border(1.dp, stroke, shape)
            .clickable(interactionSource = source, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = ink,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Clip,
            textAlign = TextAlign.Center
        )
    }
}

/* --------------------------------------------------------------------- */
/* Numeric keypad                                                        */
/* --------------------------------------------------------------------- */

@Composable
private fun ScreenNumericPad(
    dark: Boolean,
    onDigit: (Char) -> Unit,
    onDecimal: () -> Unit,
    onOperator: (Char) -> Unit,
    onClear: () -> Unit,
    onToggleSign: () -> Unit,
    onPercent: () -> Unit,
    onEquals: () -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier) {
        val rowHeight = (maxHeight - PadGap * (PadRows - 1).toFloat()) / PadRows.toFloat()

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(PadGap)
        ) {
            ScreenPadRow(rowHeight) {
                ScreenFunctionKey("C", dark, 21.sp, onClick = onClear)
                ScreenFunctionKey("±", dark, 21.sp, onClick = onToggleSign)
                ScreenFunctionKey("%", dark, 19.sp, onClick = onPercent)
                ScreenOperatorKey(OP_DIVIDE.toString(), dark) { onOperator(OP_DIVIDE) }
            }

            ScreenPadRow(rowHeight) {
                ScreenDigitKey("7", dark) { onDigit('7') }
                ScreenDigitKey("8", dark) { onDigit('8') }
                ScreenDigitKey("9", dark) { onDigit('9') }
                ScreenOperatorKey(OP_TIMES.toString(), dark) { onOperator(OP_TIMES) }
            }

            ScreenPadRow(rowHeight) {
                ScreenDigitKey("4", dark) { onDigit('4') }
                ScreenDigitKey("5", dark) { onDigit('5') }
                ScreenDigitKey("6", dark) { onDigit('6') }
                ScreenOperatorKey(OP_MINUS.toString(), dark) { onOperator(OP_MINUS) }
            }

            ScreenPadRow(rowHeight) {
                ScreenDigitKey("1", dark) { onDigit('1') }
                ScreenDigitKey("2", dark) { onDigit('2') }
                ScreenDigitKey("3", dark) { onDigit('3') }
                ScreenOperatorKey(OP_PLUS.toString(), dark) { onOperator(OP_PLUS) }
            }

            ScreenPadRow(rowHeight) {
                ScreenDigitKey("0", dark, weight = 2f) { onDigit('0') }
                ScreenKey(
                    label = DECIMAL.toString(),
                    onClick = onDecimal,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    container = if (dark) DarkKeySurface else ScreenKeyWhite,
                    contentColor = if (dark) DarkDisplayText else DisplayText,
                    borderColor = if (dark) DarkHairlineBorder else HairlineBorder,
                    elevation = if (dark) 0.dp else 2.dp,
                    fontSize = 26.sp
                )
                ScreenEqualsKey(onClick = onEquals)
            }
        }
    }
}

@Composable
private fun ScreenPadRow(
    rowHeight: Dp,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(rowHeight),
        horizontalArrangement = Arrangement.spacedBy(PadGap),
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )
}

@Composable
private fun RowScope.ScreenDigitKey(
    digit: String,
    dark: Boolean,
    weight: Float = 1f,
    onClick: () -> Unit
) {
    ScreenKey(
        label = digit,
        onClick = onClick,
        modifier = Modifier
            .weight(weight)
            .fillMaxHeight(),
        container = if (dark) DarkKeySurface else ScreenKeyWhite,
        contentColor = if (dark) DarkDisplayText else DisplayText,
        borderColor = if (dark) DarkHairlineBorder else HairlineBorder,
        elevation = if (dark) 0.dp else 2.dp,
        fontSize = 24.sp
    )
}

@Composable
private fun RowScope.ScreenFunctionKey(
    label: String,
    dark: Boolean,
    fontSize: TextUnit,
    onClick: () -> Unit
) {
    ScreenKey(
        label = label,
        onClick = onClick,
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight(),
        container = if (dark) DarkKeySurfaceAlt else KeySurfaceAlt,
        contentColor = if (dark) ForestGreenGlow else ForestGreenDeep,
        borderColor = if (dark) DarkHairlineBorder else HairlineBorder,
        elevation = if (dark) 0.dp else 1.dp,
        fontSize = fontSize,
        fontWeight = FontWeight.Medium
    )
}

@Composable
private fun RowScope.ScreenOperatorKey(
    label: String,
    dark: Boolean,
    onClick: () -> Unit
) {
    if (dark) {
        ScreenKey(
            label = label,
            onClick = onClick,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            container = ForestGreenSoftDark,
            contentColor = ForestGreenGlow,
            borderColor = Color(0xFF2B4B36),
            elevation = 0.dp,
            fontSize = 27.sp,
            fontWeight = FontWeight.Medium
        )
    } else {
        ScreenKey(
            label = label,
            onClick = onClick,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            brush = ScreenOperatorWash,
            contentColor = ForestGreen,
            borderColor = Color(0xFFD3E7DA),
            elevation = 2.dp,
            fontSize = 27.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

/** The one key that breathes: a slow halo pulses behind the gradient face. */
@Composable
private fun RowScope.ScreenEqualsKey(onClick: () -> Unit) {
    val shape = RoundedCornerShape(KeyCorner)
    val transition = rememberInfiniteTransition()
    val glow by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .padding(3.dp)
                .graphicsLayer {
                    alpha = 0.14f + 0.26f * glow
                    scaleX = 1.05f
                    scaleY = 1.07f
                }
                .clip(shape)
                .background(ScreenAccentGradient)
        )
        ScreenKey(
            label = "=",
            onClick = onClick,
            modifier = Modifier.fillMaxSize(),
            brush = ScreenAccentGradient,
            contentColor = Color.White,
            borderColor = Color.Transparent,
            elevation = 6.dp,
            fontSize = 28.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

/**
 * One premium key: a springy squash on press, a soft lift and either a
 * flat colour or a gradient face.
 */
@Composable
private fun ScreenKey(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    container: Color = ScreenKeyWhite,
    brush: Brush? = null,
    contentColor: Color = DisplayText,
    borderColor: Color = HairlineBorder,
    elevation: Dp = 2.dp,
    fontSize: TextUnit = 24.sp,
    fontWeight: FontWeight = FontWeight.Normal
) {
    val shape = RoundedCornerShape(KeyCorner)
    val source = remember { MutableInteractionSource() }
    val pressed by source.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.93f else 1f,
        animationSpec = spring(dampingRatio = 0.42f, stiffness = Spring.StiffnessMediumLow)
    )
    val pressAlpha by animateFloatAsState(
        targetValue = if (pressed) 0.86f else 1f,
        animationSpec = tween(durationMillis = 120, easing = FastOutSlowInEasing)
    )

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = pressAlpha
            }
            .shadow(elevation = elevation, shape = shape)
            .clip(shape)
            .then(
                if (brush != null) Modifier.background(brush)
                else Modifier.background(container)
            )
            .border(1.dp, borderColor, shape)
            .clickable(interactionSource = source, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = contentColor,
            fontSize = fontSize,
            fontWeight = fontWeight,
            letterSpacing = 0.2.sp,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Clip,
            textAlign = TextAlign.Center
        )
    }
}

/* --------------------------------------------------------------------- */
/* History sheet                                                         */
/* --------------------------------------------------------------------- */

@Composable
private fun ScreenHistoryOverlay(
    history: List<HistoryEntry>,
    dark: Boolean,
    onDismiss: () -> Unit,
    onClear: () -> Unit,
    onSelect: (HistoryEntry) -> Unit
) {
    val ink = if (dark) DarkDisplayText else DisplayText
    val mute = if (dark) DarkMutedText else MutedText
    val accent = if (dark) ForestGreenGlow else ForestGreen
    val panelColor = if (dark) DarkForestSurface else Color.White
    val scrimSource = remember { MutableInteractionSource() }

    val anim = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        anim.animateTo(
            targetValue = 1f,
            animationSpec = spring(dampingRatio = 0.86f, stiffness = Spring.StiffnessMediumLow)
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { alpha = anim.value }
                .background(Color.Black.copy(alpha = 0.38f))
                .clickable(
                    interactionSource = scrimSource,
                    indication = null,
                    onClick = onDismiss
                )
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .graphicsLayer {
                    alpha = anim.value
                    translationY = (1f - anim.value) * 280f
                }
                .clip(RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp))
                .background(panelColor)
                .padding(horizontal = 18.dp)
                .padding(top = 14.dp, bottom = 24.dp)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(38.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(if (dark) DarkHairlineBorder else HairlineBorder)
            )

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "History",
                    color = ink,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )
                Spacer(Modifier.weight(1f))
                if (history.isNotEmpty()) {
                    Text(
                        text = "Clear",
                        color = accent,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable(onClick = onClear)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                    Spacer(Modifier.width(2.dp))
                }
                Text(
                    text = "✕",
                    color = mute,
                    fontSize = 14.sp,
                    maxLines = 1,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(onClick = onDismiss)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            Spacer(Modifier.height(10.dp))

            if (history.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No calculations yet",
                        color = mute,
                        fontSize = 14.sp,
                        maxLines = 1
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.heightIn(max = 300.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(history) { entry ->
                        ScreenHistoryRow(
                            entry = entry,
                            dark = dark,
                            onClick = { onSelect(entry) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ScreenHistoryRow(
    entry: HistoryEntry,
    dark: Boolean,
    onClick: () -> Unit
) {
    val mute = if (dark) DarkMutedText else MutedText
    val accent = if (dark) ForestGreenGlow else ForestGreen
    val stamp = remember(entry.timestamp) { formatHistoryStamp(entry.timestamp) }
    val source = remember { MutableInteractionSource() }
    val pressed by source.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessMedium)
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clip(RoundedCornerShape(16.dp))
            .background(if (dark) DarkKeySurface else ForestGreenSoft)
            .clickable(interactionSource = source, indication = null, onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Text(
            text = entry.expression,
            color = mute,
            fontSize = 13.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = entry.result,
            color = accent,
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        if (stamp.isNotEmpty()) {
            Spacer(Modifier.height(3.dp))
            Text(
                text = stamp,
                color = mute,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/** Friendly stamp for a saved calculation, empty when there is no time. */
private fun formatHistoryStamp(timestamp: Long): String {
    if (timestamp <= 0L) return ""
    val format = SimpleDateFormat("d MMM · h:mm a", Locale.getDefault())
    return format.format(Date(timestamp))
}
