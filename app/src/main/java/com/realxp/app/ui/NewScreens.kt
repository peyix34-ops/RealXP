package com.realxp.app.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.realxp.app.data.Areas
import com.realxp.app.data.Levels
import com.realxp.app.data.Ranks
import com.realxp.app.data.Store
import com.realxp.app.data.formatTime

val Danger = Color(0xFFFF6B6B)

fun arenaColor(name: String): Color = when (name) {
    "English" -> Color(0xFF1F5FE0)
    "Fitness" -> Color(0xFF5B3FD6)
    "Reading" -> Color(0xFF12A07A)
    "History" -> Color(0xFFD9732B)
    "Coding" -> Color(0xFF0E9DB5)
    "Math" -> Color(0xFFC23A7A)
    "Writing" -> Color(0xFF6A4BDD)
    "Music" -> Color(0xFF3F55E0)
    "Finance" -> Color(0xFFD9A02B)
    "Science" -> Color(0xFF12A6A0)
    "Art" -> Color(0xFFE0574F)
    "Meditation" -> Color(0xFF4C9F8A)
    else -> RX.Blue
}

@Composable
fun ArenaTile(name: String, size: Int) {
    val c = arenaColor(name)
    Box(
        modifier = Modifier
            .size(size.dp)
            .background(
                Brush.verticalGradient(listOf(c, c.copy(alpha = 0.65f))),
                RoundedCornerShape((size / 3).dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            name.take(1),
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = (size * 0.45f).sp
        )
    }
}

@Composable
fun ArenaChoice(name: String, on: Boolean, modifier: Modifier, onClick: () -> Unit) {
    val border by animateColorAsState(if (on) RX.Accent else RX.Line, tween(250), label = "cb")
    val bg by animateColorAsState(
        if (on) RX.Accent.copy(alpha = 0.10f) else RX.Panel, tween(250), label = "cbg"
    )
    val dot by animateColorAsState(if (on) RX.Accent else Color.Transparent, tween(250), label = "cd")
    val sc by animateFloatAsState(if (on) 1.03f else 1f, tween(250), label = "csc")
    Box(
        modifier = modifier
            .scale(sc)
            .background(bg, RoundedCornerShape(20.dp))
            .border(1.dp, border, RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Column {
            ArenaTile(name, 46)
            Spacer(Modifier.height(14.dp))
            Text(name, color = RX.Text, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
        }
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(24.dp)
                .background(dot, CircleShape)
                .border(1.dp, if (on) RX.Accent else RX.Line, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (on) Text("✓", color = RX.Bg, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
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
                .padding(horizontal = 24.dp)
        ) {
            Spacer(Modifier.height(20.dp))
            if (onBack != null) {
                Text(
                    "←  Back",
                    color = RX.Muted, fontSize = 15.sp,
                    modifier = Modifier.clickable { onBack() }.padding(vertical = 8.dp)
                )
                Spacer(Modifier.height(8.dp))
            }
            Text(
                if (onBack == null) "CHOOSE YOUR AREAS" else "ADD AREAS",
                color = RX.Muted, fontSize = 13.sp, letterSpacing = 4.sp
            )
            Spacer(Modifier.height(10.dp))
            Text(
                title,
                color = RX.Text, fontSize = 30.sp, fontWeight = FontWeight.Bold, lineHeight = 36.sp
            )
            Spacer(Modifier.height(24.dp))
            if (options.isEmpty()) {
                Text("You've added every arena.", color = RX.Muted, fontSize = 16.sp)
            }
            options.chunked(2).forEach { pair ->
                Row(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                    pair.forEachIndexed { i, name ->
                        if (i > 0) Spacer(Modifier.width(12.dp))
                        ArenaChoice(name, name in selected, Modifier.weight(1f)) {
                            selected = if (name in selected) selected - name else selected + name
                        }
                    }
                    if (pair.size == 1) {
                        Spacer(Modifier.width(12.dp))
                        Spacer(Modifier.weight(1f))
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
        }
        Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
            Text(
                if (ready) "${selected.size} selected" else "Select at least one area to continue.",
                color = RX.Muted, fontSize = 13.sp
            )
            Spacer(Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(
                        if (ready) Modifier.background(RX.Button, RoundedCornerShape(50))
                        else Modifier.border(1.dp, RX.Line, RoundedCornerShape(50))
                    )
                    .clickable(enabled = ready) { onDone(selected) }
                    .padding(vertical = 18.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    buttonLabel,
                    color = if (ready) Color.White else RX.Muted,
                    fontWeight = FontWeight.Bold, letterSpacing = 3.sp
                )
            }
        }
    }
}

@Composable
fun ArenaCard(
    area: String,
    store: Store,
    tick: Int,
    editing: Boolean,
    canRemove: Boolean,
    onClick: () -> Unit
) {
    val xp = remember(tick) { store.xp(area) }
    val live = remember(tick) { store.running()?.area == area }
    val info = Levels.of(xp)
    val p = animatedProgress(info.into.toFloat() / info.need, 900)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .background(RX.Panel, RoundedCornerShape(20.dp))
            .border(1.dp, if (live) RX.Accent else RX.Line, RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ArenaTile(area, 52)
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(area, color = RX.Text, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                    Text(
                        if (live) "●  IN SESSION" else Ranks.title(info.level),
                        color = if (live) RX.Accent else RX.Muted,
                        fontSize = 12.sp, letterSpacing = 1.sp
                    )
                }
                if (editing && canRemove) {
                    Text("REMOVE", color = Danger, fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                } else {
                    Text("LV ${info.level}", color = RX.Accent, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(10.dp))
            Bar(p, 5)
            Spacer(Modifier.height(6.dp))
            Text(
                "${info.into} / ${info.need} XP",
                color = RX.Muted, fontSize = 12.sp,
                modifier = Modifier.align(Alignment.End)
            )
        }
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RX.Page)
            .systemBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(20.dp))
        Text("REAL XP", color = RX.Text, fontSize = 15.sp, letterSpacing = 6.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(28.dp))
        Box(contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.size(240.dp)) {
                val stroke = 10.dp.toPx()
                val topLeft = Offset(stroke / 2, stroke / 2)
                val arcSize = Size(size.width - stroke, size.height - stroke)
                drawArc(
                    color = RX.Line, startAngle = -90f, sweepAngle = 360f, useCenter = false,
                    topLeft = topLeft, size = arcSize,
                    style = Stroke(stroke, cap = StrokeCap.Round)
                )
                drawArc(
                    brush = Brush.linearGradient(listOf(RX.Blue, RX.Accent)),
                    startAngle = -90f, sweepAngle = 360f * p, useCenter = false,
                    topLeft = topLeft, size = arcSize,
                    style = Stroke(stroke, cap = StrokeCap.Round)
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("LEVEL", color = RX.Muted, fontSize = 13.sp, letterSpacing = 4.sp)
                Text("${info.level}", color = RX.Text, fontSize = 76.sp, fontWeight = FontWeight.Bold)
                Text(
                    Ranks.title(info.level).uppercase(),
                    color = RX.Accent, fontSize = 12.sp, letterSpacing = 3.sp, fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text("${info.into} / ${info.need} XP", color = RX.Muted, fontSize = 13.sp)
            }
        }
        Spacer(Modifier.height(14.dp))
        Text("$invested invested", color = RX.Muted, fontSize = 14.sp)
        Spacer(Modifier.height(30.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("YOUR ARENAS", color = RX.Muted, fontSize = 13.sp, letterSpacing = 4.sp)
            if (chosen.size > 1) {
                Text(
                    if (editing) "DONE" else "EDIT",
                    color = RX.Accent, fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp,
                    modifier = Modifier.clickable { editing = !editing }.padding(vertical = 6.dp)
                )
            }
        }
        Spacer(Modifier.height(12.dp))
        chosen.forEach { name ->
            ArenaCard(name, store, tick, editing, chosen.size > 1) {
                if (editing) {
                    if (chosen.size > 1) toRemove = name
                } else {
                    onOpen(name)
                }
            }
        }
        if (chosen.size < Areas.all.size) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, RX.Line, RoundedCornerShape(20.dp))
                    .clickable { onAdd() }
                    .padding(vertical = 18.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("+  Add arena", color = RX.Accent, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }
        Spacer(Modifier.height(32.dp))
    }
}
