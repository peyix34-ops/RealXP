package com.realxp.app.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.realxp.app.data.Levels
import com.realxp.app.data.LogEntry
import com.realxp.app.data.Ranks
import com.realxp.app.data.Store
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin

val Flame = Color(0xFFFF7A1A)

fun heatLevel(min: Int): Int = when {
    min <= 0 -> 0
    min < 30 -> 1
    min < 60 -> 2
    min < 120 -> 3
    else -> 4
}

@Composable
fun Heatmap(cols: List<List<Int?>>) {
    val weeks = cols.size
    val gap = 3.dp
    val prog = animatedProgress(1f, 1500)
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val cell = (maxWidth - gap * (weeks - 1)) / weeks
        val h = cell * 7 + gap * 6
        Canvas(modifier = Modifier.fillMaxWidth().height(h)) {
            val c = cell.toPx()
            val g = gap.toPx()
            val base = RX.Line
            val tint = RX.Accent
            for (w in 0 until weeks) {
                for (r in 0 until 7) {
                    val m = cols[w][r] ?: continue
                    val t = (prog * (weeks + 5) - w).coerceIn(0f, 1f)
                    if (t <= 0f) continue
                    val color = when (heatLevel(m)) {
                        0 -> base.copy(alpha = 0.7f)
                        1 -> tint.copy(alpha = 0.28f)
                        2 -> tint.copy(alpha = 0.5f)
                        3 -> tint.copy(alpha = 0.75f)
                        else -> tint
                    }
                    val s = c * (0.4f + 0.6f * t)
                    val off = (c - s) / 2f
                    drawRoundRect(
                        color = color.copy(alpha = color.alpha * t),
                        topLeft = Offset(w * (c + g) + off, r * (c + g) + off),
                        size = Size(s, s),
                        cornerRadius = CornerRadius(3.dp.toPx())
                    )
                }
            }
        }
    }
}

@Composable
fun HeatLegend() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(T("Less"), color = RX.Muted, fontSize = 10.sp)
        Spacer(Modifier.width(6.dp))
        listOf(0.7f to 0, 0.28f to 1, 0.5f to 2, 0.75f to 3, 1f to 4).forEach { (a, lvl) ->
            Box(
                modifier = Modifier
                    .padding(horizontal = 1.5.dp)
                    .size(10.dp)
                    .background(
                        if (lvl == 0) RX.Line.copy(alpha = a) else RX.Accent.copy(alpha = a),
                        RoundedCornerShape(2.dp)
                    )
            )
        }
        Spacer(Modifier.width(6.dp))
        Text(T("More"), color = RX.Muted, fontSize = 10.sp)
    }
}

