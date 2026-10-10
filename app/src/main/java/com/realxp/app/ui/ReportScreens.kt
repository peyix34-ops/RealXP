package com.realxp.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.realxp.app.data.ArenaShare
import com.realxp.app.data.DayStat
import com.realxp.app.data.MonthReport
import com.realxp.app.data.Store
import com.realxp.app.data.monthKey
import java.util.Calendar

@Composable
fun ReportCard(title: String, subtitle: String, highlight: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .bounceClick { onClick() }
            .background(
                if (highlight) RX.Accent.copy(alpha = 0.10f) else RX.Panel,
                RoundedCornerShape(16.dp)
            )
            .border(
                1.dp,
                if (highlight) RX.Accent else RX.Line,
                RoundedCornerShape(16.dp)
            )
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(RX.Accent.copy(alpha = 0.12f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Outlined.BarChart, null, tint = RX.Accent, modifier = Modifier.size(22.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = RX.Text, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            Text(subtitle, color = RX.Muted, fontSize = 12.sp)
        }
        Icon(Icons.Outlined.ChevronRight, null, tint = RX.Muted, modifier = Modifier.size(20.dp))
    }
}

@Composable
fun MonthChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .then(
                if (selected) Modifier.background(RX.Accent.copy(alpha = 0.14f), RoundedCornerShape(50))
                else Modifier
            )
            .border(1.dp, if (selected) RX.Accent else RX.Line, RoundedCornerShape(50))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            label,
            color = if (selected) RX.Accent else RX.Muted,
            fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 1
        )
    }
}

