package com.realxp.app.ui

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.realxp.app.data.Areas
import com.realxp.app.data.Levels
import com.realxp.app.data.LogEntry
import com.realxp.app.data.Ranks
import com.realxp.app.data.Store
import com.realxp.app.data.formatTime
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
                    val color = when (heatLevel(m)) {
                        0 -> base.copy(alpha = 0.7f)
                        1 -> tint.copy(alpha = 0.28f)
                        2 -> tint.copy(alpha = 0.5f)
                        3 -> tint.copy(alpha = 0.75f)
                        else -> tint
                    }
                    drawRoundRect(
                        color = color,
                        topLeft = Offset(w * (c + g), r * (c + g)),
                        size = Size(c, c),
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
        Text("Less", color = RX.Muted, fontSize = 10.sp)
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
        Text("More", color = RX.Muted, fontSize = 10.sp)
    }
}

@Composable
fun HistoryRow(e: LogEntry) {
    val fmt = remember { SimpleDateFormat("MMM d  ·  HH:mm", Locale.US) }
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
                formatTime(e.minutes * 60),
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
            .background(Flame.copy(alpha = 0.12f), RoundedCornerShape(50))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Outlined.LocalFireDepartment, null, tint = Flame, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(6.dp))
        Text(
            if (days == 1) "1 day streak" else "$days day streak",
            color = Flame, fontSize = 12.sp, fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun RoundIconButton(icon: ImageVector, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(38.dp)
            .background(RX.Panel, CircleShape)
            .border(1.dp, RX.Line, CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, null, tint = RX.Muted, modifier = Modifier.size(20.dp))
    }
}

