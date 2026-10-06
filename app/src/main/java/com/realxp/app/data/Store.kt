package com.realxp.app.data

import android.content.Context
import java.text.SimpleDateFormat
import java.util.Calendar
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

    fun of(xp: Int): LevelInfo = walk(xp) { 200 + 60 * (it - 1) }

    fun overall(xp: Int): LevelInfo = walk(xp) { 400 + 120 * (it - 1) }
}

object Ranks {
    fun title(level: Int): String = when {
        level < 3 -> "Novice"
        level < 6 -> "Apprentice"
        level < 10 -> "Adept"
        level < 20 -> "Veteran"
        level < 30 -> "Elite"
        level < 40 -> "Master"
        level < 50 -> "Grandmaster"
        else -> "Legend"
    }
}

object Xp {
    const val DAILY_CAP = 180

    private val pts = listOf(
        0.0 to 0.0, 15.0 to 20.0, 30.0 to 45.0,
        60.0 to 100.0, 90.0 to 150.0, 150.0 to 180.0
    )

    fun forMinutes(m: Double): Int {
        if (m <= 0.0) return 0
        val last = pts.last()
        if (m >= last.first) return last.second.toInt()
        for (i in 1 until pts.size) {
            val (x0, y0) = pts[i - 1]
            val (x1, y1) = pts[i]
            if (m <= x1) return (y0 + (y1 - y0) * (m - x0) / (x1 - x0)).toInt()
        }
        return last.second.toInt()
    }
}

data class LogResult(val minutes: Int, val gain: Int)
data class DayStat(val label: String, val minutes: Int, val isToday: Boolean)

fun formatTime(sec: Int): String {
    val h = sec / 3600
    val m = (sec % 3600) / 60
    return if (h > 0) "${h}h ${m}m" else "${m}m"
}

class Store(context: Context) {
    private val prefs = context.getSharedPreferences("realxp2", Context.MODE_PRIVATE)

    private fun dayKey(back: Int = 0): String {
        val c = Calendar.getInstance()
        c.add(Calendar.DAY_OF_YEAR, -back)
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(c.time)
    }

    fun chosen(): List<String> {
        val s = prefs.getString("chosen", "") ?: ""
        return if (s.isEmpty()) emptyList() else s.split(",")
    }

    fun saveChosen(list: List<String>) {
        prefs.edit().putString("chosen", list.joinToString(",")).apply()
    }

    fun xp(area: String): Int = prefs.getInt("xp_$area", 0)
    fun secondsToday(area: String): Int = prefs.getInt("sec_${area}_${dayKey()}", 0)
    fun totalSeconds(area: String): Int = prefs.getInt("tot_$area", 0)
    fun sessionCount(area: String): Int = prefs.getInt("cnt_$area", 0)
    fun allSeconds(): Int = Areas.all.sumOf { totalSeconds(it) }
    fun totalXp(): Int = Areas.all.sumOf { xp(it) }

    fun week(area: String): List<DayStat> {
        val out = mutableListOf<DayStat>()
        for (back in 6 downTo 0) {
            val c = Calendar.getInstance()
            c.add(Calendar.DAY_OF_YEAR, -back)
            val key = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(c.time)
            val label = SimpleDateFormat("EEE", Locale.US).format(c.time).take(1)
            out.add(DayStat(label, prefs.getInt("sec_${area}_$key", 0) / 60, back == 0))
        }
        return out
    }

    fun logMinutes(area: String, minutes: Int): LogResult {
        val sec = minutes * 60
        val key = dayKey()
        val before = prefs.getInt("sec_${area}_$key", 0)
        val after = before + sec
        val gain = Xp.forMinutes(after / 60.0) - Xp.forMinutes(before / 60.0)
        prefs.edit()
            .putInt("sec_${area}_$key", after)
            .putInt("tot_$area", totalSeconds(area) + sec)
            .putInt("cnt_$area", sessionCount(area) + 1)
            .putInt("xp_$area", xp(area) + gain)
            .putString("last_area", area)
            .putString("last_key", key)
            .putInt("last_sec", sec)
            .putInt("last_gain", gain)
            .apply()
        return LogResult(minutes, gain)
    }

    fun undoLast(): Boolean {
        val area = prefs.getString("last_area", null) ?: return false
        val key = prefs.getString("last_key", "") ?: ""
        val sec = prefs.getInt("last_sec", 0)
        val gain = prefs.getInt("last_gain", 0)
        val daySec = prefs.getInt("sec_${area}_$key", 0)
        prefs.edit()
            .putInt("sec_${area}_$key", (daySec - sec).coerceAtLeast(0))
            .putInt("tot_$area", (totalSeconds(area) - sec).coerceAtLeast(0))
            .putInt("cnt_$area", (sessionCount(area) - 1).coerceAtLeast(0))
            .putInt("xp_$area", (xp(area) - gain).coerceAtLeast(0))
            .remove("last_area")
            .remove("last_key")
            .remove("last_sec")
            .remove("last_gain")
            .apply()
        return true
    }
}
