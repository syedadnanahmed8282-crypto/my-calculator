package com.vibecraft.generated

import android.content.Context
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.log10
import kotlin.math.round
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan

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
private const val MAX_HISTORY = 100
private const val MINUS_TEXT = "−"
private const val ERROR_TEXT = "Error"

/**
 * Everything the calculator screen needs to draw itself.
 *
 * @param expression the raw expression typed by the user ("12+7×3", "50%").
 * @param preview    live evaluation of [expression], empty when there is nothing useful to show.
 * @param hasError   true when the last evaluation failed (bad expression or division by zero).
 * @param showingResult true right after "=", so the next digit starts a fresh calculation.
 * @param memory     value kept in calculator memory (0.0 when the memory is empty).
 * @param history    saved calculations, newest first.
 * @param isDark     true when the user picked the dark theme.
 * @param scientific true when the scientific keypad is expanded.
 * @param degrees    true when trigonometry works in degrees, false for radians.
 */
data class CalculatorUiState(
    val expression: String = "",
    val preview: String = "",
    val hasError: Boolean = false,
    val showingResult: Boolean = false,
    val memory: Double = 0.0,
    val history: List<HistoryEntry> = emptyList(),
    val isDark: Boolean = false,
    val scientific: Boolean = false,
    val degrees: Boolean = true
) {
    /** Big display line. */
    val primaryText: String get() = if (expression.isEmpty()) "0" else expression

    /** Small line above the big one. */
    val secondaryText: String get() = if (hasError) ERROR_TEXT else preview

    val hasSecondary: Boolean get() = secondaryText.isNotEmpty()

    /** True when something is stored in memory. */
    val hasMemory: Boolean get() = memory != 0.0

    /** Short label for the memory badge. */
    val memoryText: String get() = formatNumber(memory)

    /** Text handed to the clipboard when the user taps the display. */
    val copyText: String get() = when {
        hasError -> primaryText
        preview.isNotEmpty() -> preview
        else -> primaryText
    }
}

class CalculatorViewModel : ViewModel() {

    /** Optional convenience constructor for screens that already have a Context. */
    constructor(context: Context) : this() {
        attach(context)
    }

    private var store: HistoryStore? = null

    private val _uiState = MutableStateFlow(CalculatorUiState())
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    /* --------------------------- persistence -------------------------- */

    /**
     * Connects the view model to on-device storage so history, memory and the
     * theme choice survive restarts. Safe to call more than once.
     */
    fun attach(context: Context) {
        if (store != null) return
        val created = HistoryStore(context)
        store = created
        _uiState.value = _uiState.value.copy(
            history = created.loadHistory(),
            memory = created.loadMemory(),
            isDark = created.loadDarkMode()
        )
    }

