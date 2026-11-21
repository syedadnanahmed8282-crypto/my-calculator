package com.vibecraft.generated

import kotlin.math.roundToInt

/**
 * A single habit the user is building.
 *
 * [completions] maps an epoch day (see [HabitDay]) to the number of check-ins
 * recorded on that day, so a habit can be a simple once-a-day task ("Meditate")
 * or something repeated several times a day ("Drink water", target 8).
 */
data class Habit(
    val id: Long,
    val name: String,
    val emoji: String = "🌿",
    val targetPerDay: Int = 1,
    val completions: Map<Long, Int> = emptyMap()
) {
    /** Guarded daily target, always at least one check-in. */
    val target: Int
        get() = targetPerDay.coerceAtLeast(1)

    /** How many check-ins were recorded on [day]. */
    fun countOn(day: Long): Int = completions[day] ?: 0

    /** True when [day] reached its daily target. */
    fun isCompleteOn(day: Long): Boolean = countOn(day) >= target

    /** True when today's target is already met. */
    fun isCompleteToday(today: Long = HabitDay.today()): Boolean = isCompleteOn(today)

    /** Progress of a single day between 0f and 1f, handy for rings and bars. */
    fun dayFraction(day: Long): Float =
        (countOn(day).toFloat() / target).coerceIn(0f, 1f)

    /** Records one check-in on [day], never going past the daily target. */
    fun checkIn(day: Long = HabitDay.today()): Habit {
        val current = countOn(day)
        if (current >= target) return this
        return copy(completions = completions + (day to (current + 1)))
    }

    /** Removes every check-in recorded on [day]. */
    fun undo(day: Long = HabitDay.today()): Habit =
        if (completions.containsKey(day)) copy(completions = completions - day) else this

    /**
     * Single tap behaviour: adds a check-in while the day is still open and
     * clears the whole day once the target has been reached.
     */
    fun toggle(day: Long = HabitDay.today()): Habit =
        if (isCompleteOn(day)) undo(day) else checkIn(day)

    /**
     * Number of consecutive completed days ending today. When today is not
     * finished yet the streak counts up to yesterday, so an open day never
     * breaks it.
     */
    fun streak(today: Long = HabitDay.today()): Int {
        var day = if (isCompleteOn(today)) today else today - 1
        var count = 0
        while (count < HabitDay.MAX_STREAK && isCompleteOn(day)) {
            count++
            day--
        }
        return count
    }

    /** Total number of days on which the daily target was reached. */
    fun completedDays(): Int = completions.count { (_, count) -> count >= target }

    /** Total number of individual check-ins ever recorded. */
    fun totalCheckIns(): Int = completions.values.sum()

    /** Copy of this habit with a new daily target, keeping the history. */
    fun withTarget(newTarget: Int): Habit =
        copy(targetPerDay = newTarget.coerceIn(1, 20))

    companion object {
        /** Builds a fresh, normalised habit. */
        fun create(
            id: Long,
            name: String,
            emoji: String = "🌿",
            targetPerDay: Int = 1
        ): Habit = Habit(
            id = id,
            name = name.trim().ifBlank { "New habit" },
            emoji = emoji.ifBlank { "🌿" },
            targetPerDay = targetPerDay.coerceIn(1, 20)
        )
    }
}

/** How much of today's list is done, used by the progress header. */
data class DayProgress(
    val completed: Int,
    val total: Int
) {
    val fraction: Float
        get() = if (total <= 0) 0f else (completed.toFloat() / total).coerceIn(0f, 1f)

    val percent: Int
        get() = (fraction * 100f).roundToInt()

    val allDone: Boolean
        get() = total > 0 && completed >= total

    val label: String
        get() = "$completed/$total"
}

/** Snapshot of the whole list for one day. */
fun List<Habit>.progressOn(day: Long = HabitDay.today()): DayProgress =
    DayProgress(completed = count { it.isCompleteOn(day) }, total = size)

/** Highest streak across all habits, useful for a small badge. */
fun List<Habit>.bestStreak(today: Long = HabitDay.today()): Int =
    maxOfOrNull { it.streak(today) } ?: 0

/**
 * Tiny calendar helper based on epoch days so the model never needs a
 * date-formatting dependency. Day 0 is 1970-01-01 (a Thursday).
 */
object HabitDay {
    const val MILLIS_PER_DAY: Long = 86_400_000L

    /** Upper bound used while walking a streak, ~10 years of days. */
    const val MAX_STREAK: Int = 3650

    private val WEEKDAY_LETTERS = listOf("M", "T", "W", "T", "F", "S", "S")
    private val WEEKDAY_SHORT = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    private val MONTHS = listOf(
        "Jan", "Feb", "Mar", "Apr", "May", "Jun",
        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    )

    /** Epoch day for [millis] since the Unix epoch. */
    fun ofMillis(millis: Long): Long {
        var result = millis / MILLIS_PER_DAY
        if (millis < 0 && millis % MILLIS_PER_DAY != 0L) result -= 1
        return result
    }

    /** The current epoch day. */
    fun today(): Long = ofMillis(System.currentTimeMillis())

    /** The last [count] days, oldest first and ending with [today]. */
    fun recentDays(count: Int, today: Long = today()): List<Long> {
        if (count <= 0) return emptyList()
        return (count - 1 downTo 0).map { today - it }
    }

    /** 0 = Monday ... 6 = Sunday. */
    fun weekdayIndex(day: Long): Int = ((((day + 3) % 7) + 7) % 7).toInt()

    /** Single letter used by the week strip ("M", "T", ...). */
    fun weekdayLetter(day: Long): String = WEEKDAY_LETTERS[weekdayIndex(day)]

    /** Three letter weekday ("Mon"). */
    fun weekdayShort(day: Long): String = WEEKDAY_SHORT[weekdayIndex(day)]

    /** Short date such as "Jan 5". */
    fun label(day: Long): String {
        val (_, month, dayOfMonth) = civilFromEpochDay(day)
        return "${MONTHS[(month - 1).coerceIn(0, 11)]} $dayOfMonth"
    }

    /** "Today", "Yesterday" or a short date. */
    fun relativeLabel(day: Long, today: Long = today()): String = when (day) {
        today -> "Today"
        today - 1 -> "Yesterday"
        else -> label(day)
    }
}

/**
 * Converts an epoch day into (year, month, day) using Howard Hinnant's
 * civil-from-days algorithm.
 */
private fun civilFromEpochDay(epochDay: Long): Triple<Int, Int, Int> {
    val z = epochDay + 719468
    val era = (if (z >= 0) z else z - 146096) / 146097
    val dayOfEra = z - era * 146097
    val yearOfEra =
        (dayOfEra - dayOfEra / 1460 + dayOfEra / 36524 - dayOfEra / 146096) / 365
    val year = yearOfEra + era * 400
    val dayOfYear = dayOfEra - (365 * yearOfEra + yearOfEra / 4 - yearOfEra / 100)
    val mp = (5 * dayOfYear + 2) / 153
    val dayOfMonth = dayOfYear - (153 * mp + 2) / 5 + 1
    val month = if (mp < 10) mp + 3 else mp - 9
    val fullYear = if (month <= 2) year + 1 else year
    return Triple(fullYear.toInt(), month.toInt(), dayOfMonth.toInt())
}
