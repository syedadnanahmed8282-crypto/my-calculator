package com.vibecraft.generated

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
/* Layout constants                                                      */
/*                                                                       */
/* Everything here is private to this file and carries a distinct name    */
/* so it can never clash with a declaration in another file of the        */
/* package (CalcComponents.kt owns the shared "Calc*" pieces).            */
/* --------------------------------------------------------------------- */

private val ScreenPadding = 16.dp
private val PadSpacing = 9.dp
private val SciSpacing = 6.dp
private val KeyRadius = 22.dp
private val MinKeyHeight = 44.dp
private const val PadRowCount = 5

/** Crisp white key body for digits in the light theme. */
private val KeyWhiteFill = Color(0xFFFFFFFF)

/** Hairline around the green tinted operator keys. */
private val OperatorLine = Color(0xFFD4E8DA)
private val OperatorLineDark = Color(0xFF2B4B36)

/** Soft green wash used behind the operator keys in the light theme. */
private val OperatorWash: Brush = Brush.verticalGradient(
    colors = listOf(Color(0xFFF2F9F4), Color(0xFFE4F1E8))
)

/** Night page wash built from the dark forest palette. */
private val NightWash: Brush = Brush.verticalGradient(
    colors = listOf(DarkForestBackground, Color(0xFF091A10), Color(0xFF0D2315))
)

/* --------------------------------------------------------------------- */
/* Theme aware colour helpers                                            */
/* --------------------------------------------------------------------- */

private fun padDigitFill(dark: Boolean): Color = if (dark) DarkKeySurface else KeyWhiteFill
private fun padFunctionFill(dark: Boolean): Color = if (dark) DarkKeySurfaceAlt else KeySurfaceAlt
private fun padHairline(dark: Boolean): Color = if (dark) DarkHairlineBorder else HairlineBorder
private fun padDigitInk(dark: Boolean): Color = if (dark) DarkDisplayText else DisplayText
private fun padFunctionInk(dark: Boolean): Color = if (dark) ForestGreenGlow else ForestGreenDeep
private fun padMutedInk(dark: Boolean): Color = if (dark) DarkMutedText else MutedText
private fun padAccentInk(dark: Boolean): Color = if (dark) ForestGreenGlow else ForestGreen

/* --------------------------------------------------------------------- */
/* Main screen                                                           */
/* --------------------------------------------------------------------- */

/**
 * Assembles the whole Hisab experience: wordmark and tool chips, the premium
 * display, the memory strip, the optional scientific panel and the numeric
 * keypad. Every key gives a light haptic tick, "=" answers with a stronger
 * one and tapping the display copies the visible value.
 *
 * The screen only talks to [CalculatorViewModel] here; the smaller building
 * blocks below receive plain callbacks so nothing else has to know about the
 * view model.
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

    // Connects the calculator to local storage (history, memory, theme).
    LaunchedEffect(Unit) { viewModel.attach(context) }

    val tick: () -> Unit = {
        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = if (dark) DarkForestBackground else Color.White
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(if (dark) NightWash else PremiumBackground)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding()
                    .padding(horizontal = ScreenPadding, vertical = 12.dp)
            ) {
                HeaderBar(
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

                CalculatorDisplay(
                    state = state,
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
                        .padding(top = 8.dp)
                )

                MemoryBar(
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

                if (state.scientific) {
                    Spacer(Modifier.height(7.dp))
                    ScientificPad(
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
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(Modifier.height(PadSpacing))

                NumericPad(
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
                        .weight(if (state.scientific) 1.35f else 1.75f)
                )
            }

            if (historyOpen) {
                HistoryOverlay(
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
}

/* --------------------------------------------------------------------- */
/* Header: wordmark on the left, three glossy tool chips on the right    */
/* --------------------------------------------------------------------- */

