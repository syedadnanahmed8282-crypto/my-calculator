package com.vibecraft.generated

import android.content.Context

/**
 * One saved calculation: what was entered, what came out, and when.
 */
data class HistoryEntry(
    val expression: String = "",
    val result: String = "",
    val timestamp: Long = 0L
)

/**
 * Lightweight local storage for Hisab.
 *
 * Keeps the calculation history, the calculator memory and the light / dark
 * theme choice on the device so that all of them survive closing the app.
 * Only Android platform APIs are used, so no extra dependency is needed.
 */
class HistoryStore(context: Context) {

    private val prefs = context.applicationContext
        .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /* ---------------------------- history ---------------------------- */

    /** Reads the saved history, newest entry first. */
    fun loadHistory(): List<HistoryEntry> {
        val raw = prefs.getString(KEY_HISTORY, null)
        if (raw.isNullOrEmpty()) return emptyList()
        val decoded = decode(raw)
        return if (decoded.size > MAX_ENTRIES) {
            ArrayList(decoded.subList(0, MAX_ENTRIES))
        } else {
            decoded
        }
    }

    /** Writes the whole history list back to disk (newest first). */
    fun saveHistory(entries: List<HistoryEntry>) {
        val trimmed = if (entries.size > MAX_ENTRIES) {
            ArrayList(entries.subList(0, MAX_ENTRIES))
        } else {
            entries
        }
        prefs.edit().putString(KEY_HISTORY, encode(trimmed)).apply()
    }

    /** Adds a new entry on top and keeps at most [MAX_ENTRIES] items. */
    fun addEntry(entry: HistoryEntry): List<HistoryEntry> {
        val stamped = if (entry.timestamp > 0L) {
            entry
        } else {
            entry.copy(timestamp = System.currentTimeMillis())
        }
        val current = loadHistory()
        val updated = ArrayList<HistoryEntry>(current.size + 1)
        updated.add(stamped)
        updated.addAll(current)
        val trimmed = if (updated.size > MAX_ENTRIES) {
            ArrayList(updated.subList(0, MAX_ENTRIES))
        } else {
            updated
        }
        saveHistory(trimmed)
        return trimmed
    }

    /** Removes every saved calculation. */
    fun clearHistory() {
        prefs.edit().remove(KEY_HISTORY).apply()
    }

    /** True when at least one calculation has been stored. */
    fun hasHistory(): Boolean = !prefs.getString(KEY_HISTORY, null).isNullOrEmpty()

    /* ----------------------------- theme ----------------------------- */

    /** True when the user picked the dark theme. */
    fun loadDarkMode(): Boolean = prefs.getBoolean(KEY_DARK_MODE, false)

    /** Stores the current theme choice. */
    fun saveDarkMode(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_DARK_MODE, enabled).apply()
    }

    /* ---------------------------- memory ----------------------------- */

    /** Reads the value kept in calculator memory (0.0 when it is empty). */
    fun loadMemory(): Double {
        val raw = prefs.getString(KEY_MEMORY, null) ?: return 0.0
        val value = raw.toDoubleOrNull() ?: return 0.0
        return if (value.isNaN() || value.isInfinite()) 0.0 else value
    }

    /** True when a value is currently held in memory. */
    fun hasMemory(): Boolean = !prefs.getString(KEY_MEMORY, null).isNullOrEmpty()

    /** Stores the value kept in calculator memory. */
    fun saveMemory(value: Double) {
        if (value.isNaN() || value.isInfinite()) {
            clearMemory()
        } else {
            prefs.edit().putString(KEY_MEMORY, value.toString()).apply()
        }
    }

    /** Clears the calculator memory. */
    fun clearMemory() {
        prefs.edit().remove(KEY_MEMORY).apply()
    }

    /* --------------- simple, separator based encoding ---------------- */

    private fun encode(entries: List<HistoryEntry>): String {
        val builder = StringBuilder()
        for (index in entries.indices) {
            if (index > 0) builder.append(ENTRY_SEP)
            val entry = entries[index]
            builder.append(sanitize(entry.expression))
            builder.append(FIELD_SEP)
            builder.append(sanitize(entry.result))
            builder.append(FIELD_SEP)
            builder.append(entry.timestamp.toString())
        }
        return builder.toString()
    }

    private fun decode(raw: String): List<HistoryEntry> {
        if (raw.isEmpty()) return emptyList()
        val chunks = raw.split(ENTRY_SEP)
        val result = ArrayList<HistoryEntry>(chunks.size)
        for (chunk in chunks) {
            if (chunk.isEmpty()) continue
            val parts = chunk.split(FIELD_SEP)
            if (parts.size < 2) continue
            val expression = parts[0]
            val value = parts[1]
            if (expression.isEmpty() && value.isEmpty()) continue
            val stamp = if (parts.size > 2) parts[2].toLongOrNull() ?: 0L else 0L
            result.add(
                HistoryEntry(
                    expression = expression,
                    result = value,
                    timestamp = stamp
                )
            )
        }
        return result
    }

    private fun sanitize(text: String): String {
        if (text.isEmpty()) return ""
        val builder = StringBuilder(text.length)
        for (character in text) {
            when (character) {
                '\u001F', '\u001E' -> Unit
                '\n', '\r', '\t' -> builder.append(' ')
                else -> builder.append(character)
            }
        }
        return builder.toString()
    }

    companion object {
        private const val PREFS_NAME = "hisab_prefs"
        private const val KEY_HISTORY = "history"
        private const val KEY_DARK_MODE = "dark_mode"
        private const val KEY_MEMORY = "memory"
        private const val MAX_ENTRIES = 100

        private const val ENTRY_SEP = "\u001F"
        private const val FIELD_SEP = "\u001E"
    }
}
