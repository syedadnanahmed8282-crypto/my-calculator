package com.vibecraft.generated

import android.content.Context

/**
 * One saved calculation: what was entered, what came out, and when.
 */
data class HistoryEntry(
    val expression: String,
    val result: String,
    val timestamp: Long = 0L
)

/**
 * Lightweight local storage for Hisab.
 *
 * Keeps the calculation history and the light / dark theme choice on the
 * device so that both survive closing the app. Uses SharedPreferences from
 * the Android platform, so no extra dependency is needed.
 */
class HistoryStore(context: Context) {

    private val prefs = context.applicationContext
        .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /** Reads the saved history, newest entry first. */
    fun loadHistory(): List<HistoryEntry> {
        val raw = prefs.getString(KEY_HISTORY, null) ?: return emptyList()
        return decode(raw).take(MAX_ENTRIES)
    }

    /** Writes the whole history list back to disk (newest first). */
    fun saveHistory(entries: List<HistoryEntry>) {
        val trimmed = entries.take(MAX_ENTRIES)
        prefs.edit().putString(KEY_HISTORY, encode(trimmed)).apply()
    }

    /** Adds a new entry on top and keeps at most [MAX_ENTRIES] items. */
    fun addEntry(entry: HistoryEntry): List<HistoryEntry> {
        val updated = (listOf(entry) + loadHistory()).take(MAX_ENTRIES)
        saveHistory(updated)
        return updated
    }

    /** Removes every saved calculation. */
    fun clearHistory() {
        prefs.edit().remove(KEY_HISTORY).apply()
    }

    /** True when the user picked the dark theme. */
    fun loadDarkMode(): Boolean = prefs.getBoolean(KEY_DARK_MODE, false)

    /** Stores the current theme choice. */
    fun saveDarkMode(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_DARK_MODE, enabled).apply()
    }

    /** Reads the value kept in calculator memory (0.0 when it is empty). */
    fun loadMemory(): Double =
        prefs.getString(KEY_MEMORY, null)?.toDoubleOrNull() ?: 0.0

    /** Stores the value kept in calculator memory. */
    fun saveMemory(value: Double) {
        prefs.edit().putString(KEY_MEMORY, value.toString()).apply()
    }

    /** Clears the calculator memory. */
    fun clearMemory() {
        prefs.edit().remove(KEY_MEMORY).apply()
    }

    // --- simple, separator based encoding ---------------------------------

    private fun encode(entries: List<HistoryEntry>): String =
        entries.joinToString(ENTRY_SEP) { entry ->
            listOf(
                sanitize(entry.expression),
                sanitize(entry.result),
                entry.timestamp.toString()
            ).joinToString(FIELD_SEP)
        }

    private fun decode(raw: String): List<HistoryEntry> {
        if (raw.isEmpty()) return emptyList()
        return raw.split(ENTRY_SEP).mapNotNull { chunk ->
            if (chunk.isEmpty()) return@mapNotNull null
            val parts = chunk.split(FIELD_SEP)
            if (parts.size < 2) return@mapNotNull null
            val expression = parts[0]
            val result = parts[1]
            if (expression.isEmpty() && result.isEmpty()) return@mapNotNull null
            HistoryEntry(
                expression = expression,
                result = result,
                timestamp = parts.getOrNull(2)?.toLongOrNull() ?: 0L
            )
        }
    }

    private fun sanitize(text: String): String =
        text.replace(ENTRY_SEP, "")
            .replace(FIELD_SEP, "")
            .replace("\n", " ")
            .replace("\r", " ")

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
