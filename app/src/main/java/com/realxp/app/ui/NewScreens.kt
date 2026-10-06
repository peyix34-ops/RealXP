package com.realxp.app.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
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
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.Paid
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Science
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Sms
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.realxp.app.data.Areas
import com.realxp.app.data.Levels
import com.realxp.app.data.Ranks
import com.realxp.app.data.Store
import com.realxp.app.data.formatTime

val Danger = Color(0xFFFF6B6B)

object RankColors {
    val normal: Color
        get() = RX.Text
    val bronze: Color
        get() = if (RX.dark) Color(0xFFD9904F) else Color(0xFFB8691F)
    val silver: Color
        get() = if (RX.dark) Color(0xFFC9D3E6) else Color(0xFF8593AF)
    val gold: Color
        get() = if (RX.dark) Color(0xFFFFC93C) else Color(0xFFD99700)
    val diamond: Color
        get() = if (RX.dark) Color(0xFF7DF0FF) else Color(0xFF00B4E6)

    private val starts = listOf(1, 10, 20, 30, 40)

    fun forLevel(level: Int): Color {
        val colors = listOf(normal, bronze, silver, gold, diamond)
        var idx = 0
        for (i in starts.indices) {
            if (level >= starts[i]) idx = i
        }
        if (idx >= starts.size - 1) return diamond
        val span = (starts[idx + 1] - starts[idx]).toFloat()
        val f = ((level - starts[idx]) / span) * 0.5f
        return lerp(colors[idx], colors[idx + 1], f)
    }

    fun tone(level: Int): Color = if (level >= 10) forLevel(level) else RX.Accent
}

@Composable
private fun ShimmerText(
    text: String,
    fontSize: TextUnit,
    modifier: Modifier,
    fontWeight: FontWeight,
    letterSpacing: TextUnit,
    maxLines: Int,
    overflow: TextOverflow
) {
    val shift by rememberInfiniteTransition(label = "shimmer").animateFloat(
        initialValue = 0f,
        targetValue = 700f,
        animationSpec = infiniteRepeatable(tween(2600, easing = LinearEasing)),
        label = "shimmerValue"
    )
    val brush = Brush.linearGradient(
        colors = listOf(
            RankColors.diamond,
            if (RX.dark) Color.White else RX.Blue,
            RankColors.diamond
        ),
        start = Offset(shift - 350f, 0f),
        end = Offset(shift, 0f)
    )
    Text(
        text,
        modifier = modifier,
        maxLines = maxLines,
        overflow = overflow,
        style = TextStyle(
            brush = brush,
            fontSize = fontSize,
            fontWeight = fontWeight,
            letterSpacing = letterSpacing
        )
    )
}

@Composable
fun RankText(
    text: String,
    level: Int,
    fontSize: TextUnit,
    modifier: Modifier = Modifier,
    fontWeight: FontWeight = FontWeight.Normal,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip
) {
    if (level >= 50) {
        ShimmerText(text, fontSize, modifier, fontWeight, letterSpacing, maxLines, overflow)
    } else {
        Text(
            text,
            color = RankColors.forLevel(level),
            fontSize = fontSize,
            fontWeight = fontWeight,
            letterSpacing = letterSpacing,
            maxLines = maxLines,
            overflow = overflow,
            modifier = modifier
        )
    }
}

fun arenaIcon(name: String): ImageVector = when (name) {
    "English" -> Icons.Outlined.Sms
    "Fitness" -> Icons.Outlined.FitnessCenter
    "Reading" -> Icons.Outlined.MenuBook
    "History" -> Icons.Outlined.AccountBalance
    "Coding" -> Icons.Outlined.Code
    "Math" -> Icons.Outlined.Calculate
    "Writing" -> Icons.Outlined.Edit
    "Music" -> Icons.Outlined.MusicNote
    "Finance" -> Icons.Outlined.Paid
    "Science" -> Icons.Outlined.Science
    "Art" -> Icons.Outlined.Palette
    "Meditation" -> Icons.Outlined.Spa
    else -> Icons.Outlined.Sms
}

@Composable
fun ArenaIcon(name: String, size: Int, tint: Color = RX.Accent) {
    Icon(
        imageVector = arenaIcon(name),
        contentDescription = null,
        tint = tint,
        modifier = Modifier.size(size.dp)
    )
}

