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
import androidx.compose.material3.Text
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
import com.realxp.app.data.Store

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
fun ArenaPicker(onDone: (List<String>) -> Unit) {
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
            Spacer(Modifier.height(28.dp))
            Text("CHOOSE YOUR AREAS", color = RX.Muted, fontSize = 13.sp, letterSpacing = 4.sp)
            Spacer(Modifier.height(10.dp))
            Text(
                "Pick everything\nyou want to level up.",
                color = RX.Text, fontSize = 30.sp, fontWeight = FontWeight.Bold, lineHeight = 36.sp
            )
            Spacer(Modifier.height(24.dp))
            Areas.all.chunked(2).forEach { pair ->
                Row(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                    pair.forEachIndexed { i, name ->
                        if (i > 0) Spacer(Modifier.width(12.dp))
                        ArenaChoice(name, name in selected, Modifier.weight(1f)) {
                            selected = if (name in selected) selected - name else selected + name
                        }
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
                    "CONTINUE",
                    color = if (ready) Color.White else RX.Muted,
                    fontWeight = FontWeight.Bold, letterSpacing = 3.sp
                )
            }
        }
    }
}

@Composable
fun ArenaCard(area: String, store: Store, tick: Int, onClick: () -> Unit) {
    val xp = remember(tick) { store.xp(area) }
    val info = Levels.of(xp)
    val p = animatedProgress(info.into.toFloat() / info.need, 900)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .background(RX.Panel, RoundedCornerShape(20.dp))
            .border(1.dp, RX.Line, RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ArenaTile(area, 52)
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(area, color = RX.Text, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                Text("LV ${info.level}", color = RX.Accent, fontSize = 16.sp, fontWeight = FontWeight.Bold)
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
fun LifeHome(store: Store, chosen: List<String>, tick: Int, onOpen: (String) -> Unit) {
    val total = remember(tick) { store.totalXp() }
    val info = Levels.overall(total)
    val p = animatedProgress(info.into.toFloat() / info.need, 1400)

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
                Text("${info.into} / ${info.need} XP", color = RX.Muted, fontSize = 13.sp)
            }
        }
        Spacer(Modifier.height(36.dp))
        Text(
            "YOUR ARENAS",
            color = RX.Muted, fontSize = 13.sp, letterSpacing = 4.sp,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(Modifier.height(12.dp))
        chosen.forEach { name -> ArenaCard(name, store, tick) { onOpen(name) } }
        Spacer(Modifier.height(24.dp))
    }
}
