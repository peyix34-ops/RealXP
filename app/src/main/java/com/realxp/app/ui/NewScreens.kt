package com.realxp.app.ui

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.realxp.app.data.Areas
import com.realxp.app.data.Levels
import com.realxp.app.data.Ranks
import com.realxp.app.data.Store
import com.realxp.app.data.formatTime

val Danger = Color(0xFFFF6B6B)

fun arenaColor(name: String): Color = when (name) {
    "English" -> Color(0xFF3D7BFF)
    "Fitness" -> Color(0xFF8B6BFF)
    "Reading" -> Color(0xFF22C493)
    "History" -> Color(0xFFF08A3C)
    "Coding" -> Color(0xFF1CC6E0)
    "Math" -> Color(0xFFE5539A)
    "Writing" -> Color(0xFF9B7BFF)
    "Music" -> Color(0xFF5B72FF)
    "Finance" -> Color(0xFFF2B93B)
    "Science" -> Color(0xFF24D1C4)
    "Art" -> Color(0xFFFF6F61)
    "Meditation" -> Color(0xFF6CC7A8)
    else -> RX.Blue
}

@Composable
fun ArenaChoice(name: String, on: Boolean, modifier: Modifier, onClick: () -> Unit) {
    val border by animateColorAsState(if (on) RX.Accent else RX.Line, tween(200), label = "cb")
    val bg by animateColorAsState(
        if (on) RX.Accent.copy(alpha = 0.12f) else RX.Panel, tween(200), label = "cbg"
    )
    Row(
        modifier = modifier
            .height(52.dp)
            .background(bg, RoundedCornerShape(14.dp))
            .border(1.dp, border, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.width(14.dp), contentAlignment = Alignment.Center) {
            if (on) {
                Text("✓", color = RX.Accent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            } else {
                Box(modifier = Modifier.size(8.dp).background(arenaColor(name), CircleShape))
            }
        }
        Spacer(Modifier.width(4.dp))
        Text(
            name,
            color = if (on) RX.Accent else RX.Text,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun ArenaPicker(
    options: List<String>,
    title: String,
    buttonLabel: String,
    onBack: (() -> Unit)?,
    onDone: (List<String>) -> Unit
) {
    var selected by remember { mutableStateOf(listOf<String>()) }
    val ready = selected.isNotEmpty()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RX.Page)
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(16.dp))
            if (onBack != null) {
                Text(
                    "←  Back",
                    color = RX.Muted, fontSize = 14.sp,
                    modifier = Modifier.clickable { onBack() }.padding(vertical = 8.dp)
                )
                Spacer(Modifier.height(4.dp))
            }
            Text(
                if (onBack == null) "CHOOSE YOUR AREAS" else "ADD AREAS",
                color = RX.Muted, fontSize = 11.sp, letterSpacing = 3.sp
            )
            Spacer(Modifier.height(8.dp))
            Text(
                title,
                color = RX.Text, fontSize = 24.sp, fontWeight = FontWeight.Bold, lineHeight = 30.sp
            )
            Spacer(Modifier.height(20.dp))
            if (options.isEmpty()) {
                Text("You've added every arena.", color = RX.Muted, fontSize = 15.sp)
            }
            options.chunked(3).forEach { row ->
                Row(modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)) {
                    row.forEachIndexed { i, name ->
                        if (i > 0) Spacer(Modifier.width(10.dp))
                        ArenaChoice(name, name in selected, Modifier.weight(1f)) {
                            selected = if (name in selected) selected - name else selected + name
                        }
                    }
                    repeat(3 - row.size) {
                        Spacer(Modifier.width(10.dp))
                        Spacer(Modifier.weight(1f))
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
        }
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp)) {
            Text(
                if (ready) "${selected.size} selected" else "Select at least one area to continue.",
                color = RX.Muted, fontSize = 12.sp
            )
            Spacer(Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(
                        if (ready) Modifier.background(RX.Button, RoundedCornerShape(50))
                        else Modifier.border(1.dp, RX.Line, RoundedCornerShape(50))
                    )
                    .clickable(enabled = ready) { onDone(selected) }
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    buttonLabel,
                    color = if (ready) Color.White else RX.Muted,
                    fontSize = 14.sp, fontWeight = FontWeight.Bold, letterSpacing = 3.sp
                )
            }
        }
    }
}

