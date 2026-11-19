package com.vibecraft.generated

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * Entry point of the whole app.
 *
 * Hoists the [CalculatorViewModel] here so it survives configuration changes
 * and recompositions, restores the saved history / memory / theme choice from
 * on-device storage, and then wraps the calculator screen in the premium
 * white-and-forest-green Material 3 theme.
 *
 * The dark flag comes straight from the view model state, so switching the
 * theme in the header immediately re-themes the whole app.
 */
@Composable
fun AppRoot() {
    val context = LocalContext.current
    val calculatorViewModel: CalculatorViewModel = viewModel()

    // Current state drives the theme (light / dark forest).
    val state by calculatorViewModel.uiState.collectAsState()

    // Restore history, memory and theme choice from disk exactly once.
    LaunchedEffect(calculatorViewModel) {
        calculatorViewModel.attach(context)
    }

    // First (and only) positional argument is the dark-theme flag.
    CalculatorTheme(state.isDark) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onBackground
        ) {
            CalculatorScreen(viewModel = calculatorViewModel)
        }
    }
}
