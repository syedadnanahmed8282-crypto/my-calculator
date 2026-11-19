package com.vibecraft.generated

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * Entry point of the whole app.
 *
 * Wires the white and forest green Material 3 theme around the calculator
 * screen and hoists the [CalculatorViewModel] here so it survives
 * configuration changes and recompositions.
 */
@Composable
fun AppRoot() {
    CalculatorTheme {
        val calculatorViewModel: CalculatorViewModel = viewModel()
        CalculatorScreen(viewModel = calculatorViewModel)
    }
}