@Composable
fun MonthCalendar(rep: MonthReport) {
    val cells = rep.firstWeekday + rep.days
    val rows = (cells + 6) / 7
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth()) {
            for (i in 0 until 7) {
                Text(
                    dayLetter(i),
                    color = RX.Muted, fontSize = 11.sp, textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        for (r in 0 until rows) {
            Row(modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
                for (c in 0 until 7) {
                    val idx = r * 7 + c - rep.firstWeekday
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(2.dp)
                            .aspectRatio(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        if (idx in 0 until rep.days) {
                            val mins = rep.dayMinutes[idx]
                            val lvl = heatLevel(mins)
                            val bg = when (lvl) {
                                0 -> RX.Line.copy(alpha = 0.5f)
                                1 -> RX.Accent.copy(alpha = 0.28f)
                                2 -> RX.Accent.copy(alpha = 0.5f)
                                3 -> RX.Accent.copy(alpha = 0.75f)
                                else -> RX.Accent
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(bg, RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "${idx + 1}",
                                    color = if (lvl >= 3) Color.White else if (lvl == 0) RX.Muted else RX.Text,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ArenaShareRow(s: ArenaShare, total: Int) {
    val frac = if (total > 0) s.minutes.toFloat() / total else 0f
    val p = animatedProgress(frac, 900)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ArenaBadge(s.area, 38)
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(A(s.area), color = RX.Text, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Text(ftime(s.minutes * 60), color = RX.Muted, fontSize = 13.sp)
            }
            Spacer(Modifier.height(6.dp))
            Bar(p, 5)
            Spacer(Modifier.height(4.dp))
            Text(
                "${pctText((frac * 100).toInt())}  ·  +${s.xp} XP",
                color = RX.Muted, fontSize = 11.sp
            )
        }
    }
}

@Composable
fun InsightRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .background(RX.Panel, RoundedCornerShape(12.dp))
            .border(1.dp, RX.Line, RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = RX.Muted, fontSize = 13.sp)
        Text(value, color = RX.Text, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun ReportBody(rep: MonthReport) {
    val avg = if (rep.activeDays > 0) rep.totalMinutes / rep.activeDays else 0
    val pct = if (rep.days > 0) rep.activeDays * 100 / rep.days else 0
    val compare = when {
        rep.prevMinutes <= 0 -> T("First tracked month")
        rep.totalMinutes >= rep.prevMinutes ->
            "▲ ${pctText((rep.totalMinutes - rep.prevMinutes) * 100 / rep.prevMinutes)} ${T("vs last month")}"
        else ->
            "▼ ${pctText((rep.prevMinutes - rep.totalMinutes) * 100 / rep.prevMinutes)} ${T("vs last month")}"
    }

    if (rep.totalMinutes == 0) {
        Text(
            TF("No activity in %s.", monthName(rep.month)),
            color = RX.Muted, fontSize = 15.sp
        )
        return
    }

    Row(modifier = Modifier.fillMaxWidth()) {
        StatCard(
            Icons.Outlined.Schedule, T("Total Time"),
            ftime(rep.totalMinutes * 60), compare, RX.Accent,
            Modifier.weight(1f).staggerIn(0)
        )
        Spacer(Modifier.width(10.dp))
        StatCard(
            Icons.Outlined.EmojiEvents, T("XP Earned"), "${rep.totalXp}",
            null, RankColors.gold, Modifier.weight(1f).staggerIn(1)
        )
    }
    Spacer(Modifier.height(10.dp))
    Row(modifier = Modifier.fillMaxWidth()) {
        StatCard(
            Icons.Outlined.CalendarMonth, T("Active Days"),
            "${rep.activeDays} / ${rep.days}", "${pctText(pct)} ${T("consistency")}", RX.Accent,
            Modifier.weight(1f).staggerIn(2)
        )
        Spacer(Modifier.width(10.dp))
        StatCard(
            Icons.Outlined.Star, T("Best Day"),
            ftime(rep.bestMinutes * 60),
            "${monthName(rep.month).take(3)} ${rep.bestDay}", Flame,
            Modifier.weight(1f).staggerIn(3)
        )
    }

    Spacer(Modifier.height(24.dp))
    Text(T("INSIGHTS"), color = RX.Accent, fontSize = 11.sp, letterSpacing = 3.sp, fontWeight = FontWeight.Bold)
    Spacer(Modifier.height(10.dp))
    InsightRow(T("Longest streak"), daysText(rep.longestStreak))
    InsightRow(T("Average on active days"), ftime(avg * 60))
    if (rep.arenas.isNotEmpty()) {
        val top = rep.arenas.first()
        val share = if (rep.totalMinutes > 0) top.minutes * 100 / rep.totalMinutes else 0
        InsightRow(T("Most invested"), "${A(top.area)}  ·  ${pctText(share)}")
    }

    Spacer(Modifier.height(20.dp))
    Text(T("CALENDAR"), color = RX.Accent, fontSize = 11.sp, letterSpacing = 3.sp, fontWeight = FontWeight.Bold)
    Spacer(Modifier.height(10.dp))
    MonthCalendar(rep)

    Spacer(Modifier.height(24.dp))
    Text(T("WEEK BY WEEK"), color = RX.Accent, fontSize = 11.sp, letterSpacing = 3.sp, fontWeight = FontWeight.Bold)
    Spacer(Modifier.height(10.dp))
    val weeks = rep.dayMinutes.chunked(7).mapIndexed { i, l -> DayStat("W${i + 1}", l.sum(), false) }
    WeekChart(weeks, RX.Accent)

    Spacer(Modifier.height(24.dp))
    Text(T("ARENAS"), color = RX.Accent, fontSize = 11.sp, letterSpacing = 3.sp, fontWeight = FontWeight.Bold)
    Spacer(Modifier.height(12.dp))
    rep.arenas.forEach { ArenaShareRow(it, rep.totalMinutes) }
}

@Composable
fun ReportsScreen(store: Store, startYear: Int?, startMonth: Int?, onBack: () -> Unit) {
    val months = remember { store.reportMonths() }
    val first = if (startYear != null && startMonth != null && Pair(startYear, startMonth) in months) {
        Pair(startYear, startMonth)
    } else {
        months.first()
    }
    var sel by remember { mutableStateOf(first) }
    val rep = remember(sel) { store.report(sel.first, sel.second) }

    val now = Calendar.getInstance()
    var py = now.get(Calendar.YEAR)
    var pm = now.get(Calendar.MONTH) - 1
    if (pm < 0) {
        pm = 11
        py--
    }
    val prevKey = monthKey(py, pm)

    LaunchedEffect(sel) {
        if (monthKey(sel.first, sel.second) == prevKey) store.markReportSeen(prevKey)
    }

    val curKey = monthKey(now.get(Calendar.YEAR), now.get(Calendar.MONTH))
    val isCurrent = monthKey(sel.first, sel.second) == curKey

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RX.Page)
            .systemBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(10.dp))
        Text(
            T("←  Back"),
            color = RX.Muted, fontSize = 14.sp,
            modifier = Modifier.clickable { onBack() }.padding(vertical = 8.dp)
        )
        Spacer(Modifier.height(8.dp))
        Text(up(T("Monthly report")), color = RX.Accent, fontSize = 11.sp, letterSpacing = 3.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())) {
            months.forEach { ym ->
                MonthChip(
                    "${monthName(ym.second).take(3)} ${ym.first}",
                    ym == sel
                ) { sel = ym }
                Spacer(Modifier.width(8.dp))
            }
        }
        Spacer(Modifier.height(20.dp))
        Text(
            "${monthName(sel.second)} ${sel.first}",
            color = RX.Text, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold
        )
        Text(
            if (isCurrent) T("In progress") else T("Complete"),
            color = RX.Muted, fontSize = 13.sp
        )
        Spacer(Modifier.height(18.dp))
        ReportBody(rep)
        Spacer(Modifier.height(40.dp))
    }
}
