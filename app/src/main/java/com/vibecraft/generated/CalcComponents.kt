package com.vibecraft.generated

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/* --------------------------------------------------------------------- */
/* Shared metrics                                                        */
/*                                                                       */
/* Every top level helper in this file carries the "Calc" prefix so it    */
/* can never clash with a declaration in another file of the package.     */
/* --------------------------------------------------------------------- */

private val CalcKeyGap = 8.dp
private val CalcSciGap = 6.dp
private val CalcBigKeyShape = RoundedCornerShape(18.dp)
private val CalcSciChipShape = RoundedCornerShape(12.dp)

/* --------------------------------------------------------------------- */
/* Motion vocabulary                                                     */
/* --------------------------------------------------------------------- */

/** Very short colour / hover tween. */
private val CalcQuickTween = tween<Float>(
    durationMillis = 130,
    easing = FastOutSlowInEasing
)

/** Bouncy spring used when a key is pressed. */
private val CalcKeySpring = spring<Float>(
    dampingRatio = 0.42f,
    stiffness = Spring.StiffnessHigh
)

/** Softer spring used for panels flying in. */
private val CalcInSpring = spring<Float>(
    dampingRatio = 0.72f,
    stiffness = Spring.StiffnessMediumLow
)

/** The forest gradient carried by the equals key. */
private val CalcAccentBrush: Brush = Brush.verticalGradient(
    colors = listOf(ForestGreenRich, ForestGreen, ForestGreenDeep)
)

/** Visual families used by [CalcKey]. */
enum class CalcKeyStyle { Digit, Function, Operator, Accent }

/* --------------------------------------------------------------------- */
/* Display                                                               */
/* --------------------------------------------------------------------- */

/**
 * The premium display block: memory chip, angle chip, the live preview line
 * and the large expression. Tapping the big line copies it to the clipboard
 * through [onCopy]. Everything here reacts with spring and fade animations.
 */
@Composable
fun CalculatorDisplay(
    state: CalculatorUiState,
    onCopy: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dark = state.isDark
    val errorFraction by animateFloatAsState(
        targetValue = if (state.hasError) 1f else 0f,
        animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing),
        label = "displayError"
    )

    val primaryColor = lerp(if (dark) DarkDisplayText else DisplayText, ErrorRed, errorFraction)
    val mutedColor = if (dark) DarkMutedText else MutedText
    val secondaryColor = lerp(mutedColor, ErrorRed, errorFraction)
    val scroll = rememberScrollState()

    // Keep the newest typed characters visible when the expression grows.
    LaunchedEffect(state.primaryText) {
        scroll.animateScrollTo(scroll.maxValue)
    }

    // Spring pop when a result lands on the display.
    val resultScale = remember { Animatable(1f) }
    LaunchedEffect(state.showingResult) {
        if (state.showingResult) {
            resultScale.snapTo(0.9f)
            resultScale.animateTo(
                targetValue = 1f,
                animationSpec = spring<Float>(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            )
        } else {
            resultScale.animateTo(targetValue = 1f, animationSpec = CalcQuickTween)
        }
    }

    // Gentle fade on the small preview line whenever it changes.
    val previewAlpha = remember { Animatable(1f) }
    LaunchedEffect(state.secondaryText) {
        if (state.hasSecondary) {
            previewAlpha.snapTo(0.45f)
            previewAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing)
            )
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 104.dp),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Bottom
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (state.hasMemory) {
                CalcMemoryBadge(text = state.memoryText, dark = dark)
            }
            Spacer(Modifier.weight(1f))
            if (state.scientific) {
                CalcAngleBadge(degrees = state.degrees, dark = dark)
            }
        }

        Spacer(Modifier.height(8.dp))

        if (state.hasSecondary) {
            Text(
                text = state.secondaryText,
                color = secondaryColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(previewAlpha.value)
                    .offset(y = ((1f - previewAlpha.value) * 5f).dp)
            )
            Spacer(Modifier.height(4.dp))
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .clickable { onCopy() }
                .horizontalScroll(scroll),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = state.primaryText,
                color = primaryColor,
                fontSize = calcDisplayFontSize(state.primaryText),
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.SansSerif,
                letterSpacing = (-0.5).sp,
                maxLines = 1,
                modifier = Modifier.scale(resultScale.value)
            )
        }
    }
}

