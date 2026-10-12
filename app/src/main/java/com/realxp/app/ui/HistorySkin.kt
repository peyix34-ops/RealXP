package com.realxp.app.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.realxp.app.data.DayStat
import com.realxp.app.data.Levels
import com.realxp.app.data.Ranks
import com.realxp.app.data.Store
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin

private val HBg: Color
    get() = if (RX.dark) Color(0xFF1B140C) else Color(0xFFF4E7C8)
private val HBg2: Color
    get() = if (RX.dark) Color(0xFF241A10) else Color(0xFFEBD7AB)
private val HPanel: Color
    get() = if (RX.dark) Color(0xFF2A1F13) else Color(0xFFFBF3DE)
private val HLine: Color
    get() = if (RX.dark) Color(0xFF4A3820) else Color(0xFFD3B67E)
private val HInk: Color
    get() = if (RX.dark) Color(0xFFF0E1C0) else Color(0xFF4A3219)
private val HMuted: Color
    get() = if (RX.dark) Color(0xFFA89170) else Color(0xFF8A6B43)
private val HBronze = Color(0xFFC8862B)
private val HBronzeT: Color
    get() = if (RX.dark) Color(0xFFD9993A) else Color(0xFF9A5F12)
private val HGold: Color
    get() = if (RX.dark) Color(0xFFE6BD4A) else Color(0xFFB8860B)
private val HSeal = Color(0xFFA82E27)
private val HSealT: Color
    get() = if (RX.dark) Color(0xFFE8715F) else Color(0xFFA82E27)
private val SealInk = Color(0xFFF4E7C8)
private val HSerif = FontFamily.Serif

private fun eraName(level: Int): String = if (Lang.tr) {
    when {
        level < 3 -> "Tarih Öncesi"
        level < 6 -> "Antik Çağ"
        level < 10 -> "Ortaçağ"
        level < 20 -> "Rönesans"
        level < 30 -> "Sanayi Çağı"
        level < 40 -> "Modern Çağ"
        else -> "Efsaneler Çağı"
    }
} else {
    when {
        level < 3 -> "Prehistory"
        level < 6 -> "Ancient Age"
        level < 10 -> "Middle Ages"
        level < 20 -> "Renaissance"
        level < 30 -> "Industrial Age"
        level < 40 -> "Modern Age"
        else -> "Age of Legends"
    }
}

@Composable
private fun S(
    text: String,
    color: Color,
    size: Int,
    bold: Boolean = false,
    modifier: Modifier = Modifier,
    spacing: TextUnit = TextUnit.Unspecified,
    italic: Boolean = false
) {
    Text(
        text,
        color = color,
        fontSize = size.sp,
        fontFamily = HSerif,
        fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
        fontStyle = if (italic) FontStyle.Italic else FontStyle.Normal,
        letterSpacing = spacing,
        modifier = modifier
    )
}

@Composable
private fun Compass(boxSize: Dp, color: Color, modifier: Modifier = Modifier) {
    val t by rememberInfiniteTransition(label = "compass").animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(60000, easing = LinearEasing)),
        label = "compassT"
    )
    Canvas(modifier = modifier.size(boxSize)) {
        val c = Offset(size.width / 2f, size.height / 2f)
        val r = size.minDimension / 2f * 0.9f
        drawCircle(color, r, c, style = Stroke(1.dp.toPx()))
        drawCircle(color, r * 0.72f, c, style = Stroke(1.dp.toPx()))
        rotate(t, c) {
            for (k in 0 until 8) {
                val len = if (k % 2 == 0) r else r * 0.62f
                rotate(k * 45f, c) {
                    val p = Path().apply {
                        moveTo(c.x, c.y - len)
                        lineTo(c.x - r * 0.11f, c.y)
                        lineTo(c.x + r * 0.11f, c.y)
                        close()
                    }
                    drawPath(p, color)
                }
            }
        }
    }
}