@Composable
fun ArenaCell(
    area: String,
    store: Store,
    tick: Int,
    editing: Boolean,
    canRemove: Boolean,
    modifier: Modifier,
    onClick: () -> Unit
) {
    val xp = remember(tick) { store.xp(area) }
    val live = remember(tick) { store.running()?.area == area }
    val info = Levels.of(xp)
    val p = animatedProgress(info.into.toFloat() / info.need, 900)
    Column(
        modifier = modifier
            .height(96.dp)
            .background(RX.Panel, RoundedCornerShape(14.dp))
            .border(1.dp, if (live) RX.Accent else RX.Line, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(8.dp).background(arenaColor(area), CircleShape))
            Spacer(Modifier.width(6.dp))
            Text(
                area,
                color = RX.Text, fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
                maxLines = 1, overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            if (editing && canRemove) {
                Text("✕", color = Danger, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            } else if (live) {
                Text("●", color = RX.Accent, fontSize = 10.sp)
            }
        }
        Spacer(Modifier.height(2.dp))
        Text(Ranks.title(info.level), color = RX.Muted, fontSize = 10.sp)
        Spacer(Modifier.weight(1f))
        Text("LV ${info.level}", color = RX.Accent, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(6.dp))
        Bar(p, 3)
    }
}

@Composable
fun AddCell(modifier: Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .height(96.dp)
            .border(1.dp, RX.Line, RoundedCornerShape(14.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text("+", color = RX.Accent, fontSize = 26.sp, fontWeight = FontWeight.Light)
    }
}

@Composable
fun LifeHome(
    store: Store,
    chosen: List<String>,
    tick: Int,
    onOpen: (String) -> Unit,
    onAdd: () -> Unit,
    onRemove: (String) -> Unit
) {
    val total = remember(tick) { store.totalXp() }
    val invested = remember(tick) { formatTime(store.allSeconds()) }
    val info = Levels.overall(total)
    val p = animatedProgress(info.into.toFloat() / info.need, 1400)
    var editing by remember { mutableStateOf(false) }
    var toRemove by remember { mutableStateOf<String?>(null) }

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
            Text("REAL XP", color = RX.Text, fontSize = 13.sp, letterSpacing = 5.sp, fontWeight = FontWeight.Bold)
            if (chosen.size > 1) {
                Text(
                    if (editing) "DONE" else "EDIT",
                    color = RX.Accent, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp,
                    modifier = Modifier.clickable { editing = !editing }.padding(vertical = 6.dp)
                )
            }
        }
        Spacer(Modifier.height(18.dp))
        Box(contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.size(150.dp)) {
                val stroke = 8.dp.toPx()
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
                Text("${info.level}", color = RX.Text, fontSize = 46.sp, fontWeight = FontWeight.Bold)
                Text(
                    Ranks.title(info.level).uppercase(),
                    color = RX.Accent, fontSize = 10.sp, letterSpacing = 2.sp, fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(Modifier.height(10.dp))
        Text(
            "${info.into} / ${info.need} XP   ·   $invested invested",
            color = RX.Muted, fontSize = 12.sp
        )
        Spacer(Modifier.height(24.dp))
        Text(
            "YOUR ARENAS",
            color = RX.Muted, fontSize = 11.sp, letterSpacing = 3.sp,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(Modifier.height(10.dp))
        cells.chunked(3).forEach { row ->
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
                repeat(3 - row.size) {
                    Spacer(Modifier.width(10.dp))
                    Spacer(Modifier.weight(1f))
                }
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}
