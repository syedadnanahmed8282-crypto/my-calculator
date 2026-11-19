package com.vibecraft.generated

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

/* --------------------------------------------------------------------- */
/* Layout constants                                                      */
/* --------------------------------------------------------------------- */

private val ScreenPadding = 16.dp
private val KeyGap = 10.dp
private val KeyCorner = 22.dp
private const val KeypadRows = 5
private val MinKeyHeight = 44.dp

/**
 * Main calculator surface: display on top, keypad below.
 * The display and the keypad share the vertical space by weight so the
 * whole pad always fits on screen without scrolling.
 */
@Composable
fun CalculatorScreen(
    modifier: Modifier = Modifier,
    viewModel: CalculatorViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = ScreenPadding, vertical = ScreenPadding)
        ) {
            Header()
            Display(
                state = state,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
            Spacer(Modifier.height(KeyGap))
            Keypad(
                state = state,
                viewModel = viewModel,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.65f)
            )
        }
    }
}

/* --------------------------------------------------------------------- */
/* Header: tiny green wordmark so the white screen keeps some structure   */
/* --------------------------------------------------------------------- */

@Composable
private fun Header() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 4.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Calculator",
            style = MaterialTheme.typography.titleMedium,
            color = ForestGreen,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(0.dp))
        Box(
            modifier = Modifier
                .padding(start = 8.dp)
                .height(6.dp)
                .fillMaxWidth(0.08f)
                .clip(RoundedCornerShape(3.dp))
                .background(ForestGreenSoft)
        )
    }
}

/* --------------------------------------------------------------------- */
/* Display                                                               */
/* --------------------------------------------------------------------- */

@Composable
private fun Display(
    state: CalculatorUiState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.End
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(26.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            if (state.hasSecondary) {
                Text(
                    text = state.secondaryText,
                    color = if (state.hasError) ErrorRed else MutedText,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Normal,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(Modifier.height(6.dp))

        Text(
            text = state.primaryText,
            color = if (state.hasError) ErrorRed else DisplayText,
            fontSize = displayFontSize(state.primaryText.length),
            fontWeight = FontWeight.Light,
            letterSpacing = (-0.5).sp,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.End,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/** Shrinks the big line as the expression grows so it never gets clipped. */
private fun displayFontSize(length: Int): TextUnit = when {
    length <= 8 -> 58.sp
    length <= 11 -> 48.sp
    length <= 14 -> 40.sp
    length <= 18 -> 32.sp
    else -> 26.sp
}

/* --------------------------------------------------------------------- */
/* Keypad                                                                */
/* --------------------------------------------------------------------- */

/**
 * The keypad measures the space it was given and hands every row an exact
 * height, so keys are sized with plain dp values instead of scope-limited
 * weight modifiers.
 */
@Composable
private fun Keypad(
    state: CalculatorUiState,
    viewModel: CalculatorViewModel,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier) {
        val rowHeight = ((maxHeight - KeyGap * (KeypadRows - 1)) / KeypadRows)
            .coerceAtLeast(MinKeyHeight)

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(KeyGap)
        ) {
            KeyRow(rowHeight) {
                CalcKey(
                    label = "C",
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    container = KeySurfaceAlt,
                    content = ForestGreenDeep,
                    onClick = { viewModel.onClear() }
                )
                CalcKey(
                    label = "±",
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    container = KeySurfaceAlt,
                    content = ForestGreenDeep,
                    fontSize = 24.sp,
                    onClick = { viewModel.onToggleSign() }
                )
                CalcKey(
                    label = "%",
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    container = KeySurfaceAlt,
                    content = ForestGreenDeep,
                    fontSize = 22.sp,
                    onClick = { viewModel.onPercent() }
                )
                CalcKey(
                    label = OP_DIVIDE.toString(),
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    container = ForestGreenSoft,
                    content = ForestGreen,
                    fontSize = 30.sp,
                    onClick = { viewModel.onOperator(OP_DIVIDE) }
                )
            }

            KeyRow(rowHeight) {
                DigitKey("7", viewModel)
                DigitKey("8", viewModel)
                DigitKey("9", viewModel)
                CalcKey(
                    label = OP_TIMES.toString(),
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    container = ForestGreenSoft,
                    content = ForestGreen,
                    fontSize = 30.sp,
                    onClick = { viewModel.onOperator(OP_TIMES) }
                )
            }

            KeyRow(rowHeight) {
                DigitKey("4", viewModel)
                DigitKey("5", viewModel)
                DigitKey("6", viewModel)
                CalcKey(
                    label = OP_MINUS.toString(),
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    container = ForestGreenSoft,
                    content = ForestGreen,
                    fontSize = 30.sp,
                    onClick = { viewModel.onOperator(OP_MINUS) }
                )
            }

            KeyRow(rowHeight) {
                DigitKey("1", viewModel)
                DigitKey("2", viewModel)
                DigitKey("3", viewModel)
                CalcKey(
                    label = OP_PLUS.toString(),
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    container = ForestGreenSoft,
                    content = ForestGreen,
                    fontSize = 30.sp,
                    onClick = { viewModel.onOperator(OP_PLUS) }
                )
            }

            KeyRow(rowHeight) {
                DigitKey("0", viewModel, weight = 2f)
                CalcKey(
                    label = DECIMAL.toString(),
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    container = KeySurface,
                    content = DisplayText,
                    fontSize = 30.sp,
                    onClick = { viewModel.onDecimal() }
                )
                CalcKey(
                    label = "=",
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    container = if (state.showingResult) ForestGreenDeep else ForestGreen,
                    content = Color.White,
                    fontSize = 30.sp,
                    onClick = { viewModel.onEquals() }
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
        horizontalArrangement = Arrangement.spacedBy(KeyGap),
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )
}

@Composable
private fun RowScope.DigitKey(
    digit: String,
    viewModel: CalculatorViewModel,
    weight: Float = 1f
) {
    CalcKey(
        label = digit,
        modifier = Modifier.weight(weight).fillMaxHeight(),
        container = KeySurface,
        content = DisplayText,
        onClick = { viewModel.onDigit(digit[0]) }
    )
}

@Composable
private fun CalcKey(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    container: Color = KeySurface,
    content: Color = DisplayText,
    fontSize: TextUnit = 28.sp
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(KeyCorner))
            .background(container)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = content,
            fontSize = fontSize,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Clip,
            textAlign = TextAlign.Center
        )
    }
}