@Composable
private fun HeaderBar(
    dark: Boolean,
    scientific: Boolean,
    onToggleTheme: () -> Unit,
    onToggleScientific: () -> Unit,
    onOpenHistory: () -> Unit
) {
    val ink = if (dark) DarkDisplayText else ForestGreenDeep
    val accent = padAccentInk(dark)

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(9.dp)
                .clip(CircleShape)
                .background(AccentGradient)
        )
        Spacer(Modifier.width(9.dp))
        Text(
            text = "Hisab",
            color = ink,
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.4.sp
        )
        Spacer(Modifier.weight(1f))
        ToolChipButton(active = scientific, dark = dark, onClick = onToggleScientific) {
            Text(
                text = "fx",
                color = if (scientific) Color.White else accent,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        Spacer(Modifier.width(7.dp))
        ToolChipButton(dark = dark, onClick = onOpenHistory) {
            Text(
                text = "≡",
                color = accent,
                fontSize = 19.sp,
                fontWeight = FontWeight.Medium
            )
        }
        Spacer(Modifier.width(7.dp))
        ToolChipButton(dark = dark, onClick = onToggleTheme) {
            ThemeGlyphIcon(
                dark = dark,
                tint = accent,
                bite = if (dark) DarkKeySurface else KeyWhiteFill,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun ToolChipButton(
    dark: Boolean,
    onClick: () -> Unit,
    active: Boolean = false,
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(11.dp)
    Box(
        modifier = Modifier
            .size(34.dp)
            .clip(shape)
            .then(
                if (active) Modifier.background(AccentGradient)
                else Modifier.background(if (dark) DarkKeySurface else KeyWhiteFill)
            )
            .border(1.dp, if (active) Color.Transparent else padHairline(dark), shape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

/**
 * Tiny hand drawn sun / moon so the theme toggle stays dependency free.
 * In the light theme it shows a crescent (switch to dark), in the dark
 * theme a sun with rays (switch back to light).
 */
@Composable
private fun ThemeGlyphIcon(
    dark: Boolean,
    tint: Color,
    bite: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val radius = size.minDimension / 2f
        val middle = center
        if (dark) {
            drawCircle(color = tint, radius = radius * 0.38f, center = middle)
            drawCircle(
                color = tint,
                radius = radius * 0.74f,
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
/* Memory strip                                                          */
/* --------------------------------------------------------------------- */

@Composable
private fun MemoryBar(
    state: CalculatorUiState,
    dark: Boolean,
    onRecall: () -> Unit,
    onClear: () -> Unit,
    onAdd: () -> Unit,
    onSubtract: () -> Unit,
    modifier: Modifier = Modifier
) {
    val stored = state.hasMemory
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MemoryChipButton("MC", dark, enabled = stored, onClick = onClear)
        MemoryChipButton("MR", dark, enabled = stored, onClick = onRecall)
        MemoryChipButton("M+", dark, onClick = onAdd)
        MemoryChipButton("M−", dark, onClick = onSubtract)
        if (stored) {
            Text(
                text = state.memoryText,
                color = padAccentInk(dark),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.End,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun MemoryChipButton(
    label: String,
    dark: Boolean,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(9.dp)
    Box(
        modifier = Modifier
            .height(28.dp)
            .clip(shape)
            .background(if (dark) DarkKeySurface else KeyWhiteFill)
            .border(1.dp, padHairline(dark), shape)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 11.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = padAccentInk(dark).copy(alpha = if (enabled) 1f else 0.35f),
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
private fun ScientificPad(
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
        verticalArrangement = Arrangement.spacedBy(SciSpacing)
    ) {
        SciPadRow {
            SciKeyButton("sin", dark, onClick = onSin)
            SciKeyButton("cos", dark, onClick = onCos)
            SciKeyButton("tan", dark, onClick = onTan)
            SciKeyButton("ln", dark, onClick = onLn)
            SciKeyButton("√", dark, onClick = onSqrt)
        }
        SciPadRow {
            SciKeyButton("x2", dark, onClick = onSquare)
            SciKeyButton("π", dark, onClick = onPi)
            SciKeyButton("(", dark, onClick = onOpenParen)
            SciKeyButton(")", dark, onClick = onCloseParen)
            SciKeyButton(
                label = if (state.degrees) "DEG" else "RAD",
                dark = dark,
                active = true,
                onClick = onToggleDegrees
            )
        }
    }
}

@Composable
private fun SciPadRow(content: @Composable RowScope.() -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(SciSpacing),
        content = content
    )
}

@Composable
private fun RowScope.SciKeyButton(
    label: String,
    dark: Boolean,
    active: Boolean = false,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(12.dp)
    val fill = when {
        active -> if (dark) ForestGreenSoftDark else ForestGreenSoft
        dark -> DarkKeySurface
        else -> KeyWhiteFill
    }
    val ink = when {
        active -> padAccentInk(dark)
        else -> padMutedInk(dark)
    }
    val stroke = when {
        active -> padAccentInk(dark).copy(alpha = 0.35f)
        else -> padHairline(dark)
    }

    Box(
        modifier = Modifier
            .weight(1f)
            .height(34.dp)
            .clip(shape)
            .background(fill)
            .border(1.dp, stroke, shape)
            .clickable(onClick = onClick),
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
private fun NumericPad(
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
        val rowHeight = ((maxHeight - PadSpacing * (PadRowCount - 1)) / PadRowCount)
            .coerceAtLeast(MinKeyHeight)

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(PadSpacing)
        ) {
            PadRow(rowHeight) {
                FunctionKey("C", 22.sp, dark, onClick = onClear)
                FunctionKey("±", 22.sp, dark, onClick = onToggleSign)
                FunctionKey("%", 20.sp, dark, onClick = onPercent)
                OperatorKey(OP_DIVIDE.toString(), dark) { onOperator(OP_DIVIDE) }
            }

            PadRow(rowHeight) {
                DigitKey("7", dark) { onDigit('7') }
                DigitKey("8", dark) { onDigit('8') }
                DigitKey("9", dark) { onDigit('9') }
                OperatorKey(OP_TIMES.toString(), dark) { onOperator(OP_TIMES) }
            }

            PadRow(rowHeight) {
                DigitKey("4", dark) { onDigit('4') }
                DigitKey("5", dark) { onDigit('5') }
                DigitKey("6", dark) { onDigit('6') }
                OperatorKey(OP_MINUS.toString(), dark) { onOperator(OP_MINUS) }
            }

            PadRow(rowHeight) {
                DigitKey("1", dark) { onDigit('1') }
                DigitKey("2", dark) { onDigit('2') }
                DigitKey("3", dark) { onDigit('3') }
                OperatorKey(OP_PLUS.toString(), dark) { onOperator(OP_PLUS) }
            }

            PadRow(rowHeight) {
                DigitKey("0", dark, weight = 2f) { onDigit('0') }
                KeyCap(
                    label = DECIMAL.toString(),
                    onClick = onDecimal,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    container = padDigitFill(dark),
                    content = padDigitInk(dark),
                    borderColor = padHairline(dark),
                    elevation = if (dark) 0.dp else 2.dp,
                    fontSize = 26.sp
                )
                KeyCap(
                    label = "=",
                    onClick = onEquals,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    brush = AccentGradient,
                    content = Color.White,
                    borderColor = Color.Transparent,
                    elevation = if (dark) 2.dp else 6.dp,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun PadRow(
    rowHeight: Dp,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(rowHeight),
        horizontalArrangement = Arrangement.spacedBy(PadSpacing),
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )
}

@Composable
private fun RowScope.DigitKey(
    digit: String,
    dark: Boolean,
    weight: Float = 1f,
    onClick: () -> Unit
) {
    KeyCap(
        label = digit,
        onClick = onClick,
        modifier = Modifier
            .weight(weight)
            .fillMaxHeight(),
        container = padDigitFill(dark),
        content = padDigitInk(dark),
        borderColor = padHairline(dark),
        elevation = if (dark) 0.dp else 2.dp,
        fontSize = 26.sp
    )
}

@Composable
private fun RowScope.FunctionKey(
    label: String,
    fontSize: TextUnit,
    dark: Boolean,
    onClick: () -> Unit
) {
    KeyCap(
        label = label,
        onClick = onClick,
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight(),
        container = padFunctionFill(dark),
        content = padFunctionInk(dark),
        borderColor = padHairline(dark),
        elevation = if (dark) 0.dp else 1.dp,
        fontSize = fontSize,
        fontWeight = FontWeight.Medium
    )
}

@Composable
private fun RowScope.OperatorKey(
    label: String,
    dark: Boolean,
    onClick: () -> Unit
) {
    if (dark) {
        KeyCap(
            label = label,
            onClick = onClick,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            container = ForestGreenSoftDark,
            content = ForestGreenGlow,
            borderColor = OperatorLineDark,
            elevation = 0.dp,
            fontSize = 28.sp,
            fontWeight = FontWeight.Medium
        )
    } else {
        KeyCap(
            label = label,
            onClick = onClick,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            brush = OperatorWash,
            content = ForestGreen,
            borderColor = OperatorLine,
            elevation = 2.dp,
            fontSize = 28.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * A single premium key: soft lift, hairline outline and either a flat
 * colour or a gradient fill.
 */
@Composable
private fun KeyCap(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    container: Color = KeyWhiteFill,
    brush: Brush? = null,
    content: Color = DisplayText,
    borderColor: Color = HairlineBorder,
    elevation: Dp = 2.dp,
    fontSize: TextUnit = 26.sp,
    fontWeight: FontWeight = FontWeight.Normal
) {
    val shape = RoundedCornerShape(KeyRadius)
    Box(
        modifier = modifier
            .shadow(elevation, shape)
            .clip(shape)
            .then(
                if (brush != null) Modifier.background(brush)
                else Modifier.background(container)
            )
            .border(1.dp, borderColor, shape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = content,
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
/* History panel (custom overlay, no experimental APIs)                  */
/* --------------------------------------------------------------------- */

@Composable
private fun HistoryOverlay(
    history: List<HistoryEntry>,
    dark: Boolean,
    onDismiss: () -> Unit,
    onClear: () -> Unit,
    onSelect: (HistoryEntry) -> Unit
) {
    val ink = if (dark) DarkDisplayText else DisplayText
    val mute = padMutedInk(dark)
    val panelColor = if (dark) DarkForestSurface else Color.White
    val scrimInteraction = remember { MutableInteractionSource() }

    Box(modifier = Modifier.fillMaxSize()) {
        // Tapping the dimmed area closes the history.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.34f))
                .clickable(
                    interactionSource = scrimInteraction,
                    indication = null,
                    onClick = onDismiss
                )
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp))
                .background(panelColor)
                .padding(horizontal = 18.dp)
                .padding(top = 16.dp, bottom = 22.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "History",
                    color = ink,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.weight(1f))
                if (history.isNotEmpty()) {
                    Text(
                        text = "Clear",
                        color = padAccentInk(dark),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable(onClick = onClear)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Spacer(Modifier.width(2.dp))
                Text(
                    text = "✕",
                    color = mute,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(onClick = onDismiss)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            Spacer(Modifier.height(8.dp))

            if (history.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No calculations yet",
                        color = mute,
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.heightIn(max = 320.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(history) { entry ->
                        HistoryRow(
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
private fun HistoryRow(
    entry: HistoryEntry,
    dark: Boolean,
    onClick: () -> Unit
) {
    val mute = padMutedInk(dark)
    val stamp = remember(entry.timestamp) { formatHistoryStamp(entry.timestamp) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (dark) DarkKeySurface else ForestGreenSoft)
            .clickable(onClick = onClick)
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
            color = padAccentInk(dark),
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
                maxLines = 1
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
