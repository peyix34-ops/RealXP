package com.realxp.app.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Bedtime
import androidx.compose.material.icons.outlined.BusinessCenter
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.DesignServices
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.DirectionsRun
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Flight
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.Paid
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.PedalBike
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.Pool
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.Science
import androidx.compose.material.icons.outlined.SelfImprovement
import androidx.compose.material.icons.outlined.Sms
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material.icons.outlined.SportsEsports
import androidx.compose.material.icons.outlined.Translate
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.realxp.app.data.Levels
import com.realxp.app.data.Ranks
import com.realxp.app.data.Store

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
    "Languages" -> Icons.Outlined.Translate
    "Reading" -> Icons.Outlined.MenuBook
    "History" -> Icons.Outlined.AccountBalance
    "Math" -> Icons.Outlined.Calculate
    "Science" -> Icons.Outlined.Science
    "Writing" -> Icons.Outlined.Edit
    "Coding" -> Icons.Outlined.Code
    "AI" -> Icons.Outlined.Psychology
    "Design" -> Icons.Outlined.DesignServices
    "Photography" -> Icons.Outlined.PhotoCamera
    "Art" -> Icons.Outlined.Palette
    "Music" -> Icons.Outlined.MusicNote
    "Speaking" -> Icons.Outlined.Mic
    "Cooking" -> Icons.Outlined.Restaurant
    "Driving" -> Icons.Outlined.DirectionsCar
    "Gardening" -> Icons.Outlined.Eco
    "Fitness" -> Icons.Outlined.FitnessCenter
    "Running" -> Icons.Outlined.DirectionsRun
    "Swimming" -> Icons.Outlined.Pool
    "Cycling" -> Icons.Outlined.PedalBike
    "Yoga" -> Icons.Outlined.SelfImprovement
    "Health" -> Icons.Outlined.Favorite
    "Finance" -> Icons.Outlined.Paid
    "Business" -> Icons.Outlined.BusinessCenter
    "Investing" -> Icons.Outlined.TrendingUp
    "Meditation" -> Icons.Outlined.Spa
    "Sleep" -> Icons.Outlined.Bedtime
    "Travel" -> Icons.Outlined.Flight
    "Gaming" -> Icons.Outlined.SportsEsports
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
        Text("${T("LV")} $level", color = tone, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun ArenaChoice(name: String, on: Boolean, modifier: Modifier, onClick: () -> Unit) {
    val border by animateColorAsState(if (on) RX.Accent else RX.Line, tween(200), label = "cb")
    val bg by animateColorAsState(
        if (on) RX.Accent.copy(alpha = 0.10f) else RX.Panel, tween(200), label = "cbg"
    )
    val sc by animateFloatAsState(
        if (on) 1.05f else 1f,
        spring(dampingRatio = 0.5f, stiffness = 500f),
        label = "csc"
    )
    val chk by animateFloatAsState(
        if (on) 1f else 0f,
        spring(dampingRatio = 0.45f, stiffness = 600f),
        label = "chk"
    )
    Box(
        modifier = modifier
            .height(88.dp)
            .scale(sc)
            .background(bg, RoundedCornerShape(14.dp))
            .border(1.dp, border, RoundedCornerShape(14.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            ArenaIcon(name, 26)
            Spacer(Modifier.height(8.dp))
            Text(
                A(name),
                color = if (on) RX.Accent else RX.Text,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 6.dp)
            )
        }
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(6.dp)
                .size(16.dp)
                .scale(chk)
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

@Composable
fun ArenaPicker(
    options: List<String>,
    title: String,
    buttonLabel: String,
    onBack: (() -> Unit)?,
    onDone: (List<String>) -> Unit
) {
    var selected by remember { mutableStateOf(listOf<String>()) }
    var cat by remember { mutableStateOf("All") }
    val ready = selected.isNotEmpty()
    val shownOpts = if (cat == "All") options else options.filter { Catalog.categoryOf(it) == cat }

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
                    T("←  Back"),
                    color = RX.Muted, fontSize = 14.sp,
                    modifier = Modifier.clickable { onBack() }.padding(vertical = 8.dp)
                )
                Spacer(Modifier.height(4.dp))
            }
            Text(
                if (onBack == null) T("CHOOSE YOUR AREAS") else T("ADD AREAS"),
                color = RX.Accent, fontSize = 11.sp, letterSpacing = 3.sp, fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            Text(
                title,
                color = RX.Text, fontSize = 24.sp, fontWeight = FontWeight.Bold, lineHeight = 30.sp
            )
            Spacer(Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())) {
                Catalog.categories.forEach { c ->
                    MonthChip(T(c), c == cat) { cat = c }
                    Spacer(Modifier.width(8.dp))
                }
            }
            Spacer(Modifier.height(18.dp))
            if (options.isEmpty()) {
                Text(T("You've added every arena."), color = RX.Muted, fontSize = 15.sp)
            } else if (shownOpts.isEmpty()) {
                Text(T("Nothing left in this category."), color = RX.Muted, fontSize = 15.sp)
            }
            key(cat) {
                Column {
                    shownOpts.chunked(3).forEachIndexed { r, row ->
                        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)) {
                            row.forEachIndexed { i, name ->
                                if (i > 0) Spacer(Modifier.width(10.dp))
                                ArenaChoice(
                                    name, name in selected,
                                    Modifier.weight(1f).staggerIn(minOf(r * 3 + i, 12), 35)
                                ) {
                                    selected = if (name in selected) selected - name else selected + name
                                }
                            }
                            repeat(3 - row.size) {
                                Spacer(Modifier.width(10.dp))
                                Spacer(Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
        }
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp)) {
            Text(
                if (ready) TF("%d selected", selected.size) else T("Select at least one area to continue."),
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
                A(area), info.level, 14.sp,
                Modifier.weight(1f), FontWeight.SemiBold,
                maxLines = 1, overflow = TextOverflow.Ellipsis
            )
        }
        Spacer(Modifier.height(2.dp))
        Text(
            T(Ranks.title(info.level)),
            color = if (info.level >= 10) tone else RX.Muted,
            fontSize = 11.sp
        )
        Spacer(Modifier.weight(1f))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("${T("LV")} ${info.level}", color = tone, fontSize = 18.sp, fontWeight = FontWeight.Bold)
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
        Text(T("Add Arena"), color = RX.Muted, fontSize = 12.sp)
    }
}
