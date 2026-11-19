package com.vibecraft.generated

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale
import kotlin.math.abs

/* --------------------------------------------------------------------- */
/* Expression glyphs. The expression string is also what the display     */
/* shows, so it uses typographic operators instead of ASCII ones.        */
/* --------------------------------------------------------------------- */

internal const val OP_PLUS = '+'
internal const val OP_MINUS = '−'
internal const val OP_TIMES = '×'
internal const val OP_DIVIDE = '÷'
internal const val OP_PERCENT = '%'
internal const val DECIMAL = '.'

internal fun isBinaryOperator(symbol: Char): Boolean =
    symbol == OP_PLUS || symbol == OP_MINUS || symbol == OP_TIMES || symbol == OP_DIVIDE

private const val MAX_INPUT_DIGITS = 15
private const val MINUS_TEXT = "−"
private const val ERROR_TEXT = "Error"

/**
 * Everything the calculator screen needs to draw itself.
 *
 * @param expression the raw expression typed by the user ("12+7×3", "50%").
 * @param preview    live evaluation of [expression], empty when there is nothing useful to show.
 * @param hasError   true when the last evaluation failed (bad expression or division by zero).
 * @param showingResult true right after "=", so the next digit starts a fresh calculation.
 */
data class CalculatorUiState(
    val expression: String = "",
    val preview: String = "",
    val hasError: Boolean = false,
    val showingResult: Boolean = false
) {
    /** Big display line. */
    val primaryText: String get() = if (expression.isEmpty()) "0" else expression

    /** Small line above the big one. */
    val secondaryText: String get() = if (hasError) ERROR_TEXT else preview

    val hasSecondary: Boolean get() = secondaryText.isNotEmpty()
}

class CalculatorViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CalculatorUiState())
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    /* ----------------------------- input ----------------------------- */

    fun onDigit(digit: Char) {
        val current = _uiState.value
        val base = if (current.showingResult || current.hasError) "" else current.expression
        val start = trailingNumberStart(base)
        val trailing = base.substring(start)

        val next = when {
            trailing == "0" -> base.substring(0, start) + digit
            trailing.count { it.isDigit() } >= MAX_INPUT_DIGITS -> base
            else -> base + digit
        }
        apply(next)
    }

    fun onDecimal() {
        val current = _uiState.value
        val base = if (current.showingResult || current.hasError) "" else current.expression
        val start = trailingNumberStart(base)
        val trailing = base.substring(start)

        val next = when {
            trailing.isEmpty() -> base + "0."
            trailing.contains(DECIMAL) -> base
            else -> base + DECIMAL
        }
        apply(next)
    }

    fun onOperator(symbol: Char) {
        val current = _uiState.value
        val base = if (current.hasError) "" else current.expression

        val next = when {
            base.isEmpty() || base == MINUS_TEXT -> if (symbol == OP_MINUS) MINUS_TEXT else ""
            isBinaryOperator(base.last()) -> base.dropLast(1) + symbol
            base.last() == DECIMAL -> base.dropLast(1) + symbol
            else -> base + symbol
        }
        apply(next)
    }

    fun onPercent() {
        val current = _uiState.value
        val base = if (current.hasError) "" else current.expression
        if (base.isEmpty()) return
        val last = base.last()
        if (!(last.isDigit() || last == OP_PERCENT)) return
        apply(base + OP_PERCENT)
    }

    fun onToggleSign() {
        val current = _uiState.value
        if (current.hasError) return
        val expression = current.expression
        if (expression.isEmpty()) return

        val numberStart = trailingNumberStart(expression)
        if (numberStart == expression.length) return // nothing to flip (trailing operator or %)

        val minusIndex = numberStart - 1
        val alreadyNegated = minusIndex >= 0 &&
            expression[minusIndex] == OP_MINUS &&
            (minusIndex == 0 || isBinaryOperator(expression[minusIndex - 1]))

        val next = if (alreadyNegated) {
            expression.substring(0, minusIndex) + expression.substring(numberStart)
        } else {
            expression.substring(0, numberStart) + OP_MINUS + expression.substring(numberStart)
        }
        apply(next, showingResult = current.showingResult)
    }

    fun onBackspace() {
        val current = _uiState.value
        if (current.hasError || current.showingResult || current.expression.isEmpty()) {
            apply("")
            return
        }
        apply(current.expression.dropLast(1))
    }

    fun onClear() {
        _uiState.value = CalculatorUiState()
    }

    fun onEquals() {
        val current = _uiState.value
        if (current.hasError) {
            onClear()
            return
        }

        var expression = current.expression
        while (expression.isNotEmpty() && isBinaryOperator(expression.last())) {
            expression = expression.dropLast(1)
        }
        if (expression.isEmpty()) return

        val value = ExpressionEvaluator.evaluate(expression)
        if (value == null) {
            _uiState.value = CalculatorUiState(expression = expression, hasError = true)
            return
        }

        _uiState.value = CalculatorUiState(
            expression = formatNumber(value),
            preview = "",
            hasError = false,
            showingResult = true
        )
    }

    /* ---------------------------- internals -------------------------- */

    private fun apply(expression: String, showingResult: Boolean = false) {
        _uiState.value = CalculatorUiState(
            expression = expression,
            preview = previewFor(expression),
            hasError = false,
            showingResult = showingResult
        )
    }

    private fun trailingNumberStart(expression: String): Int {
        var index = expression.length
        while (index > 0 && (expression[index - 1].isDigit() || expression[index - 1] == DECIMAL)) {
            index--
        }
        return index
    }

    private fun previewFor(expression: String): String {
        if (expression.isEmpty()) return ""
        val last = expression.last()
        if (isBinaryOperator(last) || last == DECIMAL) return ""

        val hasOperation = expression.any {
            it == OP_PLUS || it == OP_TIMES || it == OP_DIVIDE || it == OP_PERCENT
        } || expression.drop(1).any { it == OP_MINUS }
        if (!hasOperation) return ""

        val value = ExpressionEvaluator.evaluate(expression) ?: return ""
        val text = formatNumber(value)
        return if (text == expression) "" else text
    }
}

