package com.vibecraft.generated

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Color as AndroidColor
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Walks up the context chain until the hosting [ComponentActivity] is found.
 *
 * Needed because the calculator is drawn edge to edge, which is a window
 * level operation and can only be applied from the activity.
 */
private fun Context.findComponentActivity(): ComponentActivity? {
    var current: Context? = this
    while (current is ContextWrapper) {
        if (current is ComponentActivity) return current
        current = current.baseContext
    }
    return current as? ComponentActivity
}

/**
 * Entry point of the whole app.
 *
 * Hoists the [CalculatorViewModel] here so it survives configuration changes and
 * recompositions, restores the saved history / memory / theme choice from
 * on-device storage, switches the window to a true edge-to-edge layout, and
 * then wraps everything in the premium white-and-forest-green Material 3 theme.
 *
 * On the very first frame the custom app icon (see [AppIcon]) plays a short
 * splash choreography — it eases up from a soft scale, the glow behind it
 * blooms, and once it settles the calculator screen rises into place.
 *
 * The dark flag comes straight from the view model state, so switching the
 * theme in the header immediately re-themes the whole app together with the
 * status bar and navigation bar icons.
 */
@Composable
fun AppRoot() {
    val context = LocalContext.current
    val appContext = remember(context) { context.applicationContext }
    val activity = remember(context) { context.findComponentActivity() }
    val calculatorViewModel: CalculatorViewModel = viewModel()

    // Current state drives the theme (light / dark forest).
    val state by calculatorViewModel.uiState.collectAsState()

    // Restore history, memory and theme choice from disk exactly once.
    LaunchedEffect(calculatorViewModel) {
        calculatorViewModel.attach(appContext)
    }

    // Draw behind the system bars in both themes, keeping the icons legible.
    LaunchedEffect(activity, state.isDark) {
        val dark = state.isDark
        val statusBarStyle = if (dark) {
            SystemBarStyle.dark(AndroidColor.TRANSPARENT)
        } else {
            SystemBarStyle.light(AndroidColor.TRANSPARENT, AndroidColor.TRANSPARENT)
        }
        val navigationBarStyle = if (dark) {
            SystemBarStyle.dark(AndroidColor.TRANSPARENT)
        } else {
            SystemBarStyle.light(AndroidColor.TRANSPARENT, AndroidColor.TRANSPARENT)
        }
        activity?.enableEdgeToEdge(
            statusBarStyle = statusBarStyle,
            navigationBarStyle = navigationBarStyle
        )
    }

    // Splash choreography for the hand drawn app icon.
    val iconScale = remember { Animatable(0.62f) }
    val iconAlpha = remember { Animatable(0f) }
    val glowAlpha = remember { Animatable(0f) }
    val splashExit = remember { Animatable(0f) }
    var showSplash by remember { mutableStateOf(true) }

    // Soft fade and short rise used when the calculator takes over.
    val entrance = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        coroutineScope {
            launch { glowAlpha.animateTo(1f, tween(durationMillis = 520, easing = FastOutSlowInEasing)) }
            launch { iconAlpha.animateTo(1f, tween(durationMillis = 380, easing = FastOutSlowInEasing)) }
            launch { iconScale.animateTo(1f, tween(durationMillis = 640, easing = FastOutSlowInEasing)) }
        }
        delay(660L)
        splashExit.animateTo(1f, tween(durationMillis = 320, easing = FastOutSlowInEasing))
        showSplash = false
        entrance.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 440, easing = FastOutSlowInEasing)
        )
    }

    // First positional argument is the dark-theme flag.
    CalculatorTheme(state.isDark) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (showSplash) {
                IconSplash(
                    iconScale = iconScale,
                    iconAlpha = iconAlpha,
                    glowAlpha = glowAlpha,
                    splashExit = splashExit
                )
            } else {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            alpha = entrance.value
                            translationY = (1f - entrance.value) * 26f
                        },
                    color = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.onBackground
                ) {
                    CalculatorScreen(viewModel = calculatorViewModel)
                }
            }
        }
    }
}

/**
 * The animated splash: the forest green squircle icon, a soft radial glow that
 * grows behind it and a small gold accent line underneath. Everything shares
 * the same exit value so the whole layer dissolves as one piece.
 */
@Composable
private fun IconSplash(
    iconScale: Animatable<Float, AnimationVector1D>,
    iconAlpha: Animatable<Float, AnimationVector1D>,
    glowAlpha: Animatable<Float, AnimationVector1D>,
    splashExit: Animatable<Float, AnimationVector1D>,
) {
    val glowColor = MaterialTheme.colorScheme.primary
    val gold = Color(0xFFD9B45C)

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Warm halo so the icon does not sit flat on the background.
        Canvas(
            modifier = Modifier
                .size(288.dp)
                .graphicsLayer {
                    val fade = 1f - splashExit.value
                    alpha = glowAlpha.value * fade
                    val grow = 0.88f + 0.12f * iconScale.value
                    scaleX = grow
                    scaleY = grow
                }
        ) {
            val radius = size.minDimension / 2f
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        glowColor.copy(alpha = 0.26f),
                        glowColor.copy(alpha = 0.10f),
                        Color.Transparent
                    ),
                    center = Offset(radius, radius),
                    radius = radius
                ),
                radius = radius,
                center = Offset(radius, radius)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            AppIcon(
                modifier = Modifier
                    .size(132.dp)
                    .graphicsLayer {
                        val fade = 1f - splashExit.value
                        val shrink = 1f - 0.08f * splashExit.value
                        scaleX = iconScale.value * shrink
                        scaleY = iconScale.value * shrink
                        alpha = iconAlpha.value * fade
                    }
            )
            Spacer(modifier = Modifier.height(22.dp))
            Box(
                modifier = Modifier
                    .size(width = 40.dp, height = 3.dp)
                    .graphicsLayer {
                        val fade = 1f - splashExit.value
                        alpha = iconAlpha.value * fade
                        val pull = 0.7f + 0.3f * iconScale.value
                        scaleX = pull
                    }
                    .background(color = gold, shape = RoundedCornerShape(percent = 50))
            )
        }
    }
}