@Composable
fun ArenaBadge(name: String, size: Int) {
    Box(
        modifier = Modifier
            .size(size.dp)
            .background(RX.Accent.copy(alpha = 0.10f), RoundedCornerShape((size / 3).dp)),
        contentAlignment = Alignment.Center
    ) {
        ArenaIcon(name, (size * 0.55f).toInt())
    }
}

@Composable
fun LevelPill(level: Int) {
    val tone = RankColors.tone(level)
    Box(
        modifier = Modifier
            .background(tone.copy(alpha = 0.12f), RoundedCornerShape(50))
            .padding(horizontal = 12.dp, vertical = 5.dp)
    ) {
        Text("LV $level", color = tone, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun ArenaChoice(name: String, on: Boolean, modifier: Modifier, onClick: () -> Unit) {
    val border by animateColorAsState(if (on) RX.Accent else RX.Line, tween(200), label = "cb")
    val bg by animateColorAsState(
        if (on) RX.Accent.copy(alpha = 0.10f) else RX.Panel, tween(200), label = "cbg"
    )
    Box(
        modifier = modifier
            .height(88.dp)
            .background(bg, RoundedCornerShape(14.dp))
            .border(1.dp, border, RoundedCornerShape(14.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            ArenaIcon(name, 26)
            Spacer(Modifier.height(8.dp))
            Text(
                name,
                color = if (on) RX.Accent else RX.Text,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 6.dp)
            )
        }
        if (on) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp)
                    .size(16.dp)
                    .background(RX.Accent, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.Check, contentDescription = null,
                    tint = Color.White, modifier = Modifier.size(11.dp)
                )
            }
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
                color = RX.Accent, fontSize = 11.sp, letterSpacing = 3.sp, fontWeight = FontWeight.Bold
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
                    .padding(vertical = 15.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "$buttonLabel  →",
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
    val info = Levels.of(xp)
    val tone = RankColors.tone(info.level)
    val p = animatedProgress(info.into.toFloat() / info.need, 900)
    Column(
        modifier = modifier
            .height(108.dp)
            .background(RX.Panel, RoundedCornerShape(16.dp))
            .border(
                1.dp,
                if (info.level >= 10) tone.copy(alpha = 0.5f) else RX.Line,
                RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            ArenaIcon(area, 20)
            Spacer(Modifier.width(8.dp))
            RankText(
                area, info.level, 14.sp,
                Modifier.weight(1f), FontWeight.SemiBold,
                maxLines = 1, overflow = TextOverflow.Ellipsis
            )
        }
        Spacer(Modifier.height(2.dp))
        Text(
            Ranks.title(info.level),
            color = if (info.level >= 10) tone else RX.Muted,
            fontSize = 11.sp
        )
        Spacer(Modifier.weight(1f))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("LV ${info.level}", color = tone, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            if (editing && canRemove) {
                Icon(Icons.Outlined.Close, null, tint = Danger, modifier = Modifier.size(18.dp))
            } else {
                Icon(Icons.Outlined.ChevronRight, null, tint = RX.Muted, modifier = Modifier.size(18.dp))
            }
        }
        Spacer(Modifier.height(6.dp))
        Bar(p, 3, tone)
    }
}

@Composable
fun AddCell(modifier: Modifier, onClick: () -> Unit) {
    Column(
        modifier = modifier
            .height(108.dp)
            .background(RX.Accent.copy(alpha = 0.06f), RoundedCornerShape(16.dp))
            .border(1.dp, RX.Line, RoundedCornerShape(16.dp))
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Outlined.Add, null, tint = RX.Accent, modifier = Modifier.size(24.dp))
        Spacer(Modifier.height(6.dp))
        Text("Add Arena", color = RX.Muted, fontSize = 12.sp)
    }
}

@Composable
fun LifeHome(
    store: Store,
    chosen: List<String>,
    tick: Int,
    onOpen: (String) -> Unit,
    onAdd: () -> Unit,
    onRemove: (String) -> Unit,
    onToggleDark: () -> Unit
) {
    val total = remember(tick) { store.totalXp() }
    val invested = remember(tick) { formatTime(store.allSeconds()) }
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
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(RX.Panel, CircleShape)
                    .border(1.dp, RX.Line, CircleShape)
                    .clickable { showSettings = true },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.Settings, null, tint = RX.Muted, modifier = Modifier.size(20.dp))
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
        Spacer(Modifier.height(24.dp))
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