@Composable
private fun ScrollIcon(boxW: Dp, boxH: Dp, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(boxW, boxH)) {
        val w = size.width
        val h = size.height
        val body = Color(0xFFF3E2BC)
        val roll = Color(0xFFD9B873)
        val ink = Color(0xFF6B4A22)
        drawRoundRect(body, Offset(w * 0.12f, h * 0.12f), Size(w * 0.76f, h * 0.76f), CornerRadius(2.dp.toPx()))
        drawRoundRect(
            ink.copy(alpha = 0.5f), Offset(w * 0.12f, h * 0.12f), Size(w * 0.76f, h * 0.76f),
            CornerRadius(2.dp.toPx()), style = Stroke(1f)
        )
        drawRoundRect(roll, Offset(0f, 0f), Size(w, h * 0.2f), CornerRadius(h * 0.1f))
        drawRoundRect(roll, Offset(0f, h * 0.8f), Size(w, h * 0.2f), CornerRadius(h * 0.1f))
        for (i in 0..2) {
            val y = h * (0.36f + i * 0.14f)
            drawLine(ink.copy(alpha = 0.6f), Offset(w * 0.25f, y), Offset(w * (0.75f - (i % 2) * 0.15f), y), 1.5f)
        }
    }
}

@Composable
private fun WaxSeal(boxSize: Dp, modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) {
    Box(modifier = modifier.size(boxSize), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val c = Offset(size.width / 2f, size.height / 2f)
            val r = size.minDimension / 2f
            for (i in 0 until 14) {
                val a = i * (2.0 * Math.PI / 14.0)
                drawCircle(
                    HSeal, r * 0.16f,
                    Offset(c.x + (r * 0.82f * cos(a)).toFloat(), c.y + (r * 0.82f * sin(a)).toFloat())
                )
            }
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(Color(0xFFD0453B), HSeal, Color(0xFF7A1E19)),
                    center = Offset(c.x - r * 0.2f, c.y - r * 0.25f),
                    radius = r
                ),
                radius = r * 0.84f,
                center = c
            )
            drawCircle(Color(0x33FFFFFF), r * 0.62f, c, style = Stroke(1.5.dp.toPx()))
        }
        content()
    }
}

@Composable
private fun JourneyBar(p: Float) {
    val pulse by rememberInfiniteTransition(label = "journey").animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1100, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "journeyT"
    )
    Canvas(modifier = Modifier.fillMaxWidth().height(34.dp)) {
        val y = size.height * 0.6f
        val pad = 8.dp.toPx()
        val w = size.width - 2 * pad
        drawLine(HLine, Offset(pad, y), Offset(pad + w, y), 5.dp.toPx(), StrokeCap.Round)
        if (p > 0.01f) {
            drawLine(
                brush = Brush.horizontalGradient(listOf(HBronze, HGold)),
                start = Offset(pad, y), end = Offset(pad + w * p, y),
                strokeWidth = 5.dp.toPx(), cap = StrokeCap.Round
            )
        }
        for (i in 0..4) {
            val x = pad + w * i / 4f
            val passed = p >= i / 4f - 0.001f
            drawCircle(if (passed) HGold else HLine, 6.dp.toPx(), Offset(x, y))
            drawCircle(HBg, 2.5.dp.toPx(), Offset(x, y))
        }
        val mx = pad + w * p
        drawCircle(HGold.copy(alpha = 0.25f * pulse), 11.dp.toPx(), Offset(mx, y))
        val d = Path().apply {
            moveTo(mx, y - 12.dp.toPx())
            lineTo(mx + 6.dp.toPx(), y - 4.dp.toPx())
            lineTo(mx, y + 4.dp.toPx())
            lineTo(mx - 6.dp.toPx(), y - 4.dp.toPx())
            close()
        }
        drawPath(d, HGold)
        drawPath(d, HInk.copy(alpha = 0.5f), style = Stroke(1.dp.toPx()))
    }
}