@Composable
fun HistoryRow(e: LogEntry) {
    val fmt = remember(Lang.tr) {
        SimpleDateFormat(
            "MMM d  ·  HH:mm",
            if (Lang.tr) Locale("tr", "TR") else Locale.US
        )
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .background(RX.Panel, RoundedCornerShape(14.dp))
            .border(1.dp, RX.Line, RoundedCornerShape(14.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                ftime(e.minutes * 60),
                color = RX.Text, fontSize = 15.sp, fontWeight = FontWeight.SemiBold
            )
            Text(fmt.format(Date(e.ts)), color = RX.Muted, fontSize = 12.sp)
        }
        Text("+${e.gain} XP", color = RX.Accent, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun StreakPill(days: Int) {
    Row(
        modifier = Modifier
            .background(
                Brush.horizontalGradient(listOf(Color(0x33FF8A1A), Color(0x22FF3D2E))),
                RoundedCornerShape(50)
            )
            .border(1.dp, Color(0x55FF8A1A), RoundedCornerShape(50))
            .padding(horizontal = 14.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FlameIcon(24.dp)
        Spacer(Modifier.width(8.dp))
        Text(
            if (days == 1) T("1 day streak") else TF("%d day streak", days),
            color = Flame, fontSize = 13.sp, fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun RoundIconButton(icon: ImageVector, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .bounceClick { onClick() }
            .size(38.dp)
            .background(RX.Panel, CircleShape)
            .border(1.dp, RX.Line, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, null, tint = RX.Muted, modifier = Modifier.size(20.dp))
    }
}

@Composable
fun DialogButton(label: String, color: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .border(1.dp, RX.Line, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(label, color = color, fontSize = 14.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
    }
}

@Composable
fun HeroRing(p: Float, level: Int) {
    val breathe by rememberInfiniteTransition(label = "hero").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2400, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "heroT"
    )
    Box(contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(176.dp)) {
            val stroke = 12.dp.toPx()
            val tl = Offset(stroke / 2, stroke / 2)
            val sz = Size(size.width - stroke, size.height - stroke)
            if (p > 0.01f) {
                drawArc(
                    color = RX.Accent.copy(alpha = 0.10f + 0.07f * breathe),
                    startAngle = -90f, sweepAngle = 360f * p, useCenter = false,
                    topLeft = tl, size = sz,
                    style = Stroke(stroke * 1.9f, cap = StrokeCap.Round)
                )
            }
            drawArc(
                color = RX.Line,
                startAngle = -90f, sweepAngle = 360f, useCenter = false,
                topLeft = tl, size = sz, style = Stroke(stroke)
            )
            if (p > 0.01f) {
                rotate(-90f) {
                    drawArc(
                        brush = Brush.sweepGradient(listOf(RX.Blue, RX.Accent)),
                        startAngle = 0f, sweepAngle = 360f * p, useCenter = false,
                        topLeft = tl, size = sz,
                        style = Stroke(stroke, cap = StrokeCap.Round)
                    )
                }
                val r = (size.width - stroke) / 2f
                val ang = Math.toRadians((-90f + 360f * p).toDouble())
                val ex = size.width / 2f + r * cos(ang).toFloat()
                val ey = size.height / 2f + r * sin(ang).toFloat()
                drawCircle(RX.Accent.copy(alpha = 0.35f), radius = stroke * 0.7f, center = Offset(ex, ey))
                drawCircle(Color.White, radius = stroke * 0.3f, center = Offset(ex, ey))
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(T("LEVEL"), color = RX.Muted, fontSize = 10.sp, letterSpacing = 3.sp)
            Text("$level", color = RX.Text, fontSize = 52.sp, fontWeight = FontWeight.Bold)
            RankText(
                up(T(Ranks.title(level))), level, 10.sp,
                fontWeight = FontWeight.Bold, letterSpacing = 2.sp
            )
        }
    }
}

@Composable
fun HomeHub(
    store: Store,
    chosen: List<String>,
    tick: Int,
    onOpen: (String) -> Unit,
    onAdd: () -> Unit,
    onRemove: (String, Boolean) -> Unit,
    onToggleDark: () -> Unit,
    onSetLang: (Boolean) -> Unit,
    onProfile: () -> Unit,
    onReport: (Int, Int) -> Unit
) {
    val total = remember(tick) { store.totalXp() }
    val investedSec = remember(tick) { store.allSeconds() }
    val streak = remember(tick) { store.streak() }
    val pending = remember(tick) { store.pendingReport() }
    val info = Levels.overall(total)
    val p = animatedProgress(info.into.toFloat() / info.need, 1400)
    val intoShown = countUp(info.into, 1200)
    var editing by remember { mutableStateOf(false) }
    var toRemove by remember { mutableStateOf<String?>(null) }
    var showSettings by remember { mutableStateOf(false) }

    val rm = toRemove
    if (rm != null) {
        AlertDialog(
            onDismissRequest = { toRemove = null },
            containerColor = RX.Panel,
            title = { Text(TF("Remove %s?", A(rm)), color = RX.Text) },
            text = {
                Column {
                    Text(
                        T("Keep your progress hidden, or erase it for good?"),
                        color = RX.Muted, fontSize = 14.sp
                    )
                    Spacer(Modifier.height(16.dp))
                    DialogButton(T("Remove, keep progress"), RX.Accent) {
                        toRemove = null
                        onRemove(rm, false)
                    }
                    DialogButton(T("Remove and erase progress"), Danger) {
                        toRemove = null
                        onRemove(rm, true)
                    }
                    DialogButton(T("Cancel"), RX.Muted) { toRemove = null }
                }
            },
            confirmButton = {}
        )
    }

    if (showSettings) {
        AlertDialog(
            onDismissRequest = { showSettings = false },
            containerColor = RX.Panel,
            title = { Text(T("Settings"), color = RX.Text, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(T("Dark mode"), color = RX.Text, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                            Text(T("Easier on the eyes at night."), color = RX.Muted, fontSize = 12.sp)
                        }
                        Switch(
                            checked = RX.dark,
                            onCheckedChange = { onToggleDark() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = RX.Accent,
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = RX.Line,
                                uncheckedBorderColor = RX.Line
                            )
                        )
                    }
                    Spacer(Modifier.height(20.dp))
                    Text(T("Language"), color = RX.Text, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(10.dp))
                    Row {
                        MonthChip("English", !Lang.tr) { onSetLang(false) }
                        Spacer(Modifier.width(8.dp))
                        MonthChip("Türkçe", Lang.tr) { onSetLang(true) }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSettings = false }) {
                    Text(T("Done"), color = RX.Accent)
                }
            }
        )
    }

    val cells = buildList<String?> {
        addAll(chosen)
        if (!editing && chosen.size < Catalog.all.size) add(null)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RX.Page)
            .systemBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(14.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                buildAnnotatedString {
                    withStyle(SpanStyle(color = RX.Text)) { append("REAL ") }
                    withStyle(SpanStyle(color = RX.Accent)) { append("XP") }
                },
                fontSize = 18.sp, fontWeight = FontWeight.ExtraBold
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                RoundIconButton(Icons.Outlined.Person) { onProfile() }
                Spacer(Modifier.width(8.dp))
                RoundIconButton(Icons.Outlined.Settings) { showSettings = true }
            }
        }
        Spacer(Modifier.height(16.dp))
        HeroRing(p, info.level)
        Spacer(Modifier.height(12.dp))
        Text(
            "$intoShown / ${info.need} XP   ·   ${ftime(investedSec)} ${T("invested")}",
            color = RX.Muted, fontSize = 12.sp
        )
        if (streak > 0) {
            Spacer(Modifier.height(12.dp))
            StreakPill(streak)
        }
        if (pending != null) {
            Spacer(Modifier.height(16.dp))
            ReportCard(
                TF("%s report is ready", monthName(pending.second)),
                T("See how your month went"), true
            ) { onReport(pending.first, pending.second) }
        }
        Spacer(Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(T("YOUR ARENAS"), color = RX.Accent, fontSize = 11.sp, letterSpacing = 3.sp, fontWeight = FontWeight.Bold)
            if (chosen.size > 1) {
                Text(
                    if (editing) T("DONE") else T("EDIT"),
                    color = RX.Accent, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp,
                    modifier = Modifier.clickable { editing = !editing }.padding(vertical = 6.dp)
                )
            }
        }
        Spacer(Modifier.height(10.dp))
        cells.chunked(2).forEachIndexed { r, row ->
            Row(modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)) {
                row.forEachIndexed { c, name ->
                    if (c > 0) Spacer(Modifier.width(10.dp))
                    val idx = r * 2 + c
                    if (name == null) {
                        AddCell(Modifier.weight(1f).staggerIn(minOf(idx, 12))) { onAdd() }
                    } else {
                        ArenaCard2(
                            name, store, tick, editing, chosen.size > 1,
                            Modifier.weight(1f).staggerIn(minOf(idx, 12))
                        ) {
                            if (editing) {
                                if (chosen.size > 1) toRemove = name
                            } else {
                                onOpen(name)
                            }
                        }
                    }
                }
                repeat(2 - row.size) {
                    Spacer(Modifier.width(10.dp))
                    Spacer(Modifier.weight(1f))
                }
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
fun StatCard(
    icon: ImageVector,
    label: String,
    value: String,
    sub: String?,
    tint: Color,
    modifier: Modifier,
    flame: Boolean = false
) {
    Column(
        modifier = modifier
            .background(RX.Panel, RoundedCornerShape(16.dp))
            .border(1.dp, RX.Line, RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        if (flame) {
            FlameIcon(26.dp)
        } else {
            Icon(icon, null, tint = tint, modifier = Modifier.size(22.dp))
        }
        Spacer(Modifier.height(10.dp))
        Text(label, color = RX.Muted, fontSize = 11.sp)
        Spacer(Modifier.height(2.dp))
        Text(value, color = RX.Text, fontSize = 20.sp, fontWeight = FontWeight.Bold, maxLines = 1)
        if (sub != null) {
            Spacer(Modifier.height(2.dp))
            Text(sub, color = RX.Muted, fontSize = 11.sp)
        }
    }
}

@Composable
fun ProfileScreen(store: Store, onBack: () -> Unit, onReports: () -> Unit) {
    val total = remember { store.totalXp() }
    val info = Levels.overall(total)
    val p = animatedProgress(info.into.toFloat() / info.need, 1000)
    val streak = remember { store.streak() }
    val best = remember { store.bestStreak() }
    val timeSec = remember { store.allSeconds() }
    val logs = remember { store.totalLogs() }
    val heat = remember { store.heat(16, null) }
    val totalShown = countUp(total, 1100)
    val logsShown = countUp(logs, 1100)

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
        Spacer(Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(RX.Accent.copy(alpha = 0.12f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.Person, null, tint = RX.Accent, modifier = Modifier.size(34.dp))
            }
            Spacer(Modifier.width(14.dp))
            Column {
                RankText(
                    T(Ranks.title(info.level)), info.level, 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(TF("Level %d", info.level), color = RX.Muted, fontSize = 13.sp)
            }
        }
        Spacer(Modifier.height(16.dp))
        Bar(p, 6, RankColors.tone(info.level))
        Spacer(Modifier.height(6.dp))
        Text("${info.into} / ${info.need} XP", color = RX.Muted, fontSize = 12.sp)

        Spacer(Modifier.height(24.dp))
        Text(T("MY STATS"), color = RX.Accent, fontSize = 11.sp, letterSpacing = 3.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(10.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            StatCard(
                Icons.Outlined.LocalFireDepartment, T("Current Streak"),
                daysText(streak),
                TF("Best: %d", best), Flame, Modifier.weight(1f).staggerIn(0),
                flame = true
            )
            Spacer(Modifier.width(10.dp))
            StatCard(
                Icons.Outlined.EmojiEvents, T("Total XP"), "$totalShown",
                null, RankColors.gold, Modifier.weight(1f).staggerIn(1)
            )
        }
        Spacer(Modifier.height(10.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            StatCard(
                Icons.Outlined.Schedule, T("Time Invested"), ftime(timeSec),
                null, RX.Accent, Modifier.weight(1f).staggerIn(2)
            )
            Spacer(Modifier.width(10.dp))
            StatCard(
                Icons.Outlined.BarChart, T("Total Logs"), "$logsShown",
                null, RX.Accent, Modifier.weight(1f).staggerIn(3)
            )
        }

        Spacer(Modifier.height(16.dp))
        ReportCard(T("Monthly Reports"), T("See every month at a glance"), false) { onReports() }

        Spacer(Modifier.height(24.dp))
        Text(T("ACTIVITY  ·  LAST 16 WEEKS"), color = RX.Accent, fontSize = 11.sp, letterSpacing = 3.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        Heatmap(heat)
        Spacer(Modifier.height(8.dp))
        HeatLegend()
        Spacer(Modifier.height(32.dp))
    }
}