private fun calcDisplayFontSize(text: String): TextUnit = when {
    text.length <= 9 -> 44.sp
    text.length <= 13 -> 34.sp
    text.length <= 18 -> 26.sp
    else -> 20.sp
}

@Composable
private fun CalcMemoryBadge(text: String, dark: Boolean) {
    val bg = if (dark) ForestGreenSoftDark else ForestGreenSoft
    val fg = if (dark) ForestGreenGlow else ForestGreen
    val appear = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        appear.animateTo(targetValue = 1f, animationSpec = tween(durationMillis = 240))
    }
    Row(
        modifier = Modifier
            .alpha(appear.value)
            .scale(0.9f + 0.1f * appear.value)
            .clip(RoundedCornerShape(50))
            .background(bg)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "M", color = fg, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.width(6.dp))
        Text(
            text = text,
            color = fg,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1
        )
    }
}

@Composable
private fun CalcAngleBadge(degrees: Boolean, dark: Boolean) {
    val bg = if (dark) DarkKeySurfaceAlt else KeySurfaceAlt
    val fg = if (dark) ForestGreenGlow else ForestGreenRich
    val appear = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        appear.animateTo(targetValue = 1f, animationSpec = tween(durationMillis = 240))
    }
    Text(
        text = if (degrees) "DEG" else "RAD",
        color = fg,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.8.sp,
        modifier = Modifier
            .alpha(appear.value)
            .scale(0.9f + 0.1f * appear.value)
            .clip(RoundedCornerShape(50))
            .background(bg)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    )
}

/* --------------------------------------------------------------------- */
/* Scientific panel                                                      */
/* --------------------------------------------------------------------- */

/**
 * Expandable rows of scientific keys. [onFunction] receives the raw key name
 * ("sin", "cos", "tan", "ln", "log", "√", "x2", "π", "e", "(", ")") so the
 * view model decides how to fold it into the expression.
 */
@Composable
fun ScientificPanel(
    degrees: Boolean,
    isDark: Boolean,
    onFunction: (String) -> Unit,
    onToggleDegrees: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(CalcSciGap)
    ) {
        CalcSciRow {
            CalcSciChip(
                label = if (degrees) "DEG" else "RAD",
                highlighted = true,
                isDark = isDark,
                onClick = onToggleDegrees,
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f),
                index = 0
            )
            CalcSciKey("sin", isDark, onFunction, 1)
            CalcSciKey("cos", isDark, onFunction, 2)
            CalcSciKey("tan", isDark, onFunction, 3)
        }

        CalcSciRow {
            CalcSciKey("ln", isDark, onFunction, 4)
            CalcSciKey("log", isDark, onFunction, 5)
            CalcSciKey("√", isDark, onFunction, 6)
            CalcSciKey("x2", isDark, onFunction, 7)
        }

        CalcSciRow {
            CalcSciKey("π", isDark, onFunction, 8)
            CalcSciKey("e", isDark, onFunction, 9)
            CalcSciKey("(", isDark, onFunction, 10)
            CalcSciKey(")", isDark, onFunction, 11)
        }
    }
}

@Composable
private fun CalcSciRow(content: @Composable RowScope.() -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(38.dp),
        horizontalArrangement = Arrangement.spacedBy(CalcSciGap),
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )
}

@Composable
private fun RowScope.CalcSciKey(
    label: String,
    isDark: Boolean,
    onFunction: (String) -> Unit,
    index: Int
) {
    CalcSciChip(
        label = label,
        highlighted = false,
        isDark = isDark,
        onClick = { onFunction(label) },
        modifier = Modifier
            .fillMaxHeight()
            .weight(1f),
        index = index
    )
}

