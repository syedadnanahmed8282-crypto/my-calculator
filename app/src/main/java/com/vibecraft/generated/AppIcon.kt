package com.vibecraft.generated

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics

/**
 * The app icon artwork: a forest green squircle with a soft light from the
 * top-left and a crisp white calculator inside, finished with gold accents.
 *
 * The composable scales to whatever size the modifier gives it, so it can be
 * used for the splash screen, a header badge or a settings tile.
 */
@Composable
fun AppIcon(
    modifier: Modifier = Modifier,
    cornerFraction: Float = 0.27f,
    label: String? = null,
) {
    val accessibility = if (label == null) {
        Modifier
    } else {
        Modifier.semantics { contentDescription = label }
    }

    Canvas(modifier = modifier.then(accessibility)) {
        drawAppIcon(cornerFraction)
    }
}

private fun DrawScope.drawAppIcon(cornerFraction: Float) {
    // Use the full canvas size; the icon scales to any width/height.
    val canvasW = size.width
    val canvasH = size.height
    val s = size.minDimension
    if (s <= 0f) return

    // Center a square drawing area within the possibly rectangular canvas.
    val originX = (canvasW - s) / 2f
    val originY = (canvasH - s) / 2f

    val forestTop = Color(0xFF1F6B4C)
    val forestBottom = Color(0xFF0B3125)
    val forestDeep = Color(0xFF0A2A20)
    val mint = Color(0xFFDEEDE4)
    val mintSoft = Color(0xFFE5F1EA)
    val gold = Color(0xFFD9B45C)
    val goldSoft = Color(0xFFF0DCA8)
    val greenInk = Color(0xFF14553F)
    val shadow = Color(0xFF03150E)

    val corner = CornerRadius(s * cornerFraction.coerceIn(0f, 0.5f))
    val tileSize = Size(s, s)

    translate(left = originX, top = originY) {
        // Deep forest base with a gentle diagonal light fall-off.
        drawRoundRect(
            brush = Brush.linearGradient(
                colors = listOf(forestTop, forestBottom),
                start = Offset(s * 0.05f, 0f),
                end = Offset(s * 0.95f, s)
            ),
            topLeft = Offset.Zero,
            size = tileSize,
            cornerRadius = corner
        )

        // Glossy highlight in the top-left corner.
        drawRoundRect(
            brush = Brush.radialGradient(
                colors = listOf(Color.White.copy(alpha = 0.22f), Color.Transparent),
                center = Offset(s * 0.26f, s * 0.16f),
                radius = s * 0.78f
            ),
            topLeft = Offset.Zero,
            size = tileSize,
            cornerRadius = corner
        )

        // Corner shade so the tile feels rounded and crafted.
        drawRoundRect(
            brush = Brush.linearGradient(
                colors = listOf(Color.Transparent, forestDeep.copy(alpha = 0.45f)),
                start = Offset(s * 0.15f, s * 0.10f),
                end = Offset(s, s)
            ),
            topLeft = Offset.Zero,
            size = tileSize,
            cornerRadius = corner
        )

        // Inner rim highlight along the edge.
        drawRoundRect(
            color = Color.White.copy(alpha = 0.15f),
            topLeft = Offset(s * 0.010f, s * 0.010f),
            size = Size(s * 0.980f, s * 0.980f),
            cornerRadius = CornerRadius(s * cornerFraction * 0.96f),
            style = Stroke(width = s * 0.012f)
        )

        // ---------- Calculator body ----------
        val bodyW = s * 0.46f
        val bodyH = s * 0.65f
        val bodyX = (s - bodyW) / 2f
        val bodyY = (s - bodyH) / 2f
        val bodyCorner = CornerRadius(s * 0.085f)

        drawRoundRect(
            color = shadow.copy(alpha = 0.38f),
            topLeft = Offset(bodyX, bodyY + s * 0.020f),
            size = Size(bodyW, bodyH),
            cornerRadius = bodyCorner
        )
        drawRoundRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color.White, Color(0xFFF2F8F4)),
                startY = bodyY,
                endY = bodyY + bodyH
            ),
            topLeft = Offset(bodyX, bodyY),
            size = Size(bodyW, bodyH),
            cornerRadius = bodyCorner
        )

        // ---------- Display ----------
        val pad = s * 0.05f
        val displayX = bodyX + pad
        val displayY = bodyY + pad
        val displayW = bodyW - pad * 2f
        val displayH = s * 0.115f

        drawRoundRect(
            color = mint,
            topLeft = Offset(displayX, displayY),
            size = Size(displayW, displayH),
            cornerRadius = CornerRadius(s * 0.030f)
        )
        drawCircle(
            color = gold,
            radius = s * 0.013f,
            center = Offset(displayX + s * 0.032f, displayY + displayH / 2f)
        )
        drawRoundRect(
            color = greenInk,
            topLeft = Offset(displayX + displayW * 0.60f, displayY + displayH * 0.40f),
            size = Size(displayW * 0.28f, displayH * 0.20f),
            cornerRadius = CornerRadius(displayH * 0.10f)
        )

        // ---------- Keypad ----------
        val keyW = s * 0.090f
        val keyH = s * 0.075f
        val colGap = (displayW - keyW * 3f) / 2f
        val rowGap = s * 0.030f
        val gridTop = displayY + displayH + s * 0.045f
        val keyCorner = CornerRadius(keyH * 0.36f)

        for (row in 0 until 4) {
            for (col in 0 until 3) {
                val left = displayX + col * (keyW + colGap)
                val top = gridTop + row * (keyH + rowGap)
                val isOperator = col == 2
                val isEquals = row == 3 && col == 2

                if (isEquals) {
                    drawRoundRect(
                        color = greenInk,
                        topLeft = Offset(left, top),
                        size = Size(keyW, keyH),
                        cornerRadius = keyCorner
                    )
                    val barW = keyW * 0.46f
                    val barH = keyH * 0.11f
                    val barX = left + (keyW - barW) / 2f
                    drawRoundRect(
                        color = goldSoft,
                        topLeft = Offset(barX, top + keyH * 0.37f),
                        size = Size(barW, barH),
                        cornerRadius = CornerRadius(barH / 2f)
                    )
                    drawRoundRect(
                        color = goldSoft,
                        topLeft = Offset(barX, top + keyH * 0.56f),
                        size = Size(barW, barH),
                        cornerRadius = CornerRadius(barH / 2f)
                    )
                } else {
                    drawRoundRect(
                        color = if (isOperator) gold else mintSoft,
                        topLeft = Offset(left, top),
                        size = Size(keyW, keyH),
                        cornerRadius = keyCorner
                    )
                }
            }
        }
    }
}
