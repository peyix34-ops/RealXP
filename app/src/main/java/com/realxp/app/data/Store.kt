package com.realxp.app.data

import android.content.Context

object Areas {
    val all = listOf("English", "Fitness", "Reading", "History")
}

data class LevelInfo(val level: Int, val into: Int, val need: Int)

object Levels {
    fun need(level: Int): Int = 200 + 100 * (level - 1)

    fun of(xp: Int): LevelInfo {
        var level = 1
        var rest = xp
        while (rest >= need(level)) {
            rest -= need(level)
            level++
        }
        return LevelInfo(level, rest, need(level))
    }
}

class Store(context: Context) {
    private val prefs = context.getSharedPreferences("realxp", Context.MODE_PRIVATE)

    fun chosen(): List<String> {
        val s = prefs.getString("chosen", "") ?: ""
        return if (s.isEmpty()) emptyList() else s.split(",")
    }

    fun saveChosen(list: List<String>) {
        prefs.edit().putString("chosen", list.joinToString(",")).apply()
    }

    fun xp(area: String): Int = prefs.getInt("xp_$area", 0)

    fun addXp(area: String, amount: Int) {
        prefs.edit().putInt("xp_$area", xp(area) + amount).apply()
    }
}
