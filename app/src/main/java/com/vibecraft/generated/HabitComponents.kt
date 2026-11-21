package com.vibecraft.generated

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ---------------------------------------------------------------------------
// Date helpers – kept local so the components stay dependency free.
// ---------------------------------------------------------------------------

private val WEEKDAYS = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
private val MONTHS = listOf(
    "Jan", "Feb", "Mar", "Apr", "May", "Jun",
    "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
)

/** A friendly line like "Thu, 12 Jun" for the given epoch day. */
fun dayTitle(epochDay: Long): String {
    val weekday = WEEKDAYS[Math.floorMod(epochDay + 3L, 7L).toInt()]
    val date = civilDate(epochDay)
    return "$weekday, ${date.third} ${MONTHS[date.second - 1]}"
}

/** Short weekday, e.g. "Thu". */
fun dayWeekday(epochDay: Long): String =
    WEEKDAYS[Math.floorMod(epochDay + 3L, 7L).toInt()]

private fun civilDate(epochDay: Long): Triple<Int, Int, Int> {
    val z = epochDay + 719468L
    val era = (if (z >= 0) z else z - 146096L) / 146097L
    val doe = z - era * 146097L
    val yoe = (doe - doe / 1460L + doe / 36524L - doe / 146096L) / 365L
    val y = yoe + era * 400L
    val doy = doe - (365L * yoe + yoe / 4L - yoe / 100L)
    val mp = (5L * doy + 2L) / 153L
    val d = doy - (153L * mp + 2L) / 5L + 1L
    val m = if (mp < 10L) mp + 3L else mp - 9L
    val year = if (m <= 2L) y + 1L else y
    return Triple(year.toInt(), m.toInt(), d.toInt())
}

// ---------------------------------------------------------------------------
// Progress header
// ---------------------------------------------------------------------------

/**
 * Dark forest-green card at the top of the screen: a progress ring, a short
 * summary line and the best active streak.
 */
@Composable
fun ProgressHeader(
    progress: DayProgress,
    bestStreak: Int,
    day: Long,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = HabitPalette.Forest
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProgressRing(
                fraction = progress.fraction,
                label = "${progress.percent}%"
            )
            Spacer(Modifier.width(18.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = dayTitle(day),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White.copy(alpha = 0.72f)
                )
                Spacer(Modifier.height(3.dp))
                Text(
                    text = headlineFor(progress),
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = footerFor(progress),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.72f)
                )
                Spacer(Modifier.height(10.dp))
                StreakChip(streak = bestStreak, onDark = true)
            }
        }
    }
}

private fun headlineFor(progress: DayProgress): String = when {
    progress.total == 0 -> "A fresh start"
    progress.allDone -> "All done today"
    else -> "${progress.completed} of ${progress.total} done"
}

private fun footerFor(progress: DayProgress): String = when {
    progress.total == 0 -> "Add your first habit below"
    progress.allDone -> "Every habit checked off"
    else -> "${progress.total - progress.completed} still to go"
}

@Composable
private fun ProgressRing(fraction: Float, label: String) {
    Box(
        modifier = Modifier.size(76.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 8.dp.toPx()
            val inset = strokeWidth / 2f
            val arcSize = Size(size.width - strokeWidth, size.height - strokeWidth)

            drawArc(
                color = Color.White.copy(alpha = 0.22f),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            val sweep = 360f * fraction.coerceIn(0f, 1f)
            if (sweep > 0f) {
                drawArc(
                    color = HabitPalette.SoftGreen,
                    startAngle = -90f,
                    sweepAngle = sweep,
                    useCenter = false,
                    topLeft = Offset(inset, inset),
                    size = arcSize,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }
        }
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

// ---------------------------------------------------------------------------
// Streak chip
// ---------------------------------------------------------------------------

/** Small pill showing the current streak length. */
@Composable
fun StreakChip(
    streak: Int,
    modifier: Modifier = Modifier,
    onDark: Boolean = false
) {
    val background = if (onDark) Color.White.copy(alpha = 0.16f) else HabitPalette.Mist
    val content = if (onDark) Color.White else HabitPalette.Forest

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(background)
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "🔥", fontSize = 11.sp)
        Spacer(Modifier.width(5.dp))
        Text(
            text = when {
                streak <= 0 -> "No streak yet"
                streak == 1 -> "1 day"
                else -> "$streak days"
            },
            style = MaterialTheme.typography.labelMedium,
            color = content
        )
    }
}

// ---------------------------------------------------------------------------
// Habit row
// ---------------------------------------------------------------------------

/**
 * One habit in the list. Tapping the row toggles today's check-in, the
 * trailing menu offers extra actions for multi-count habits.
 */
@Composable
fun HabitRow(
    habit: Habit,
    day: Long,
    onToggle: () -> Unit,
    onCheckIn: () -> Unit,
    onReset: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val done = habit.isCompleteOn(day)
    val streak = habit.streak(day)
    var menuOpen by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = if (done) HabitPalette.Mist else HabitPalette.White,
        border = BorderStroke(
            width = 1.dp,
            color = if (done) HabitPalette.SoftGreen else HabitPalette.Line
        ),
        onClick = onToggle
    ) {
        Row(
            modifier = Modifier.padding(start = 14.dp, end = 6.dp, top = 12.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (done) Color.White else HabitPalette.Surface),
                contentAlignment = Alignment.Center
            ) {
                Text(text = habit.emoji, fontSize = 20.sp)
            }

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = habit.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = HabitPalette.Ink,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = subtitleFor(habit, day, done),
                        style = MaterialTheme.typography.bodySmall,
                        color = HabitPalette.Muted,
                        maxLines = 1
                    )
                    if (streak > 0) {
                        Spacer(Modifier.width(8.dp))
                        StreakChip(streak = streak)
                    }
                }
                if (habit.target > 1) {
                    Spacer(Modifier.height(9.dp))
                    val fraction = habit.dayFraction(day)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp)
                            .clip(CircleShape)
                            .background(HabitPalette.Line)
                    ) {
                        if (fraction > 0f) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(fraction)
                                    .height(5.dp)
                                    .clip(CircleShape)
                                    .background(HabitPalette.Leaf)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.width(8.dp))

            CheckCircle(done = done, onClick = onToggle)

            Box {
                IconButton(
                    onClick = { menuOpen = true },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.MoreVert,
                        contentDescription = "Habit options",
                        tint = HabitPalette.Muted
                    )
                }
                DropdownMenu(
                    expanded = menuOpen,
                    onDismissRequest = { menuOpen = false }
                ) {
                    if (habit.target > 1 && !done) {
                        DropdownMenuItem(
                            text = { Text("Add a check-in") },
                            onClick = {
                                menuOpen = false
                                onCheckIn()
                            }
                        )
                    }
                    DropdownMenuItem(
                        text = { Text("Reset today") },
                        onClick = {
                            menuOpen = false
                            onReset()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Delete habit") },
                        onClick = {
                            menuOpen = false
                            onDelete()
                        }
                    )
                }
            }
        }
    }
}