@Composable
private fun MiniTimeline(p: Float) {
    Canvas(modifier = Modifier.fillMaxWidth().height(12.dp)) {
        val y = size.height / 2f
        drawLine(HLine, Offset(4.dp.toPx(), y), Offset(size.width - 4.dp.toPx(), y), 3.dp.toPx(), StrokeCap.Round)
        val w = size.width - 8.dp.toPx()
        if (p > 0.01f) {
            drawLine(
                HBronze, Offset(4.dp.toPx(), y), Offset(4.dp.toPx() + w * p, y),
                3.dp.toPx(), StrokeCap.Round
            )
        }
        for (i in 0..4) {
            val x = 4.dp.toPx() + w * i / 4f
            drawCircle(if (p >= i / 4f - 0.001f) HGold else HLine, 3.dp.toPx(), Offset(x, y))
        }
    }
}

@Composable
private fun TabletButton(m: Int, chip: String, enabled: Boolean, modifier: Modifier, onClick: () -> Unit) {
    val outer = RoundedCornerShape(12.dp)
    val inner = RoundedCornerShape(9.dp)
    Column(
        modifier = modifier
            .alpha(if (enabled) 1f else 0.5f)
            .then(if (enabled) Modifier.bounceClick { onClick() } else Modifier)
            .background(Brush.verticalGradient(listOf(HPanel, HBg2)), outer)
            .border(1.5.dp, HBronze.copy(alpha = 0.75f), outer)
            .padding(4.dp)
            .border(0.5.dp, HLine, inner)
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        S("$m${T("m")}", HInk, 19, true)
        Spacer(Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(9.dp).background(HSeal, CircleShape))
            Spacer(Modifier.width(6.dp))
            S(chip, HSealT, 13, true)
        }
    }
}

@Composable
private fun HSection(title: String, trailing: String?, content: @Composable ColumnScope.() -> Unit) {
    val shape = RoundedCornerShape(16.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(HPanel, shape)
            .border(1.dp, HLine, shape)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            S(title, HMuted, 11, true, spacing = 2.sp)
            if (trailing != null) S(trailing, HInk, 13, true)
        }
        Spacer(Modifier.height(12.dp))
        content()
    }
}