/* --------------------------------------------------------------------- */
/* Safe evaluation: tokenizer + recursive descent parser.                */
/* No scripting, no reflection, no libraries beyond the Kotlin stdlib.   */
/* --------------------------------------------------------------------- */

private sealed interface Token {
    data class Number(val value: Double) : Token
    data class Operator(val symbol: Char) : Token
}

internal object ExpressionEvaluator {

    /** Returns the value of [expression], or null when it cannot be evaluated. */
    fun evaluate(expression: String): Double? {
        if (expression.isEmpty()) return null
        val tokens = tokenize(expression) ?: return null
        if (tokens.isEmpty()) return null
        val value = Parser(tokens).parse() ?: return null
        return if (value.isFinite()) value else null
    }

    private fun tokenize(source: String): List<Token>? {
        val tokens = ArrayList<Token>(source.length)
        var index = 0
        while (index < source.length) {
            val char = source[index]
            when {
                char.isDigit() || char == DECIMAL -> {
                    val start = index
                    var seenDot = false
                    while (index < source.length &&
                        (source[index].isDigit() || source[index] == DECIMAL)
                    ) {
                        if (source[index] == DECIMAL) {
                            if (seenDot) return null
                            seenDot = true
                        }
                        index++
                    }
                    val value = source.substring(start, index).toDoubleOrNull() ?: return null
                    tokens.add(Token.Number(value))
                }

                isBinaryOperator(char) -> {
                    tokens.add(Token.Operator(char))
                    index++
                }

                char == OP_PERCENT -> {
                    tokens.add(Token.Operator(OP_PERCENT))
                    index++
                }

                // Accept ASCII fallbacks in case they ever reach us.
                char == '-' -> {
                    tokens.add(Token.Operator(OP_MINUS))
                    index++
                }

                char == '*' -> {
                    tokens.add(Token.Operator(OP_TIMES))
                    index++
                }

                char == '/' -> {
                    tokens.add(Token.Operator(OP_DIVIDE))
                    index++
                }

                else -> return null
            }
        }
        return tokens
    }

    private class Parser(private val tokens: List<Token>) {

        private var position = 0

        fun parse(): Double? {
            if (tokens.isEmpty()) return null
            val value = expression() ?: return null
            return if (position == tokens.size) value else null
        }

        private fun peek(): Token? = tokens.getOrNull(position)

        /** expression := term (('+' | '−') term)* */
        private fun expression(): Double? {
            var left = term() ?: return null
            while (true) {
                val token = peek()
                if (token is Token.Operator && (token.symbol == OP_PLUS || token.symbol == OP_MINUS)) {
                    position++
                    val right = term() ?: return null
                    left = if (token.symbol == OP_PLUS) left + right else left - right
                } else {
                    return left
                }
            }
        }

        /** term := unary (('×' | '÷') unary)* */
        private fun term(): Double? {
            var left = unary() ?: return null
            while (true) {
                val token = peek()
                if (token is Token.Operator && (token.symbol == OP_TIMES || token.symbol == OP_DIVIDE)) {
                    position++
                    val right = unary() ?: return null
                    if (token.symbol == OP_DIVIDE && right == 0.0) return null
                    left = if (token.symbol == OP_TIMES) left * right else left / right
                } else {
                    return left
                }
            }
        }

        /** unary := ('+' | '−')? postfix */
        private fun unary(): Double? {
            val token = peek()
            if (token is Token.Operator && (token.symbol == OP_PLUS || token.symbol == OP_MINUS)) {
                position++
                val value = unary() ?: return null
                return if (token.symbol == OP_MINUS) -value else value
            }
            return postfix()
        }

        /** postfix := number '%'* */
        private fun postfix(): Double? {
            val token = peek()
            if (token !is Token.Number) return null
            position++
            var value = token.value
            while (true) {
                val next = peek()
                if (next is Token.Operator && next.symbol == OP_PERCENT) {
                    position++
                    value /= 100.0
                } else {
                    return value
                }
            }
        }
    }
}

/* --------------------------------------------------------------------- */
/* Number formatting                                                     */
/* --------------------------------------------------------------------- */

internal fun formatNumber(value: Double): String {
    if (!value.isFinite()) return ERROR_TEXT
    if (value == 0.0) return "0"

    val magnitude = abs(value)
    val raw = if (magnitude >= 1e12 || magnitude < 1e-9) {
        String.format(Locale.US, "%.8e", value)
    } else {
        String.format(Locale.US, "%.10f", value)
    }
    return trimTrailingZeros(raw)
}

private fun trimTrailingZeros(text: String): String {
    val exponentIndex = text.indexOfFirst { it == 'e' || it == 'E' }
    val mantissa = if (exponentIndex >= 0) text.substring(0, exponentIndex) else text
    val exponent = if (exponentIndex >= 0) text.substring(exponentIndex) else ""

    var end = mantissa.length
    if (mantissa.contains('.')) {
        while (end > 0 && mantissa[end - 1] == '0') end--
        if (end > 0 && mantissa[end - 1] == '.') end--
    }
    return mantissa.substring(0, end) + exponent
}