@Composable
private fun CalcSciChip(
    label: String,
    highlighted: Boolean,
    isDark: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    index: Int = 0
) {
    val baseBg = when {
        highlighted && isDark -> ForestGreenSoftDark
        highlighted -> ForestGreenSoft
        isDark -> DarkKeySurface
        else -> KeySurface
    }
    val pressedBg = if (isDark) ForestGreenSoftDark else ForestGreenSoft

    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val press by animateFloatAsState(
        targetValue = if (pressed) 1f else 0f,
        animationSpec = CalcQuickTween,
        label = "sciPress"
    )
    val scaleValue by animateFloatAsState(
        targetValue = if (pressed) 0.9f else 1f,
        animationSpec = CalcKeySpring,
        label = "sciScale"
    )

    // Staggered entrance so the panel blooms open row by row.
    val appear = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        delay(18L * index)
        appear.animateTo(targetValue = 1f, animationSpec = tween(durationMillis = 300))
    }

    val fg = if (isDark) ForestGreenGlow else ForestGreen
    val borderColor = if (isDark) DarkHairlineBorder else HairlineBorder

    Box(
        modifier = modifier
            .scale(scaleValue)
            .alpha(appear.value)
            .clip(CalcSciChipShape)
            .background(lerp(baseBg, pressedBg, press))
            .border(1.dp, borderColor, CalcSciChipShape)
            .clickable(
                interactionSource = interaction,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = fg,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1
        )
    }
}

/* --------------------------------------------------------------------- */
/* Keys                                                                  */
/* --------------------------------------------------------------------- */

/**
 * One calculator key. Digits are neutral, functions are tinted, operators
 * use the soft green wash and the equals key carries the forest gradient.
 * Every key springs down under the finger and washes green while held.
 */
