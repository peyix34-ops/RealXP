package com.realxp.app.data

import android.content.Context
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.random.Random

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
    const val DAY_MINUTES = 1440
    const val DAY_SECONDS = DAY_MINUTES * 60

    private val pts = listOf(
        0.0 to 0.0, 15.0 to 20.0, 30.0 to 45.0,
        60.0 to 100.0, 90.0 to 150.0, 150.0 to 180.0
    )

    fun forMinutes(m: Double): Int {
        if (m <= 0.0) return 0
        val last = pts.last()
        if (m >= last.first) return (last.second + (m - last.first) * 0.5).toInt()
        for (i in 1 until pts.size) {
            val (x0, y0) = pts[i - 1]
            val (x1, y1) = pts[i]
            if (m <= x1) return (y0 + (y1 - y0) * (m - x0) / (x1 - x0)).toInt()
        }
        return last.second.toInt()
    }
}

data class LogResult(val minutes: Int, val gain: Int, val ok: Boolean, val leftMin: Int)
data class DayStat(val label: String, val minutes: Int, val isToday: Boolean)
data class LogEntry(val ts: Long, val area: String, val minutes: Int, val gain: Int)
data class ArenaShare(val area: String, val minutes: Int, val xp: Int)

data class MonthReport(
    val year: Int,
    val month: Int,
    val days: Int,
    val firstWeekday: Int,
    val dayMinutes: List<Int>,
    val arenas: List<ArenaShare>,
    val totalMinutes: Int,
    val totalXp: Int,
    val activeDays: Int,
    val bestDay: Int,
    val bestMinutes: Int,
    val longestStreak: Int,
    val prevMinutes: Int
)

fun formatTime(sec: Int): String {
    val h = sec / 3600
    val m = (sec % 3600) / 60
    return if (h > 0) "${h}h ${m}m" else "${m}m"
}