private fun subtitleFor(habit: Habit, day: Long, done: Boolean): String = when {
    habit.target > 1 -> "${habit.countOn(day)}/${habit.target} today"
    done -> "Done for today"
    else -> "Tap to check off"
}

@Composable
private fun CheckCircle(done: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(if (done) HabitPalette.Forest else Color.Transparent)
            .border(
                width = 2.dp,
                color = if (done) HabitPalette.Forest else HabitPalette.Line,
                shape = CircleShape
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (done) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = "Completed",
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Empty state
// ---------------------------------------------------------------------------

@Composable
fun EmptyHabits(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 44.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(HabitPalette.Mist),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "🌱", fontSize = 30.sp)
        }
        Spacer(Modifier.height(14.dp))
        Text(
            text = "No habits yet",
            style = MaterialTheme.typography.titleMedium,
            color = HabitPalette.Ink
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = "Tap the + button to plant your first habit.",
            style = MaterialTheme.typography.bodyMedium,
            color = HabitPalette.Muted,
            textAlign = TextAlign.Center
        )
    }
}

// ---------------------------------------------------------------------------
// Add habit dialog
// ---------------------------------------------------------------------------

private val HABIT_EMOJIS = listOf("🌿", "💧", "🏃", "📚", "🧘", "🥗", "💤", "🎸", "✍️", "☀️")

@Composable
fun AddHabitDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, emoji: String, target: Int) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var emoji by remember { mutableStateOf(HABIT_EMOJIS.first()) }
    var target by remember { mutableStateOf(1) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(28.dp),
        containerColor = HabitPalette.White,
        titleContentColor = HabitPalette.Ink,
        textContentColor = HabitPalette.Muted,
        title = {
            Text(text = "New habit", style = MaterialTheme.typography.titleLarge)
        },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { if (it.length <= 40) name = it },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = "e.g. Drink water",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                )

                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Pick an icon",
                    style = MaterialTheme.typography.labelMedium,
                    color = HabitPalette.Muted
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HABIT_EMOJIS.forEach { option ->
                        val selected = option == emoji
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(
                                    if (selected) HabitPalette.Mist else HabitPalette.Surface
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (selected) HabitPalette.Forest else HabitPalette.Line,
                                    shape = CircleShape
                                )
                                .clickable { emoji = option },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = option, fontSize = 18.sp)
                        }
                    }
                }

                Spacer(Modifier.height(18.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Times per day",
                            style = MaterialTheme.typography.titleSmall,
                            color = HabitPalette.Ink
                        )
                        Text(
                            text = if (target == 1) "Once a day" else "$target times a day",
                            style = MaterialTheme.typography.bodySmall,
                            color = HabitPalette.Muted
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        StepButton(
                            label = "−",
                            enabled = target > 1,
                            onClick = { target -= 1 }
                        )
                        Text(
                            text = "$target",
                            modifier = Modifier.width(34.dp),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.titleMedium,
                            color = HabitPalette.Ink
                        )
                        StepButton(
                            label = "+",
                            enabled = target < 10,
                            onClick = { target += 1 }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(name, emoji, target) },
                enabled = name.isNotBlank()
            ) {
                Text(text = "Add habit", style = MaterialTheme.typography.labelLarge)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel", style = MaterialTheme.typography.labelLarge)
            }
        }
    )
}

@Composable
private fun StepButton(label: String, enabled: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(if (enabled) HabitPalette.Mist else HabitPalette.Surface)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            color = if (enabled) HabitPalette.Forest else HabitPalette.Muted.copy(alpha = 0.45f)
        )
    }
}