    /* ----------------------------- input ------------------------------ */

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
            onClear()
            return
        }
        apply(current.expression.dropLast(1))
    }

    fun onClear() {
        _uiState.value = _uiState.value.copy(
            expression = "",
            preview = "",
            hasError = false,
            showingResult = false
        )
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
            _uiState.value = current.copy(
                expression = expression,
                preview = "",
                hasError = true,
                showingResult = false
            )
            return
        }

        val result = formatNumber(value)
        val history = if (result == expression) {
            current.history
        } else {
            saveEntry(
                HistoryEntry(
                    expression = expression,
                    result = result,
                    timestamp = System.currentTimeMillis()
                )
            )
        }

        _uiState.value = current.copy(
            expression = result,
            preview = "",
            hasError = false,
            showingResult = true,
            history = history
        )
    }

    /* ------------------------------ memory ---------------------------- */

    fun onMemoryAdd() {
        setMemory(_uiState.value.memory + currentValue())
    }

    fun onMemorySubtract() {
        setMemory(_uiState.value.memory - currentValue())
    }

    fun onMemoryStore() {
        setMemory(currentValue())
    }

    fun onMemoryRecall() {
        val current = _uiState.value
        _uiState.value = current.copy(
            expression = formatNumber(current.memory),
            preview = "",
            hasError = false,
            showingResult = true
        )
    }

    fun onMemoryClear() {
        store?.clearMemory()
        _uiState.value = _uiState.value.copy(memory = 0.0)
    }

    /* --------------------------- scientific --------------------------- */

    fun onToggleScientific() {
        _uiState.value = _uiState.value.copy(scientific = !_uiState.value.scientific)
    }

    fun onToggleAngle() {
        _uiState.value = _uiState.value.copy(degrees = !_uiState.value.degrees)
    }

    /** Applies a scientific operation to the value currently on the display. */
    fun onFunction(name: String) {
        val current = _uiState.value
        val base = if (current.hasError) "" else current.expression
        val input = if (base.isEmpty()) 0.0 else (ExpressionEvaluator.evaluate(base) ?: return)

        val result: Double? = when (name.lowercase(Locale.US)) {
            "sin" -> sin(angleToRadians(input))
            "cos" -> cos(angleToRadians(input))
            "tan" -> {
                val radians = angleToRadians(input)
                if (abs(cos(radians)) < 1e-12) null else sin(radians) / cos(radians)
            }
            "ln" -> if (input > 0.0) ln(input) else null
            "log" -> if (input > 0.0) log10(input) else null
            "sqrt", "√" -> if (input >= 0.0) sqrt(input) else null
            "sqr", "square", "x²" -> input * input
            "inv", "1/x" -> if (input != 0.0) 1.0 / input else null
            "exp", "eˣ" -> exp(input)
            "fact", "n!" -> factorial(input)
            "neg" -> -input
            else -> return
        }

        if (result == null || !result.isFinite()) {
            _uiState.value = current.copy(
                expression = base,
                preview = "",
                hasError = true,
                showingResult = false
            )
            return
        }

        _uiState.value = current.copy(
            expression = formatNumber(cleanValue(result)),
            preview = "",
            hasError = false,
            showingResult = true
        )
    }

    fun onSin() = onFunction("sin")
    fun onCos() = onFunction("cos")
    fun onTan() = onFunction("tan")
    fun onLn() = onFunction("ln")
    fun onLog() = onFunction("log")
    fun onSqrt() = onFunction("sqrt")
    fun onSquare() = onFunction("sqr")
    fun onReciprocal() = onFunction("inv")
    fun onFactorial() = onFunction("fact")

    /** Drops π or e into the expression as a number. */
    fun onConstant(symbol: String) {
        when (symbol) {
            "π", "pi", "PI" -> insertValue(PI)
            "e", "E" -> insertValue(Math.E)
        }
    }

    fun onPi() = onConstant("π")
    fun onEuler() = onConstant("e")

    /* ------------------------------ history --------------------------- */

    fun onHistorySelected(entry: HistoryEntry) {
        val current = _uiState.value
        _uiState.value = current.copy(
            expression = entry.result,
            preview = "",
            hasError = false,
            showingResult = true
        )
    }

    fun onClearHistory() {
        store?.clearHistory()
        _uiState.value = _uiState.value.copy(history = emptyList())
    }

    /* ------------------------------- theme ---------------------------- */

    fun onToggleTheme() {
        val next = !_uiState.value.isDark
        store?.saveDarkMode(next)
        _uiState.value = _uiState.value.copy(isDark = next)
    }

    fun toggleTheme() = onToggleTheme()

    fun setDarkMode(enabled: Boolean) {
        if (_uiState.value.isDark == enabled) return
        store?.saveDarkMode(enabled)
        _uiState.value = _uiState.value.copy(isDark = enabled)
    }

    /* ----------------------------- helpers ---------------------------- */

    /** The text the screen should copy to the clipboard. */
    fun copyText(): String = _uiState.value.copyText

    private fun apply(expression: String, showingResult: Boolean = false) {
        _uiState.value = _uiState.value.copy(
            expression = expression,
            preview = previewFor(expression),
            hasError = false,
            showingResult = showingResult
        )
    }

    private fun saveEntry(entry: HistoryEntry): List<HistoryEntry> {
        val saved = store
        if (saved != null) return saved.addEntry(entry)
        return (listOf(entry) + _uiState.value.history).take(MAX_HISTORY)
    }

    private fun setMemory(value: Double) {
        val safe = if (value.isFinite()) cleanValue(value) else 0.0
        store?.saveMemory(safe)
        _uiState.value = _uiState.value.copy(memory = safe)
    }

    private fun currentValue(): Double {
        val current = _uiState.value
        if (current.hasError) return 0.0
        val expression = current.expression
        if (expression.isEmpty()) return 0.0
        return ExpressionEvaluator.evaluate(expression) ?: 0.0
    }

    private fun insertValue(value: Double) {
        val current = _uiState.value
        val base = if (current.showingResult || current.hasError) "" else current.expression
        val start = trailingNumberStart(base)
        val next = base.substring(0, start) + formatNumber(value)
        apply(next)
    }

    private fun angleToRadians(value: Double): Double =
        if (_uiState.value.degrees) Math.toRadians(value) else value

    private fun factorial(value: Double): Double? {
        if (value < 0.0 || value > 170.0) return null
        if (abs(value - round(value)) > 1e-9) return null
        var result = 1.0
        var index = 2
        val limit = value.toInt()
        while (index <= limit) {
            result *= index
            index++
        }
        return result
    }

    private fun cleanValue(value: Double): Double {
        if (!value.isFinite()) return value
        if (abs(value) < 1e-12) return 0.0
        if (abs(value) < 1e12) return round(value * 1e10) / 1e10
        return value
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
