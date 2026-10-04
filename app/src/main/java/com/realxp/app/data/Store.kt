package com.realxp.app.data

import android.content.Context
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Areas {
    val all = listOf(
        "English", "Fitness", "Reading", "History",
        "Coding", "Math", "Writing", "Music",
        "Finance", "Science", "Art", "Meditation"
    )
}

data class LevelInfo(val level: Int, val into: Int, val need: Int)

object Levels {
    private fun walk(xp: Int, need: (Int) -> Int): LevelInfo {
        var level = 1
        var rest = xp
        while (rest >= need(level)) {
            rest -= need(level)
            level++
        }
        return LevelInfo(level, rest, need(level))
    }

    fun of(xp: Int): LevelInfo = walk(xp) { 200 + 100 * (it - 1) }

    fun overall(xp: Int): LevelInfo = walk(xp) { 400 + 200 * (it - 1) }
}

object Quests {
    val steps = listOf(15 to 20, 30 to 25, 60 to 55, 90 to 50)

    fun xpFor(minutes: Int): Int = steps.filter { minutes >= it.first }.sumOf { it.second }
}

class Store(context: Context) {
    private val prefs = context.getSharedPreferences("realxp2", Context.MODE_PRIVATE)

    private fun today(): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

    fun chosen(): List<String> {
        val s = prefs.getString("chosen", "") ?: ""
        return if (s.isEmpty()) emptyList() else s.split(",")
    }

    fun saveChosen(list: List<String>) {
        prefs.edit().putString("chosen", list.joinToString(",")).apply()
    }

    fun xp(area: String): Int = prefs.getInt("xp_$area", 0)

    fun minutesToday(area: String): Int = prefs.getInt("min_${area}_${today()}", 0)

    fun logMinutes(area: String, minutes: Int): Int {
        val before = minutesToday(area)
        val after = before + minutes
        val gain = Quests.xpFor(after) - Quests.xpFor(before)
        prefs.edit()
            .putInt("min_${area}_${today()}", after)
            .putInt("xp_$area", xp(area) + gain)
            .apply()
        return gain
    }

    fun totalXp(): Int = chosen().sumOf { xp(it) }
}