@Composable
private fun PillarChart(days: List<DayStat>, labels: List<String>) {
    val maxM = maxOf(60, days.maxOf { it.minutes })
    val fracs = days.map { animatedProgress(it.minutes.toFloat() / maxM, 900) }
    Column {
        Canvas(modifier = Modifier.fillMaxWidth().height(110.dp)) {
            val n = days.size
            val slot = size.width / n
            val pw = slot * 0.5f
            val base = size.height
            for (i in 0 until n) {
                val x = i * slot + (slot - pw) / 2f
                val h = 16.dp.toPx() + (size.height - 22.dp.toPx()) * fracs[i]
                val top = base - h
                val shaft = when {
                    days[i].minutes == 0 -> HLine.copy(alpha = 0.6f)
                    days[i].isToday -> HBronze
                    else -> HBronze.copy(alpha = 0.55f)
                }
                drawRect(HInk.copy(alpha = 0.30f), Offset(x - 3.dp.toPx(), base - 5.dp.toPx()), Size(pw + 6.dp.toPx(), 5.dp.toPx()))
                drawRect(shaft, Offset(x, top + 7.dp.toPx()), Size(pw, h - 12.dp.toPx()))
                drawLine(HInk.copy(alpha = 0.22f), Offset(x + pw * 0.33f, top + 9.dp.toPx()), Offset(x + pw * 0.33f, base - 7.dp.toPx()), 1f)
                drawLine(HInk.copy(alpha = 0.22f), Offset(x + pw * 0.66f, top + 9.dp.toPx()), Offset(x + pw * 0.66f, base - 7.dp.toPx()), 1f)
                drawRect(shaft, Offset(x - 3.dp.toPx(), top), Size(pw + 6.dp.toPx(), 7.dp.toPx()))
                drawRect(HInk.copy(alpha = 0.18f), Offset(x - 3.dp.toPx(), top + 6.dp.toPx()), Size(pw + 6.dp.toPx(), 1.5f))
            }
        }
        Spacer(Modifier.height(4.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            labels.forEachIndexed { i, l ->
                S(
                    l,
                    if (days[i].isToday) HBronzeT else HMuted, 11, days[i].isToday,
                    Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun SandHeat(cols: List<List<Int?>>) {
    val weeks = cols.size
    val gap = 3.dp
    val prog = animatedProgress(1f, 1500)
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val cell = (maxWidth - gap * (weeks - 1)) / weeks
        val h = cell * 7 + gap * 6
        Canvas(modifier = Modifier.fillMaxWidth().height(h)) {
            val c = cell.toPx()
            val g = gap.toPx()
            for (w in 0 until weeks) {
                for (r in 0 until 7) {
                    val m = cols[w][r] ?: continue
                    val t = (prog * (weeks + 5) - w).coerceIn(0f, 1f)
                    if (t <= 0f) continue
                    val col = when (heatLevel(m)) {
                        0 -> HLine.copy(alpha = 0.55f)
                        1 -> HBronze.copy(alpha = 0.30f)
                        2 -> HBronze.copy(alpha = 0.55f)
                        3 -> HBronze.copy(alpha = 0.85f)
                        else -> HGold
                    }
                    drawRoundRect(
                        color = col.copy(alpha = col.alpha * t),
                        topLeft = Offset(w * (c + g), r * (c + g)),
                        size = Size(c, c),
                        cornerRadius = CornerRadius(2.dp.toPx())
                    )
                }
            }
        }
    }
}

@Composable
private fun StampPop(text: String, id: Int, modifier: Modifier) {
    val sc = remember { Animatable(2.6f) }
    val a = remember { Animatable(0f) }
    LaunchedEffect(id) {
        if (id > 0) {
            sc.snapTo(2.6f)
            a.snapTo(1f)
            launch { sc.animateTo(1f, spring(dampingRatio = 0.4f, stiffness = 400f)) }
            delay(1100L)
            a.animateTo(0f, tween(450))
        }
    }
    if (id > 0 && a.value > 0.01f) {
        WaxSeal(116.dp, modifier.scale(sc.value).alpha(a.value)) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                S(text, SealInk, 22, true)
                S("XP", SealInk, 11, true, spacing = 2.sp)
            }
        }
    }
}

object HistorySkin : ArenaSkin {

    @Composable
    override fun PickerTile(name: String, on: Boolean, modifier: Modifier, onClick: () -> Unit) {
        val border by animateColorAsState(if (on) HBronze else HLine, tween(200), label = "hb")
        val sc by animateFloatAsState(
            if (on) 1.05f else 1f,
            spring(dampingRatio = 0.5f, stiffness = 500f),
            label = "hsc"
        )
        val shape = RoundedCornerShape(14.dp)
        Box(
            modifier = modifier
                .height(88.dp)
                .scale(sc)
                .clip(shape)
                .background(Brush.verticalGradient(listOf(HPanel, HBg2)))
                .border(1.5.dp, border, shape)
                .clickable { onClick() }
        ) {
            Compass(70.dp, HBronze.copy(alpha = 0.22f), Modifier.align(Alignment.Center))
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ScrollIcon(26.dp, 32.dp)
                Spacer(Modifier.height(6.dp))
                S(A(name), HInk, 13, true)
            }
            if (on) {
                PickCheck(HSeal, SealInk, Modifier.align(Alignment.TopEnd).padding(6.dp))
            }
        }
    }

    @Composable
    override fun HomeCard(
        name: String,
        store: Store,
        tick: Int,
        editing: Boolean,
        canRemove: Boolean,
        modifier: Modifier,
        onClick: () -> Unit
    ) {
        val xp = remember(tick) { store.xp(name) }
        val info = Levels.of(xp)
        val p = animatedProgress(info.into.toFloat() / info.need, 900)
        val shape = RoundedCornerShape(18.dp)
        Box(
            modifier = modifier
                .height(126.dp)
                .clip(shape)
                .background(Brush.verticalGradient(listOf(HPanel, HBg2)))
                .border(
                    1.5.dp,
                    if (info.level >= 10) HGold.copy(alpha = 0.8f) else HLine,
                    shape
                )
                .clickable { onClick() }
        ) {
            Compass(
                78.dp, HBronze.copy(alpha = 0.18f),
                Modifier.align(Alignment.TopEnd).offset(x = 16.dp, y = (-12).dp)
            )
            ScrollIcon(22.dp, 28.dp, Modifier.align(Alignment.TopStart).padding(12.dp))
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp)
                    .background(HBronze.copy(alpha = 0.18f), RoundedCornerShape(50))
                    .padding(horizontal = 9.dp, vertical = 3.dp)
            ) {
                if (editing && canRemove) {
                    Icon(Icons.Outlined.Close, null, tint = Danger, modifier = Modifier.size(12.dp))
                } else {
                    S("${T("LV")} ${info.level}", HBronzeT, 12, true)
                }
            }
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                S(A(name), HInk, 15, true)
                S(eraName(info.level), HBronzeT, 11, italic = true)
                Spacer(Modifier.height(6.dp))
                MiniTimeline(p)
            }
        }
    }

    @Composable
    override fun Screen(s: AreaState, onBack: () -> Unit) {
        val info = s.info
        val st = s.stats()
        val barP = animatedXpBar(info.level, info.into.toFloat() / info.need)
        val typed = s.input.toIntOrNull()
        val xpTodayShown = animatedCount(st.xpToday)
        val todayIdx = (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) + 5) % 7
        val weekLabels = (6 downTo 0).map { dayLetter(((todayIdx - it) % 7 + 7) % 7) }
        val fmt = remember(Lang.tr) {
            SimpleDateFormat("d MMM yyyy  ·  HH:mm", if (Lang.tr) Locale("tr", "TR") else Locale.US)
        }
        val dust = remember { Animatable(1f) }
        LaunchedEffect(s.popId) {
            if (s.popId > 0) {
                dust.snapTo(0f)
                dust.animateTo(1f, tween(1100, easing = FastOutSlowInEasing))
            }
        }

        HonestyHost(s, HPanel, HInk, HMuted, HBronze)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(HBg2, HBg)))
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawRect(
                    Brush.radialGradient(
                        listOf(Color.Transparent, HInk.copy(alpha = 0.10f)),
                        center = Offset(size.width / 2f, size.height / 2f),
                        radius = size.maxDimension * 0.75f
                    )
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(Modifier.height(10.dp))
                S(T("←  Back"), HMuted, 15, false, Modifier.clickable { onBack() }.padding(vertical = 8.dp))
                Spacer(Modifier.height(8.dp))

                val heroShape = RoundedCornerShape(20.dp)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(heroShape)
                        .background(Brush.verticalGradient(listOf(HPanel, HBg2)))
                        .border(2.dp, HBronze.copy(alpha = 0.75f), heroShape)
                ) {
                    Compass(
                        130.dp, HBronze.copy(alpha = 0.20f),
                        Modifier.align(Alignment.TopEnd).offset(x = 28.dp, y = (-26).dp)
                    )
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            WaxSeal(76.dp) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    S(T("LV"), SealInk, 9, true, spacing = 2.sp)
                                    S("${info.level}", SealInk, 26, true)
                                }
                            }
                            Spacer(Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                S(A(s.area), HInk, 26, true)
                                S(eraName(info.level), HBronzeT, 14, italic = true)
                                S(up(T(Ranks.title(info.level))), HMuted, 10, true, spacing = 2.sp)
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                        JourneyBar(barP)
                        Spacer(Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            S("${info.into} / ${info.need} XP", HMuted, 12)
                            S("${info.need - info.into} XP → ${T("LV")} ${info.level + 1}", HBronzeT, 12, true)
                        }
                    }
                }

                Spacer(Modifier.height(14.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    listOf(T("Overview"), if (Lang.tr) "Yıllıklar" else "Chronicle").forEachIndexed { i, label ->
                        val on = s.tab == i
                        Column(
                            modifier = Modifier.weight(1f).clickable { s.tab = i },
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            S(label, if (on) HBronzeT else HMuted, 15, on, Modifier.padding(vertical = 10.dp))
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .height(2.dp)
                                    .background(if (on) HBronze else HLine)
                            )
                        }
                    }
                }
                Spacer(Modifier.height(18.dp))

                if (s.tab == 0) {
                    S(T("LOG YOUR WORK"), HMuted, 11, true, spacing = 2.sp)
                    Spacer(Modifier.height(10.dp))
                    val mins = listOf(15, 30, 60, 90)
                    for (r in 0..1) {
                        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)) {
                            for (c in 0..1) {
                                val m = mins[r * 2 + c]
                                val ok = s.fits(m)
                                if (c > 0) Spacer(Modifier.width(10.dp))
                                TabletButton(
                                    m,
                                    if (ok) "+${s.gainFor(m)} XP" else "24${T("h")}",
                                    ok, Modifier.weight(1f)
                                ) { s.request(m) }
                            }
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(HPanel, RoundedCornerShape(10.dp))
                                .border(1.dp, HLine, RoundedCornerShape(10.dp))
                                .padding(horizontal = 14.dp)
                        ) {
                            BasicTextField(
                                value = s.input,
                                onValueChange = { v ->
                                    s.input = v.filter { it.isDigit() }.take(4)
                                    s.error = ""
                                },
                                singleLine = true,
                                textStyle = TextStyle(
                                    color = HInk, fontFamily = HSerif,
                                    fontSize = 18.sp, fontWeight = FontWeight.Bold
                                ),
                                cursorBrush = SolidColor(HBronze),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth().padding(vertical = 18.dp),
                                decorationBox = { inner ->
                                    Box {
                                        if (s.input.isEmpty()) S(T("Other (min)"), HMuted, 16, italic = true)
                                        inner()
                                    }
                                }
                            )
                        }
                        Spacer(Modifier.width(10.dp))
                        WaxSeal(62.dp, Modifier.bounceClick { s.submitInput() }) {
                            S(if (Lang.tr) "MÜHÜR" else "SEAL", SealInk, 10, true, spacing = 1.sp)
                        }
                    }
                    Spacer(Modifier.height(6.dp))
                    S(
                        when {
                            s.error.isNotEmpty() -> s.error
                            typed != null && typed >= 1 && s.fits(typed) ->
                                TF("+%d XP  ·  %d min", s.gainFor(typed), typed)
                            typed != null && typed >= 1 -> T("A day only has 24 hours.")
                            else -> T("Type any amount, for example 5.")
                        },
                        if (s.error.isNotEmpty()) Danger else HMuted, 12, italic = true
                    )

                    Spacer(Modifier.height(20.dp))
                    HSection(T("TODAY'S XP"), null) {
                        Row(verticalAlignment = Alignment.Bottom) {
                            S("+$xpTodayShown", HGold, 40, true)
                            Spacer(Modifier.width(6.dp))
                            S("XP", HMuted, 14, true, Modifier.padding(bottom = 8.dp))
                        }
                        Spacer(Modifier.height(12.dp))
                        Box(Modifier.fillMaxWidth().height(1.dp).background(HLine))
                        Spacer(Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Column(Modifier.weight(1f)) {
                                S(T("TODAY"), HMuted, 10, true, spacing = 1.5.sp)
                                S(ftime(st.todaySec), HInk, 16, true)
                            }
                            Column(Modifier.weight(1f)) {
                                S(T("TOTAL"), HMuted, 10, true, spacing = 1.5.sp)
                                S(ftime(st.totalSec), HInk, 16, true)
                            }
                            Column(Modifier.weight(1f)) {
                                S(T("LOGS"), HMuted, 10, true, spacing = 1.5.sp)
                                S("${st.count}", HInk, 16, true)
                            }
                        }
                    }

                    Spacer(Modifier.height(14.dp))
                    HSection(T("LAST 7 DAYS"), ftime(st.weekTotal * 60)) {
                        PillarChart(st.week, weekLabels)
                    }
                    Spacer(Modifier.height(14.dp))
                    HSection(T("LAST 16 WEEKS"), daysText(st.activeDays)) {
                        SandHeat(st.heat)
                    }
                } else {
                    S(
                        (if (Lang.tr) "YILLIKLAR" else "CHRONICLE") + "  ·  ${st.hist.size}",
                        HMuted, 11, true, spacing = 2.sp
                    )
                    Spacer(Modifier.height(12.dp))
                    if (st.hist.isEmpty()) {
                        S(T("No logs yet. Log your first session in Overview."), HMuted, 14, italic = true)
                    }
                    st.hist.forEach { e ->
                        Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
                            Box(modifier = Modifier.width(26.dp).fillMaxHeight()) {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    val cx = size.width / 2f
                                    drawLine(HLine, Offset(cx, 0f), Offset(cx, size.height), 2f)
                                    drawCircle(HSeal, 6.dp.toPx(), Offset(cx, 20.dp.toPx()))
                                    drawCircle(HBg, 2.5.dp.toPx(), Offset(cx, 20.dp.toPx()))
                                }
                            }
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(bottom = 10.dp)
                                    .background(HPanel, RoundedCornerShape(12.dp))
                                    .border(1.dp, HLine, RoundedCornerShape(12.dp))
                                    .padding(12.dp)
                            ) {
                                S(fmt.format(Date(e.ts)), HMuted, 11, italic = true)
                                S("${ftime(e.minutes * 60)}  ·  +${e.gain} XP", HInk, 15, true)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(40.dp))
            }

            if (dust.value < 1f) {
                BurstParticles(dust.value, HGold, Modifier.align(Alignment.Center).size(280.dp))
            }
            StampPop(
                "+${s.lastGain}", s.popId,
                Modifier.align(Alignment.Center)
            )
            BannerHost(
                s,
                Modifier.align(Alignment.TopCenter).statusBarsPadding().padding(top = 8.dp),
                HPanel, HBronze, HInk
            )
            LevelUpHost(s, HistorySkin)
        }
    }

    @Composable
    override fun LevelUp(area: String, level: Int, gained: Int, onDismiss: () -> Unit) {
        val unroll = remember { Animatable(0f) }
        val seal = remember { Animatable(3f) }
        val fade = remember { Animatable(0f) }
        val burst = remember { Animatable(0f) }
        LaunchedEffect(Unit) {
            launch { fade.animateTo(1f, tween(300)) }
            launch { unroll.animateTo(1f, tween(900, easing = FastOutSlowInEasing)) }
            delay(750L)
            launch { seal.animateTo(1f, spring(dampingRatio = 0.4f, stiffness = 350f)) }
            launch { burst.animateTo(1f, tween(1800, easing = FastOutSlowInEasing)) }
        }
        BackHandler { onDismiss() }
        val ink = Color(0xFF4A3219)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0B0703).copy(alpha = 0.90f * fade.value))
                .clickable { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            val shape = RoundedCornerShape(8.dp)
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.84f)
                    .scale(1f, unroll.value)
                    .clip(shape)
                    .background(Brush.verticalGradient(listOf(Color(0xFFF7EBCB), Color(0xFFE6CE9A))))
                    .border(2.dp, HBronze, shape)
                    .padding(vertical = 26.dp, horizontal = 18.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    S(
                        if (Lang.tr) "YENİ BİR ÇAĞ BAŞLIYOR" else "A NEW ERA BEGINS",
                        ink.copy(alpha = 0.75f), 12, true, spacing = 3.sp
                    )
                    Spacer(Modifier.height(8.dp))
                    S(eraName(level), ink, 30, true, Modifier, TextUnit.Unspecified, true)
                    Spacer(Modifier.height(18.dp))
                    WaxSeal(120.dp, Modifier.scale(seal.value)) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            S(T("LV"), SealInk, 11, true, spacing = 2.sp)
                            S("$level", SealInk, 38, true)
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    S(up(T(Ranks.title(level))), Color(0xFF9A5F12), 15, true, spacing = 3.sp)
                    S(A(area), ink.copy(alpha = 0.7f), 14, italic = true)
                    if (gained > 1) S("+$gained ${T("LV")}", Color(0xFF9A5F12), 14, true)
                    Spacer(Modifier.height(14.dp))
                    S(T("Tap to continue"), ink.copy(alpha = 0.6f), 12, italic = true)
                }
            }
            BurstParticles(burst.value, HGold, Modifier.size(420.dp))
        }
    }
}
