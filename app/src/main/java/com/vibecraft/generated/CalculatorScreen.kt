package com.vibecraft.generated

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
/* --------------------------------------------------------------------- */

private val ScreenPadding = 16.dp
private val PadGap = 10.dp
private val SciGap = 6.dp
private val KeyCorner = 22.dp
private val MinKeyHeight = 44.dp
private const val KeypadRows = 5

/** Crisp white key body for digits in the light theme. */
private val KeyWhite = Color(0xFFFFFFFF)

/** Hairline around the green-tinted operator keys. */
private val OperatorBorder = Color(0xFFD4E8DA)
private val OperatorBorderDark = Color(0xFF2B4B36)

/** Soft green wash used behind the operator keys in the light theme. */
private val OperatorSurface: Brush = Brush.verticalGradient(
    colors = listOf(Color(0xFFF2F9F4), Color(0xFFE4F1E8))
)

/** Night page wash built from the dark forest palette. */
private val NightBackground: Brush = Brush.verticalGradient(
    colors = listOf(DarkForestBackground, Color(0xFF091A10), Color(0xFF0D2315))
)

/* --------------------------------------------------------------------- */
/* Theme aware colour helpers                                            */
/* --------------------------------------------------------------------- */

private fun digitFill(dark: Boolean): Color = if (dark) DarkKeySurface else KeyWhite
private fun functionFill(dark: Boolean): Color = if (dark) DarkKeySurfaceAlt else KeySurfaceAlt
private fun hairline(dark: Boolean): Color = if (dark) DarkHairlineBorder else HairlineBorder
private fun digitInk(dark: Boolean): Color = if (dark) DarkDisplayText else DisplayText
private fun functionInk(dark: Boolean): Color = if (dark) ForestGreenGlow else ForestGreenDeep
private fun mutedInk(dark: Boolean): Color = if (dark) DarkMutedText else MutedText
private fun accentInk(dark: Boolean): Color = if (dark) ForestGreenGlow else ForestGreen

/* --------------------------------------------------------------------- */
/* Main screen                                                           */
/* --------------------------------------------------------------------- */

/**
 * Assembles the whole Hisab experience: wordmark and toggles, the premium
 * display, the memory strip, the optional scientific panel and the numeric
 * keypad. Every key gives a light haptic tick, "=" answers with a stronger
 * one and tapping the display copies the visible value.
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
        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = if (dark) DarkForestBackground else Color.White
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(if (dark) NightBackground else PremiumBackground)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding()
                    .padding(horizontal = ScreenPadding, vertical = ScreenPadding)
            ) {
                Header(
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
                        .padding(top = 10.dp)
                )

                MemoryStrip(
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
                    Spacer(Modifier.height(8.dp))
                    ScientificPanel(
                        state = state,
                        viewModel = viewModel,
                        dark = dark,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(Modifier.height(PadGap))

                NumericKeypad(
                    dark = dark,
                    viewModel = viewModel,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(if (state.scientific) 1.3f else 1.6f)
                )
            }

            if (historyOpen) {
                HistoryPanel(
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
private fun Header(
    dark: Boolean,
    scientific: Boolean,
    onToggleTheme: () -> Unit,
    onToggleScientific: () -> Unit,
    onOpenHistory: () -> Unit
) {
    val ink = if (dark) DarkDisplayText else ForestGreenDeep
    val accent = accentInk(dark)

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
        ToolChip(active = scientific, dark = dark, onClick = onToggleScientific) {
            Text(
                text = "fx",
                color = if (scientific) Color.White else accent,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        Spacer(Modifier.width(7.dp))
        ToolChip(dark = dark, onClick = onOpenHistory) {
            Icon(
                imageVector = Icons.Filled.List,
                contentDescription = "History",
                tint = accent,
                modifier = Modifier.size(17.dp)
            )
        }
        Spacer(Modifier.width(7.dp))
        ToolChip(dark = dark, onClick = onToggleTheme) {
            ThemeGlyph(
                dark = dark,
                tint = accent,
                bite = DarkKeySurface,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun ToolChip(
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
                else Modifier.background(if (dark) DarkKeySurface else KeyWhite)
            )
            .border(1.dp, if (active) Color.Transparent else hairline(dark), shape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

/**
 * Tiny hand drawn sun / moon so the theme toggle stays dependency free.
 * In the light theme it shows a crescent (switch to dark), in the dark
 * theme a sun (switch back to light).
 */
