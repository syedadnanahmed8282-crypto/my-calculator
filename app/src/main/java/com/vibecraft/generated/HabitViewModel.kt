package com.vibecraft.generated

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Everything the habit screen renders: the day being tracked plus the list.
 */
data class HabitUiState(
    val day: Long = HabitDay.today(),
    val habits: List<Habit> = emptyList()
) {
    /** How many habits are already done today. */
    val progress: DayProgress
        get() = habits.progressOn(day)

    /** Longest active streak across all habits. */
    val bestStreak: Int
        get() = habits.maxOfOrNull { it.streak(day) } ?: 0

    /** Lifetime check-ins, shown as a soft summary line. */
    val totalCheckIns: Int
        get() = habits.sumOf { it.totalCheckIns() }

    val isEmpty: Boolean
        get() = habits.isEmpty()

    /** Habits already completed today, useful for the "done" section. */
    val doneToday: Int
        get() = habits.count { it.isCompleteOn(day) }

    fun habit(id: Long): Habit? = habits.firstOrNull { it.id == id }
}

/**
 * Holds the in-memory habit list. Everything is synchronous state, so the
 * screen only needs to collect [state] and call the tap handlers.
 */
class HabitViewModel : ViewModel() {

    private val _state = MutableStateFlow(
        HabitUiState(
            day = HabitDay.today(),
            habits = initialHabits(HabitDay.today())
        )
    )

    val state: StateFlow<HabitUiState> = _state.asStateFlow()

    /** Tap on a habit row: adds a check-in, or clears the day when already full. */
    fun toggle(habitId: Long) {
        updateHabit(habitId) { habit, day -> habit.toggle(day) }
    }

    /** Explicit single check-in, used by the "+" affordance on multi-count habits. */
    fun checkIn(habitId: Long) {
        updateHabit(habitId) { habit, day -> habit.checkIn(day) }
    }

    /** Clears every check-in for today on one habit. */
    fun resetToday(habitId: Long) {
        updateHabit(habitId) { habit, day -> habit.undo(day) }
    }

    /** Adds a brand new habit to the bottom of the list. */
    fun addHabit(
        name: String,
        emoji: String = "🌿",
        targetPerDay: Int = 1
    ) {
        val trimmed = name.trim()
        if (trimmed.isBlank()) return
        _state.update { current ->
            val id = (current.habits.maxOfOrNull { it.id } ?: 0L) + 1L
            current.copy(
                habits = current.habits + Habit.create(
                    id = id,
                    name = trimmed,
                    emoji = emoji,
                    targetPerDay = targetPerDay
                )
            )
        }
    }

    /** Removes a habit and all of its history. */
    fun deleteHabit(habitId: Long) {
        _state.update { current ->
            current.copy(habits = current.habits.filterNot { it.id == habitId })
        }
    }

    /** Changes the daily target of an existing habit, keeping the history. */
    fun setTarget(habitId: Long, target: Int) {
        updateHabit(habitId) { habit, _ -> habit.withTarget(target) }
    }

    /** Rolls the screen over to the current day. */
    fun refreshDay() {
        _state.update { it.copy(day = HabitDay.today()) }
    }

    private inline fun updateHabit(
        habitId: Long,
        transform: (Habit, Long) -> Habit
    ) {
        _state.update { current ->
            current.copy(
                habits = current.habits.map { habit ->
                    if (habit.id == habitId) transform(habit, current.day) else habit
                }
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Seeded starter list so the first launch already looks alive.
// ---------------------------------------------------------------------------

/**
 * Copies [habit] and fills the previous [streakDays] days completely, plus
 * [todayCount] check-ins for today.
 */
private fun seed(
    habit: Habit,
    today: Long,
    streakDays: Int,
    todayCount: Int = 0
): Habit {
    val history = HashMap<Long, Int>()
    if (streakDays > 0) {
        for (offset in 1..streakDays) {
            history[today - offset] = habit.target
        }
    }
    if (todayCount > 0) {
        history[today] = todayCount.coerceAtMost(habit.target)
    }
    return habit.copy(completions = history)
}

private fun initialHabits(today: Long): List<Habit> = listOf(
    seed(
        habit = Habit.create(id = 1L, name = "Drink water", emoji = "💧", targetPerDay = 8),
        today = today,
        streakDays = 6,
        todayCount = 4
    ),
    seed(
        habit = Habit.create(id = 2L, name = "Meditate", emoji = "🧘", targetPerDay = 1),
        today = today,
        streakDays = 12
    ),
    seed(
        habit = Habit.create(id = 3L, name = "Read 10 pages", emoji = "📖", targetPerDay = 1),
        today = today,
        streakDays = 4,
        todayCount = 1
    ),
    seed(
        habit = Habit.create(id = 4L, name = "Walk outside", emoji = "🚶", targetPerDay = 1),
        today = today,
        streakDays = 3
    ),
    seed(
        habit = Habit.create(id = 5L, name = "Tidy desk", emoji = "🧹", targetPerDay = 1),
        today = today,
        streakDays = 1
    ),
    seed(
        habit = Habit.create(id = 6L, name = "Stretch", emoji = "🤸", targetPerDay = 2),
        today = today,
        streakDays = 0
    )
)