fun monthKey(year: Int, month: Int): String = "%04d-%02d".format(year, month + 1)

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
    fun secondsToday(area: String): Int = prefs.getInt("s2_${area}_${dayKey()}", 0)
    fun secondsOn(area: String, back: Int): Int = prefs.getInt("s2_${area}_${dayKey(back)}", 0)
    fun totalSeconds(area: String): Int = prefs.getInt("tot_$area", 0)
    fun sessionCount(area: String): Int = prefs.getInt("cnt_$area", 0)
    fun allSeconds(): Int = chosen().sumOf { totalSeconds(it) }
    fun totalXp(): Int = chosen().sumOf { xp(it) }
    fun totalLogs(): Int = chosen().sumOf { sessionCount(it) }

    private fun daySecondsOf(back: Int, areas: List<String>): Int {
        val key = dayKey(back)
        return areas.sumOf { prefs.getInt("s2_${it}_$key", 0) }
    }

    fun streak(): Int {
        val ar = chosen()
        var back = if (daySecondsOf(0, ar) > 0) 0 else 1
        var n = 0
        while (back < 800 && daySecondsOf(back, ar) > 0) {
            n++
            back++
        }
        return n
    }

    fun bestStreak(): Int {
        val ar = chosen()
        var best = 0
        var run = 0
        for (back in 364 downTo 0) {
            if (daySecondsOf(back, ar) > 0) {
                run++
                if (run > best) best = run
            } else {
                run = 0
            }
        }
        return best
    }

    fun heat(weeks: Int, area: String?): List<List<Int?>> {
        val ar = chosen()
        val c = Calendar.getInstance()
        val todayIdx = (c.get(Calendar.DAY_OF_WEEK) + 5) % 7
        val cols = mutableListOf<List<Int?>>()
        for (w in 0 until weeks) {
            val col = mutableListOf<Int?>()
            for (r in 0..6) {
                val back = (weeks - 1 - w) * 7 + (todayIdx - r)
                col.add(
                    if (back < 0) null
                    else (if (area == null) daySecondsOf(back, ar) else secondsOn(area, back)) / 60
                )
            }
            cols.add(col)
        }
        return cols
    }

    fun week(area: String): List<DayStat> {
        val out = mutableListOf<DayStat>()
        for (back in 6 downTo 0) {
            val c = Calendar.getInstance()
            c.add(Calendar.DAY_OF_YEAR, -back)
            val key = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(c.time)
            val label = SimpleDateFormat("EEE", Locale.US).format(c.time).take(1)
            out.add(DayStat(label, prefs.getInt("s2_${area}_$key", 0) / 60, back == 0))
        }
        return out
    }

    private fun readHist(): MutableList<LogEntry> {
        val raw = prefs.getString("hist", "") ?: ""
        val out = mutableListOf<LogEntry>()
        if (raw.isEmpty()) return out
        for (line in raw.split("\n")) {
            val p = line.split("|")
            if (p.size == 4) {
                val ts = p[0].toLongOrNull()
                val min = p[2].toIntOrNull()
                val gain = p[3].toIntOrNull()
                if (ts != null && min != null && gain != null) {
                    out.add(LogEntry(ts, p[1], min, gain))
                }
            }
        }
        return out
    }

    private fun writeHist(list: List<LogEntry>) {
        val trimmed = if (list.size > 600) list.takeLast(600) else list
        val text = trimmed.joinToString("\n") { "${it.ts}|${it.area}|${it.minutes}|${it.gain}" }
        prefs.edit().putString("hist", text).apply()
    }

    fun history(area: String, limit: Int = 100): List<LogEntry> =
        readHist().filter { it.area == area }.takeLast(limit).reversed()

    fun logMinutes(area: String, minutes: Int): LogResult {
        val sec = minutes * 60
        val key = dayKey()
        val before = prefs.getInt("s2_${area}_$key", 0)
        if (before + sec > Xp.DAY_SECONDS) {
            val left = ((Xp.DAY_SECONDS - before) / 60).coerceAtLeast(0)
            return LogResult(minutes, 0, false, left)
        }
        val after = before + sec
        val gain = Xp.forMinutes(after / 60.0) - Xp.forMinutes(before / 60.0)
        prefs.edit()
            .putInt("s2_${area}_$key", after)
            .putInt("tot_$area", totalSeconds(area) + sec)
            .putInt("cnt_$area", sessionCount(area) + 1)
            .putInt("xp_$area", xp(area) + gain)
            .putString("last_area", area)
            .putString("last_key", key)
            .putInt("last_sec", sec)
            .putInt("last_gain", gain)
            .apply()
        val h = readHist()
        h.add(LogEntry(System.currentTimeMillis(), area, minutes, gain))
        writeHist(h)
        return LogResult(minutes, gain, true, 0)
    }

    fun undoLast(): Boolean {
        val area = prefs.getString("last_area", null) ?: return false
        val key = prefs.getString("last_key", "") ?: ""
        val sec = prefs.getInt("last_sec", 0)
        val gain = prefs.getInt("last_gain", 0)
        val daySec = prefs.getInt("s2_${area}_$key", 0)
        prefs.edit()
            .putInt("s2_${area}_$key", (daySec - sec).coerceAtLeast(0))
            .putInt("tot_$area", (totalSeconds(area) - sec).coerceAtLeast(0))
            .putInt("cnt_$area", (sessionCount(area) - 1).coerceAtLeast(0))
            .putInt("xp_$area", (xp(area) - gain).coerceAtLeast(0))
            .remove("last_area")
            .remove("last_key")
            .remove("last_sec")
            .remove("last_gain")
            .apply()
        val h = readHist()
        if (h.isNotEmpty()) {
            h.removeAt(h.size - 1)
            writeHist(h)
        }
        return true
    }

    fun eraseArea(area: String) {
        val e = prefs.edit()
        for (k in prefs.all.keys) {
            if (k == "xp_$area" || k == "tot_$area" || k == "cnt_$area" || k.startsWith("s2_${area}_")) {
                e.remove(k)
            }
        }
        if (prefs.getString("last_area", null) == area) {
            e.remove("last_area").remove("last_key").remove("last_sec").remove("last_gain")
        }
        e.apply()
        writeHist(readHist().filter { it.area != area })
    }

    fun honestyDue(): Boolean {
        val last = prefs.getLong("honest_last", 0L)
        val now = System.currentTimeMillis()
        if (now - last < 8 * 3600 * 1000L) return false
        return Random.nextInt(10) == 0
    }

    fun markHonesty() {
        prefs.edit().putLong("honest_last", System.currentTimeMillis()).apply()
    }

    fun reportSeen(): String = prefs.getString("report_seen", "") ?: ""

    fun markReportSeen(key: String) {
        prefs.edit().putString("report_seen", key).apply()
    }

    private fun monthDayMinutes(year: Int, month: Int, areas: List<String>): List<Int> {
        val c = Calendar.getInstance()
        c.clear()
        c.set(year, month, 1)
        val days = c.getActualMaximum(Calendar.DAY_OF_MONTH)
        val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val out = MutableList(days) { 0 }
        for (d in 1..days) {
            c.set(year, month, d)
            val key = fmt.format(c.time)
            var s = 0
            for (a in areas) s += prefs.getInt("s2_${a}_$key", 0)
            out[d - 1] = s / 60
        }
        return out
    }

    fun pendingReport(): Pair<Int, Int>? {
        val now = Calendar.getInstance()
        var y = now.get(Calendar.YEAR)
        var m = now.get(Calendar.MONTH) - 1
        if (m < 0) {
            m = 11
            y--
        }
        if (reportSeen() == monthKey(y, m)) return null
        val total = monthDayMinutes(y, m, chosen()).sum()
        return if (total > 0) Pair(y, m) else null
    }

    fun reportMonths(): List<Pair<Int, Int>> {
        val ar = chosen().toSet()
        var minDate: String? = null
        for (k in prefs.all.keys) {
            if (k.startsWith("s2_") && k.length > 14) {
                val date = k.takeLast(10)
                val area = k.removePrefix("s2_").dropLast(11)
                if (area in ar) {
                    val cur = minDate
                    if (cur == null || date < cur) minDate = date
                }
            }
        }
        val now = Calendar.getInstance()
        val curY = now.get(Calendar.YEAR)
        val curM = now.get(Calendar.MONTH)
        var yy = curY
        var mm = curM
        val md = minDate
        if (md != null) {
            yy = md.substring(0, 4).toIntOrNull() ?: curY
            mm = (md.substring(5, 7).toIntOrNull() ?: (curM + 1)) - 1
        }
        val out = mutableListOf<Pair<Int, Int>>()
        while (yy < curY || (yy == curY && mm <= curM)) {
            out.add(Pair(yy, mm))
            mm++
            if (mm > 11) {
                mm = 0
                yy++
            }
        }
        if (out.isEmpty()) out.add(Pair(curY, curM))
        return out.reversed()
    }

    fun report(year: Int, month: Int): MonthReport {
        val ar = chosen()
        val c = Calendar.getInstance()
        c.clear()
        c.set(year, month, 1)
        val days = c.getActualMaximum(Calendar.DAY_OF_MONTH)
        val firstWd = (c.get(Calendar.DAY_OF_WEEK) + 5) % 7
        val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val dayMin = MutableList(days) { 0 }
        val areaMin = HashMap<String, Int>()
        val areaXp = HashMap<String, Int>()
        for (d in 1..days) {
            c.set(year, month, d)
            val key = fmt.format(c.time)
            var total = 0
            for (a in ar) {
                val s = prefs.getInt("s2_${a}_$key", 0)
                if (s > 0) {
                    total += s / 60
                    areaMin[a] = (areaMin[a] ?: 0) + s / 60
                    areaXp[a] = (areaXp[a] ?: 0) + Xp.forMinutes(s / 60.0)
                }
            }
            dayMin[d - 1] = total
        }
        val shares = ar
            .filter { (areaMin[it] ?: 0) > 0 }
            .map { ArenaShare(it, areaMin[it] ?: 0, areaXp[it] ?: 0) }
            .sortedByDescending { it.minutes }
        var best = 0
        var bestMin = 0
        var run = 0
        var longest = 0
        var active = 0
        for (i in 0 until days) {
            if (dayMin[i] > 0) {
                active++
                run++
                if (run > longest) longest = run
                if (dayMin[i] > bestMin) {
                    bestMin = dayMin[i]
                    best = i + 1
                }
            } else {
                run = 0
            }
        }
        val py = if (month == 0) year - 1 else year
        val pm = if (month == 0) 11 else month - 1
        val prev = monthDayMinutes(py, pm, ar).sum()
        return MonthReport(
            year, month, days, firstWd, dayMin, shares,
            dayMin.sum(), shares.sumOf { it.xp },
            active, best, bestMin, longest, prev
        )
    }
}