@Composable
fun CalcKey(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: CalcKeyStyle = CalcKeyStyle.Digit,
    isDark: Boolean = false,
    fontSize: TextUnit = 24.sp,
    icon: ImageVector? = null
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()

    val press by animateFloatAsState(
        targetValue = if (pressed) 1f else 0f,
        animationSpec = CalcQuickTween,
        label = "keyPress"
    )
    val scaleValue by animateFloatAsState(
        targetValue = if (pressed) 0.92f else 1f,
        animationSpec = CalcKeySpring,
        label = "keyScale"
    )

    val neutral = if (isDark) DarkKeySurface else KeySurface
    val alt = if (isDark) DarkKeySurfaceAlt else KeySurfaceAlt
    val soft = if (isDark) ForestGreenSoftDark else ForestGreenSoft
    val washed = if (isDark) ForestGreenSoftDark else ForestGreenSoft

    val baseSurface = when (style) {
        CalcKeyStyle.Digit -> neutral
        CalcKeyStyle.Function -> alt
        CalcKeyStyle.Operator -> soft
        CalcKeyStyle.Accent -> ForestGreen
    }
    val surface = if (style == CalcKeyStyle.Accent) {
        baseSurface
    } else {
        lerp(baseSurface, washed, press * 0.85f)
    }

    val textColor = when (style) {
        CalcKeyStyle.Accent -> Color.White
        CalcKeyStyle.Operator -> if (isDark) ForestGreenGlow else ForestGreen
        CalcKeyStyle.Function -> if (isDark) ForestGreenGlow else ForestGreenRich
        CalcKeyStyle.Digit -> if (isDark) DarkDisplayText else DisplayText
    }

    val container = modifier
        .scale(scaleValue)
        .clip(CalcBigKeyShape)
        .then(
            if (style == CalcKeyStyle.Accent) {
                Modifier.background(CalcAccentBrush)
            } else {
                Modifier
                    .background(surface)
                    .then(
                        if (style == CalcKeyStyle.Digit) {
                            Modifier.border(
                                width = 1.dp,
                                color = if (isDark) DarkHairlineBorder else HairlineBorder,
                                shape = CalcBigKeyShape
                            )
                        } else {
                            Modifier
                        }
                    )
            }
        )
        .clickable(
            interactionSource = interaction,
            indication = null,
            onClick = onClick
        )

    Box(modifier = container, contentAlignment = Alignment.Center) {
        if (icon != null) {
            Image(
                imageVector = icon,
                contentDescription = label,
                colorFilter = ColorFilter.tint(textColor),
                modifier = Modifier.size(22.dp)
            )
        } else {
            Text(
                text = label,
                color = textColor,
                fontSize = fontSize,
                fontWeight = when (style) {
                    CalcKeyStyle.Accent, CalcKeyStyle.Operator -> FontWeight.SemiBold
                    else -> FontWeight.Medium
                },
                maxLines = 1,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ColumnScope.CalcKeypadRow(
    index: Int,
    content: @Composable RowScope.() -> Unit
) {
    // Rows rise into place one after another on first draw.
    val appear = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        delay(34L * index)
        appear.animateTo(targetValue = 1f, animationSpec = tween(durationMillis = 320))
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .alpha(appear.value)
            .scale(0.96f + 0.04f * appear.value),
        horizontalArrangement = Arrangement.spacedBy(CalcKeyGap),
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )
}

/** The classic 4 × 5 keypad, tuned for the forest green palette. */
@Composable
fun CalculatorKeypad(
    state: CalculatorUiState,
    onDigit: (Char) -> Unit,
    onDecimal: () -> Unit,
    onOperator: (Char) -> Unit,
    onEquals: () -> Unit,
    onClear: () -> Unit,
    onBackspace: () -> Unit,
    onPercent: () -> Unit,
    onToggleSign: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dark = state.isDark

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(CalcKeyGap)
    ) {
        CalcKeypadRow(index = 0) {
            CalcKey(
                label = "AC",
                onClick = onClear,
                modifier = Modifier.fillMaxHeight().weight(1f),
                style = CalcKeyStyle.Function,
                isDark = dark,
                fontSize = 18.sp
            )
            CalcKey(
                label = "⌫",
                onClick = onBackspace,
                modifier = Modifier.fillMaxHeight().weight(1f),
                style = CalcKeyStyle.Function,
                isDark = dark,
                fontSize = 18.sp
            )
            CalcKey(
                label = "%",
                onClick = onPercent,
                modifier = Modifier.fillMaxHeight().weight(1f),
                style = CalcKeyStyle.Function,
                isDark = dark,
                fontSize = 20.sp
            )
            CalcKey(
                label = OP_DIVIDE.toString(),
                onClick = { onOperator(OP_DIVIDE) },
                modifier = Modifier.fillMaxHeight().weight(1f),
                style = CalcKeyStyle.Operator,
                isDark = dark,
                fontSize = 24.sp
            )
        }

        CalcKeypadRow(index = 1) {
            CalcKey(
                label = "7",
                onClick = { onDigit('7') },
                modifier = Modifier.fillMaxHeight().weight(1f),
                isDark = dark
            )
            CalcKey(
                label = "8",
                onClick = { onDigit('8') },
                modifier = Modifier.fillMaxHeight().weight(1f),
                isDark = dark
            )
            CalcKey(
                label = "9",
                onClick = { onDigit('9') },
                modifier = Modifier.fillMaxHeight().weight(1f),
                isDark = dark
            )
            CalcKey(
                label = OP_TIMES.toString(),
                onClick = { onOperator(OP_TIMES) },
                modifier = Modifier.fillMaxHeight().weight(1f),
                style = CalcKeyStyle.Operator,
                isDark = dark,
                fontSize = 24.sp
            )
        }

        CalcKeypadRow(index = 2) {
            CalcKey(
                label = "4",
                onClick = { onDigit('4') },
                modifier = Modifier.fillMaxHeight().weight(1f),
                isDark = dark
            )
            CalcKey(
                label = "5",
                onClick = { onDigit('5') },
                modifier = Modifier.fillMaxHeight().weight(1f),
                isDark = dark
            )
            CalcKey(
                label = "6",
                onClick = { onDigit('6') },
                modifier = Modifier.fillMaxHeight().weight(1f),
                isDark = dark
            )
            CalcKey(
                label = OP_MINUS.toString(),
                onClick = { onOperator(OP_MINUS) },
                modifier = Modifier.fillMaxHeight().weight(1f),
                style = CalcKeyStyle.Operator,
                isDark = dark,
                fontSize = 24.sp
            )
        }

        CalcKeypadRow(index = 3) {
            CalcKey(
                label = "1",
                onClick = { onDigit('1') },
                modifier = Modifier.fillMaxHeight().weight(1f),
                isDark = dark
            )
            CalcKey(
                label = "2",
                onClick = { onDigit('2') },
                modifier = Modifier.fillMaxHeight().weight(1f),
                isDark = dark
            )
            CalcKey(
                label = "3",
                onClick = { onDigit('3') },
                modifier = Modifier.fillMaxHeight().weight(1f),
                isDark = dark
            )
            CalcKey(
                label = OP_PLUS.toString(),
                onClick = { onOperator(OP_PLUS) },
                modifier = Modifier.fillMaxHeight().weight(1f),
                style = CalcKeyStyle.Operator,
                isDark = dark,
                fontSize = 24.sp
            )
        }

        CalcKeypadRow(index = 4) {
            CalcKey(
                label = "±",
                onClick = onToggleSign,
                modifier = Modifier.fillMaxHeight().weight(1f),
                style = CalcKeyStyle.Function,
                isDark = dark,
                fontSize = 20.sp
            )
            CalcKey(
                label = "0",
                onClick = { onDigit('0') },
                modifier = Modifier.fillMaxHeight().weight(1f),
                isDark = dark
            )
            CalcKey(
                label = DECIMAL.toString(),
                onClick = onDecimal,
                modifier = Modifier.fillMaxHeight().weight(1f),
                isDark = dark
            )
            CalcKey(
                label = "=",
                onClick = onEquals,
                modifier = Modifier.fillMaxHeight().weight(1f),
                style = CalcKeyStyle.Accent,
                isDark = dark,
                fontSize = 26.sp
            )
        }
    }
}

/* --------------------------------------------------------------------- */
/* Memory row                                                            */
/* --------------------------------------------------------------------- */

/** M+, M−, MR and MC chips shown above the keypad. */
@Composable
fun MemoryBar(
    memory: Double,
    isDark: Boolean,
    onMemoryAdd: () -> Unit,
    onMemorySubtract: () -> Unit,
    onMemoryRecall: () -> Unit,
    onMemoryClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    val hasMemory = memory != 0.0
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(CalcSciGap)
    ) {
        CalcMemoryChip(
            label = "M+",
            isDark = isDark,
            enabled = true,
            onClick = onMemoryAdd,
            modifier = Modifier.weight(1f)
        )
        CalcMemoryChip(
            label = "M−",
            isDark = isDark,
            enabled = true,
            onClick = onMemorySubtract,
            modifier = Modifier.weight(1f)
        )
        CalcMemoryChip(
            label = "MR",
            isDark = isDark,
            enabled = hasMemory,
            onClick = onMemoryRecall,
            modifier = Modifier.weight(1f)
        )
        CalcMemoryChip(
            label = "MC",
            isDark = isDark,
            enabled = hasMemory,
            onClick = onMemoryClear,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun CalcMemoryChip(
    label: String,
    isDark: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scaleValue by animateFloatAsState(
        targetValue = if (pressed && enabled) 0.93f else 1f,
        animationSpec = CalcKeySpring,
        label = "memoryScale"
    )

    val fg = when {
        isDark && enabled -> ForestGreenGlow
        enabled -> ForestGreen
        isDark -> DarkMutedText
        else -> MutedText
    }

    Box(
        modifier = modifier
            .scale(scaleValue)
            .height(34.dp)
            .clip(RoundedCornerShape(50))
            .background(if (isDark) DarkKeySurfaceAlt else KeySurfaceAlt)
            .clickable(
                interactionSource = interaction,
                indication = null,
                enabled = enabled,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = fg,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1
        )
    }
}

/* --------------------------------------------------------------------- */
/* History overlay                                                       */
/* --------------------------------------------------------------------- */

/**
 * Sheet with every saved calculation. Tapping a row hands it back to
 * [onSelect] so the expression can be reused.
 *
 * Implemented with a plain [Dialog] and a rounded card so that no
 * experimental Material API is required. The card springs up from the
 * bottom while the scrim fades in.
 */
@Composable
fun HistorySheet(
    entries: List<HistoryEntry>,
    isDark: Boolean,
    onSelect: (HistoryEntry) -> Unit,
    onClearAll: () -> Unit,
    onDismiss: () -> Unit
) {
    val titleColor = if (isDark) DarkDisplayText else DisplayText
    val mutedColor = if (isDark) DarkMutedText else MutedText
    val accentColor = if (isDark) ForestGreenGlow else ForestGreen
    val cardColor = if (isDark) DarkForestSurface else Color.White
    val sheetShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)

    // Swallows taps so that tapping inside the card never dismisses it.
    val swallow = remember { MutableInteractionSource() }

    // 1 = fully off screen, 0 = in place.
    val rise = remember { Animatable(1f) }
    LaunchedEffect(Unit) {
        rise.animateTo(targetValue = 0f, animationSpec = CalcInSpring)
    }
    val scrim = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        scrim.animateTo(targetValue = 1f, animationSpec = tween(durationMillis = 220))
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.32f * scrim.value)),
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (rise.value * 90f).dp)
                    .alpha(1f - rise.value * 0.5f)
                    .clip(sheetShape)
                    .background(cardColor)
                    .clickable(
                        interactionSource = swallow,
                        indication = null
                    ) { /* consumed */ }
                    .padding(horizontal = 20.dp)
                    .padding(top = 18.dp, bottom = 24.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "History",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = titleColor
                        )
                        Text(
                            text = if (entries.isEmpty()) {
                                "Nothing saved yet"
                            } else {
                                "${entries.size} saved calculation" +
                                    if (entries.size == 1) "" else "s"
                            },
                            fontSize = 12.sp,
                            color = mutedColor
                        )
                    }
                    if (entries.isNotEmpty()) {
                        TextButton(onClick = onClearAll) {
                            Text(
                                text = "Clear",
                                color = accentColor,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    TextButton(onClick = onDismiss) {
                        Text(
                            text = "Close",
                            color = mutedColor,
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                if (entries.isEmpty()) {
                    CalcEmptyHistory(isDark = isDark)
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 360.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(entries) { entry ->
                            CalcHistoryRow(
                                entry = entry,
                                isDark = isDark,
                                onClick = { onSelect(entry) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CalcEmptyHistory(isDark: Boolean) {
    val mutedColor = if (isDark) DarkMutedText else MutedText
    val signAlpha = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        signAlpha.animateTo(targetValue = 1f, animationSpec = tween(durationMillis = 320))
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 36.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "=",
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold,
            color = if (isDark) ForestGreenGlow else ForestGreenSoft,
            modifier = Modifier
                .alpha(signAlpha.value)
                .scale(0.8f + 0.2f * signAlpha.value)
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = "No calculations yet",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = mutedColor
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Your results will appear here",
            fontSize = 12.sp,
            color = mutedColor
        )
    }
}

@Composable
private fun CalcHistoryRow(
    entry: HistoryEntry,
    isDark: Boolean,
    onClick: () -> Unit
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scaleValue by animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        animationSpec = CalcKeySpring,
        label = "historyScale"
    )

    val bg = if (isDark) DarkKeySurface else KeySurface
    val borderColor = if (isDark) DarkHairlineBorder else HairlineBorder
    val mutedColor = if (isDark) DarkMutedText else MutedText
    val strongColor = if (isDark) DarkDisplayText else DisplayText
    val accentColor = if (isDark) ForestGreenGlow else ForestGreen
    val shape = RoundedCornerShape(16.dp)
    val time = calcFormatTime(entry.timestamp)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scaleValue)
            .clip(shape)
            .background(bg)
            .border(1.dp, borderColor, shape)
            .clickable(
                interactionSource = interaction,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Text(
            text = entry.expression,
            fontSize = 13.sp,
            color = mutedColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(Modifier.height(3.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "= " + entry.result,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = strongColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            if (time.isNotEmpty()) {
                Spacer(Modifier.width(8.dp))
                Text(text = time, fontSize = 10.sp, color = accentColor)
            }
        }
    }
}

private fun calcFormatTime(timestamp: Long): String {
    if (timestamp <= 0L) return ""
    return runCatching {
        SimpleDateFormat("d MMM · HH:mm", Locale.getDefault()).format(Date(timestamp))
    }.getOrDefault("")
}