@Composable
private fun ThemeGlyph(
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
private fun MemoryStrip(
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
        MemoryChip("MC", dark, enabled = stored, onClick = onClear)
        MemoryChip("MR", dark, enabled = stored, onClick = onRecall)
        MemoryChip("M+", dark, onClick = onAdd)
        MemoryChip("M−", dark, onClick = onSubtract)
        if (stored) {
            Text(
                text = state.memoryText,
                color = accentInk(dark),
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
private fun MemoryChip(
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
            .background(if (dark) DarkKeySurface else KeyWhite)
            .border(1.dp, hairline(dark), shape)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 11.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = accentInk(dark).copy(alpha = if (enabled) 1f else 0.35f),
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
private fun ScientificPanel(
    state: CalculatorUiState,
    viewModel: CalculatorViewModel,
    dark: Boolean,
    modifier: Modifier = Modifier
) {
    val haptics = LocalHapticFeedback.current
    val tick: () -> Unit = {
        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(SciGap)
    ) {
        SciRow {
            SciButton("sin", dark) { tick(); viewModel.onSin() }
            SciButton("cos", dark) { tick(); viewModel.onCos() }
            SciButton("tan", dark) { tick(); viewModel.onTan() }
            SciButton("ln", dark) { tick(); viewModel.onLn() }
            SciButton("√", dark) { tick(); viewModel.onSqrt() }
        }
        SciRow {
            SciButton("x²", dark) { tick(); viewModel.onSquare() }
            SciButton("π", dark) { tick(); viewModel.onPi() }
            SciButton("(", dark) { tick(); viewModel.onOpenParen() }
            SciButton(")", dark) { tick(); viewModel.onCloseParen() }
            SciButton(
                label = if (state.degrees) "DEG" else "RAD",
                dark = dark,
                active = true
            ) { tick(); viewModel.onToggleDegrees() }
        }
    }
}

@Composable
private fun SciRow(content: @Composable RowScope.() -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(SciGap),
        content = content
    )
}

@Composable
private fun RowScope.SciButton(
    label: String,
    dark: Boolean,
    active: Boolean = false,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(12.dp)
    val fill = when {
        active -> if (dark) ForestGreenSoftDark else ForestGreenSoft
        dark -> DarkKeySurface
        else -> KeyWhite
    }
    val ink = when {
        active -> accentInk(dark)
        else -> mutedInk(dark)
    }
    val stroke = when {
        active -> accentInk(dark).copy(alpha = 0.35f)
        else -> hairline(dark)
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
private fun NumericKeypad(
    dark: Boolean,
    viewModel: CalculatorViewModel,
    modifier: Modifier = Modifier
) {
    val haptics = LocalHapticFeedback.current
    val tick: () -> Unit = {
        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
    }

    BoxWithConstraints(modifier = modifier) {
        val rowHeight = ((maxHeight - PadGap * (KeypadRows - 1)) / KeypadRows)
            .coerceAtLeast(MinKeyHeight)

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(PadGap)
        ) {
            KeyRow(rowHeight) {
                FunctionButton("C", 22.sp, dark) { tick(); viewModel.onClear() }
                FunctionButton("±", 22.sp, dark) { tick(); viewModel.onToggleSign() }
                FunctionButton("%", 20.sp, dark) { tick(); viewModel.onPercent() }
                OperatorButton(OP_DIVIDE.toString(), dark) {
                    tick()
                    viewModel.onOperator(OP_DIVIDE)
                }
            }

            KeyRow(rowHeight) {
                DigitButton("7", dark) { tick(); viewModel.onDigit('7') }
                DigitButton("8", dark) { tick(); viewModel.onDigit('8') }
                DigitButton("9", dark) { tick(); viewModel.onDigit('9') }
                OperatorButton(OP_TIMES.toString(), dark) {
                    tick()
                    viewModel.onOperator(OP_TIMES)
                }
            }

            KeyRow(rowHeight) {
                DigitButton("4", dark) { tick(); viewModel.onDigit('4') }
                DigitButton("5", dark) { tick(); viewModel.onDigit('5') }
                DigitButton("6", dark) { tick(); viewModel.onDigit('6') }
                OperatorButton(OP_MINUS.toString(), dark) {
                    tick()
                    viewModel.onOperator(OP_MINUS)
                }
            }

            KeyRow(rowHeight) {
                DigitButton("1", dark) { tick(); viewModel.onDigit('1') }
                DigitButton("2", dark) { tick(); viewModel.onDigit('2') }
                DigitButton("3", dark) { tick(); viewModel.onDigit('3') }
                OperatorButton(OP_PLUS.toString(), dark) {
                    tick()
                    viewModel.onOperator(OP_PLUS)
                }
            }

            KeyRow(rowHeight) {
                DigitButton("0", dark, weight = 2f) { tick(); viewModel.onDigit('0') }
                KeyButton(
                    label = DECIMAL.toString(),
                    onClick = { tick(); viewModel.onDecimal() },
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    container = digitFill(dark),
                    content = digitInk(dark),
                    borderColor = hairline(dark),
                    elevation = if (dark) 0.dp else 2.dp,
                    fontSize = 26.sp
                )
                KeyButton(
                    label = "=",
                    onClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        viewModel.onEquals()
                    },
                    modifier = Modifier.weight(1f).fillMaxHeight(),
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
private fun KeyRow(
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
private fun RowScope.DigitButton(
    digit: String,
    dark: Boolean,
    weight: Float = 1f,
    onClick: () -> Unit
) {
    KeyButton(
        label = digit,
        onClick = onClick,
        modifier = Modifier.weight(weight).fillMaxHeight(),
        container = digitFill(dark),
        content = digitInk(dark),
        borderColor = hairline(dark),
        elevation = if (dark) 0.dp else 2.dp,
        fontSize = 26.sp
    )
}

@Composable
private fun RowScope.FunctionButton(
    label: String,
    fontSize: TextUnit,
    dark: Boolean,
    onClick: () -> Unit
) {
    KeyButton(
        label = label,
        onClick = onClick,
        modifier = Modifier.weight(1f).fillMaxHeight(),
        container = functionFill(dark),
        content = functionInk(dark),
        borderColor = hairline(dark),
        elevation = if (dark) 0.dp else 1.dp,
        fontSize = fontSize,
        fontWeight = FontWeight.Medium
    )
}

@Composable
private fun RowScope.OperatorButton(
    label: String,
    dark: Boolean,
    onClick: () -> Unit
) {
    if (dark) {
        KeyButton(
            label = label,
            onClick = onClick,
            modifier = Modifier.weight(1f).fillMaxHeight(),
            container = ForestGreenSoftDark,
            content = ForestGreenGlow,
            borderColor = OperatorBorderDark,
            elevation = 0.dp,
            fontSize = 28.sp,
            fontWeight = FontWeight.Medium
        )
    } else {
        KeyButton(
            label = label,
            onClick = onClick,
            modifier = Modifier.weight(1f).fillMaxHeight(),
            brush = OperatorSurface,
            content = ForestGreen,
            borderColor = OperatorBorder,
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
private fun KeyButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    container: Color = KeyWhite,
    brush: Brush? = null,
    content: Color = DisplayText,
    borderColor: Color = HairlineBorder,
    elevation: Dp = 2.dp,
    fontSize: TextUnit = 26.sp,
    fontWeight: FontWeight = FontWeight.Normal
) {
    val shape = RoundedCornerShape(KeyCorner)
    Box(
        modifier = modifier
            .shadow(elevation, shape, clip = false)
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
/* History sheet                                                         */
/* --------------------------------------------------------------------- */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HistoryPanel(
    history: List<HistoryEntry>,
    dark: Boolean,
    onDismiss: () -> Unit,
    onClear: () -> Unit,
    onSelect: (HistoryEntry) -> Unit
) {
    val ink = if (dark) DarkDisplayText else DisplayText
    val mute = mutedInk(dark)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = if (dark) DarkForestSurface else Color.White,
        contentColor = ink,
        scrimColor = Color.Black.copy(alpha = 0.32f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "History",
                    color = ink,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.weight(1f))
                if (history.isNotEmpty()) {
                    TextButton(onClick = onClear) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = null,
                            tint = mute,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(text = "Clear", color = mute, fontSize = 14.sp)
                    }
                }
            }

            Spacer(Modifier.height(4.dp))

            if (history.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No calculations yet",
                        color = mute,
                        fontSize = 15.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.heightIn(max = 400.dp),
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
    val mute = mutedInk(dark)
    val stamp = remember(entry.timestamp) { timeLabel(entry.timestamp) }

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
            color = accentInk(dark),
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
private fun timeLabel(timestamp: Long): String {
    if (timestamp <= 0L) return ""
    val format = SimpleDateFormat("d MMM · h:mm a", Locale.getDefault())
    return format.format(Date(timestamp))
}