@Composable
fun HomeHub(
    store: Store,
    chosen: List<String>,
    tick: Int,
    onOpen: (String) -> Unit,
    onAdd: () -> Unit,
    onRemove: (String) -> Unit,
    onToggleDark: () -> Unit,
    onProfile: () -> Unit
) {
    val total = remember(tick) { store.totalXp() }
    val invested = remember(tick) { formatTime(store.allSeconds()) }
    val streak = remember(tick) { store.streak() }
    val info = Levels.overall(total)
    val p = animatedProgress(info.into.toFloat() / info.need, 1400)
    var editing by remember { mutableStateOf(false) }
    var toRemove by remember { mutableStateOf<String?>(null) }
    var showSettings by remember { mutableStateOf(false) }

    val rm = toRemove
    if (rm != null) {
        AlertDialog(
            onDismissRequest = { toRemove = null },
            containerColor = RX.Panel,
            title = { Text("Remove $rm?", color = RX.Text) },
            text = { Text("Your XP is kept. You can add it back anytime.", color = RX.Muted) },
            confirmButton = {
                TextButton(onClick = { toRemove = null; onRemove(rm) }) {
                    Text("Remove", color = Danger)
                }
            },
            dismissButton = {
                TextButton(onClick = { toRemove = null }) {
                    Text("Cancel", color = RX.Muted)
                }
            }
        )
    }

    if (showSettings) {
        AlertDialog(
            onDismissRequest = { showSettings = false },
            containerColor = RX.Panel,
            title = { Text("Settings", color = RX.Text, fontWeight = FontWeight.Bold) },
            text = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Dark mode", color = RX.Text, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        Text("Easier on the eyes at night.", color = RX.Muted, fontSize = 12.sp)
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
            },
            confirmButton = {
                TextButton(onClick = { showSettings = false }) {
                    Text("Done", color = RX.Accent)
                }
            }
        )
    }

    val cells = buildList<String?> {
        addAll(chosen)
        if (!editing && chosen.size < Areas.all.size) add(null)
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
        Box(contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.size(160.dp)) {
                val stroke = 9.dp.toPx()
                val topLeft = Offset(stroke / 2, stroke / 2)
                val arcSize = Size(size.width - stroke, size.height - stroke)
                drawArc(
                    color = RX.Line, startAngle = -90f, sweepAngle = 360f, useCenter = false,
                    topLeft = topLeft, size = arcSize,
                    style = Stroke(stroke, cap = StrokeCap.Round)
                )
                if (p > 0.002f) {
                    drawArc(
                        brush = Brush.linearGradient(listOf(RX.Blue, RX.Accent)),
                        startAngle = -90f, sweepAngle = 360f * p, useCenter = false,
                        topLeft = topLeft, size = arcSize,
                        style = Stroke(stroke, cap = StrokeCap.Round)
                    )
                }
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("LEVEL", color = RX.Muted, fontSize = 10.sp, letterSpacing = 3.sp)
                Text("${info.level}", color = RX.Text, fontSize = 48.sp, fontWeight = FontWeight.Bold)
                RankText(
                    Ranks.title(info.level).uppercase(), info.level, 10.sp,
                    fontWeight = FontWeight.Bold, letterSpacing = 2.sp
                )
            }
        }
        Spacer(Modifier.height(10.dp))
        Text(
            "${info.into} / ${info.need} XP   ·   $invested invested",
            color = RX.Muted, fontSize = 12.sp
        )
        if (streak > 0) {
            Spacer(Modifier.height(10.dp))
            StreakPill(streak)
        }
        Spacer(Modifier.height(22.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("YOUR ARENAS", color = RX.Accent, fontSize = 11.sp, letterSpacing = 3.sp, fontWeight = FontWeight.Bold)
            if (chosen.size > 1) {
                Text(
                    if (editing) "DONE" else "EDIT",
                    color = RX.Accent, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp,
                    modifier = Modifier.clickable { editing = !editing }.padding(vertical = 6.dp)
                )
            }
        }
        Spacer(Modifier.height(10.dp))
        cells.chunked(2).forEach { row ->
            Row(modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)) {
                row.forEachIndexed { i, name ->
                    if (i > 0) Spacer(Modifier.width(10.dp))
                    if (name == null) {
                        AddCell(Modifier.weight(1f)) { onAdd() }
                    } else {
                        ArenaCell(name, store, tick, editing, chosen.size > 1, Modifier.weight(1f)) {
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
    modifier: Modifier
) {
    Column(
        modifier = modifier
            .background(RX.Panel, RoundedCornerShape(16.dp))
            .border(1.dp, RX.Line, RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Icon(icon, null, tint = tint, modifier = Modifier.size(22.dp))
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
fun ProfileScreen(store: Store, onBack: () -> Unit) {
    val total = remember { store.totalXp() }
    val info = Levels.overall(total)
    val p = animatedProgress(info.into.toFloat() / info.need, 1000)
    val streak = remember { store.streak() }
    val best = remember { store.bestStreak() }
    val time = remember { formatTime(store.allSeconds()) }
    val logs = remember { store.totalLogs() }
    val heat = remember { store.heat(16, null) }

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
            "←  Back",
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
                    Ranks.title(info.level), info.level, 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text("Level ${info.level}", color = RX.Muted, fontSize = 13.sp)
            }
        }
        Spacer(Modifier.height(16.dp))
        Bar(p, 6, RankColors.tone(info.level))
        Spacer(Modifier.height(6.dp))
        Text("${info.into} / ${info.need} XP", color = RX.Muted, fontSize = 12.sp)

        Spacer(Modifier.height(24.dp))
        Text("MY STATS", color = RX.Accent, fontSize = 11.sp, letterSpacing = 3.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(10.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            StatCard(
                Icons.Outlined.LocalFireDepartment, "Current Streak",
                if (streak == 1) "1 day" else "$streak days",
                "Best: $best", Flame, Modifier.weight(1f)
            )
            Spacer(Modifier.width(10.dp))
            StatCard(
                Icons.Outlined.EmojiEvents, "Total XP", "$total",
                null, RankColors.gold, Modifier.weight(1f)
            )
        }
        Spacer(Modifier.height(10.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            StatCard(
                Icons.Outlined.Schedule, "Time Invested", time,
                null, RX.Accent, Modifier.weight(1f)
            )
            Spacer(Modifier.width(10.dp))
            StatCard(
                Icons.Outlined.BarChart, "Total Logs", "$logs",
                null, RX.Accent, Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(24.dp))
        Text("ACTIVITY  ·  LAST 16 WEEKS", color = RX.Accent, fontSize = 11.sp, letterSpacing = 3.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        Heatmap(heat)
        Spacer(Modifier.height(8.dp))
        HeatLegend()
        Spacer(Modifier.height(32.dp))
    }
}
