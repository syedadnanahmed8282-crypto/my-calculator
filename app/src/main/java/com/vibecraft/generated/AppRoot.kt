package com.vibecraft.generated

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * Entry point of the whole app.
 *
 * Wraps everything in the premium white-and-forest-green Material 3 theme,
 * paints a quiet base surface so the soft gradient in the screen can layer on
 * top of it, and hoists the [CalculatorViewModel] here so it survives
 * configuration changes and recompositions.
 *
 * The view model is connected to on-device storage once, which is what makes
 * the history, the memory value and the light / dark theme choice persist
 * between launches.
 */
@Composable
fun AppRoot() {
    CalculatorTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onBackground
        ) {
            val context = LocalContext.current
            val calculatorViewModel: CalculatorViewModel = viewModel()

            // Restore history, memory and theme choice from disk (idempotent).
            LaunchedEffect(calculatorViewModel) {
                calculatorViewModel.attach(context)
            }

            CalculatorScreen(viewModel = calculatorViewModel)
        }
    }
}
