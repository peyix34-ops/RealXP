package com.realxp.app.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

private val SlBg = Color(0xFF080B1F)
private val SlBg2 = Color(0xFF111538)
private val SlPanel = Color(0xFF161B45)
private val SlLine = Color(0xFF2A3070)
private val SlLav = Color(0xFFB5A4FF)
private val SlMoon = Color(0xFFFFF1C2)
private val SlIndigo = Color(0xFF6B7BFF)
private val SlMint = Color(0xFF7FE7D6)
private val SlRose = Color(0xFFFF9EDC)
private val SlText = Color(0xFFEAE9FF)
private val SlMuted = Color(0xFF8D90C8)

private fun slTr(en: String, trk: String): String = if (Lang.tr) trk else en

private fun slDur(m: Int): String =
    if (m >= 60) "${m / 60}${T("h")}" + (if (m % 60 > 0) " ${m % 60}${T("m")}" else "")
    else "$m${T("m")}"

@Composable
private fun Zz(
    text: String,
    color: Color,
    sz: Int,
    bold: Boolean = false,
    modifier: Modifier = Modifier,
    spacing: TextUnit = TextUnit.Unspecified,
    italic: Boolean = false
) {
    Text(
        text,
        color = color,
        fontSize = sz.sp,
        fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
        fontStyle = if (italic) FontStyle.Italic else FontStyle.Normal,
        letterSpacing = spacing,
        modifier = modifier
    )
}

private fun moonPath(cx: Float, cy: Float, r: Float, phase: Float): Path {
    val ph = phase.coerceIn(0f, 1f)
    val path = Path()
    if (ph <= 0.01f) return path
    val k = cos(Math.PI * ph).toFloat()
    val rx = maxOf(r * abs(k), 0.5f)
    path.moveTo(cx, cy - r)
    path.arcTo(Rect(cx - r, cy - r, cx + r, cy + r), -90f, 180f, false)
    if (k >= 0f) {
        path.arcTo(Rect(cx - rx, cy - r, cx + rx, cy + r), 90f, -180f, false)
    } else {
        path.arcTo(Rect(cx - rx, cy - r, cx + rx, cy + r), 90f, 180f, false)
    }
    path.close()
    return path
}

@Composable
private fun MoonDisc(phase: Float, boxSize: Dp, glow: Float = 0f, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(boxSize)) {
        val r = size.minDimension / 2f * 0.84f
        val c = Offset(size.width / 2f, size.height / 2f)
        if (glow > 0.01f) {
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(SlMoon.copy(alpha = glow), Color.Transparent),
                    center = c,
                    radius = r * 2.3f
                ),
                radius = r * 2.3f,
                center = c
            )
        }
        drawCircle(SlBg2, r, c)
        drawCircle(SlLine, r, c, style = Stroke(1.dp.toPx()))
        drawPath(moonPath(c.x, c.y, r, phase), SlMoon)
    }
}

@Composable
private fun MoonRow(p: Float, count: Int, disc: Dp) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        for (i in 0 until count) {
            MoonDisc((p * count - i).coerceIn(0f, 1f), disc)
            Spacer(Modifier.width(4.dp))
        }
    }
}

@Composable
private fun NightSky(modifier: Modifier, stars: Int = 70, extras: Boolean = true) {
    val t by rememberInfiniteTransition(label = "sky").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(8000, easing = LinearEasing)),
        label = "skyT"
    )
    val shoot by rememberInfiniteTransition(label = "shoot").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(10000, easing = LinearEasing)),
        label = "shootT"
    )
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        if (extras) {
            drawRect(
                Brush.verticalGradient(
                    listOf(Color.Transparent, SlIndigo.copy(alpha = 0.10f), SlLav.copy(alpha = 0.13f)),
                    startY = h * 0.5f,
                    endY = h
                )
            )
        }
        for (i in 0 until stars) {
            val x = w * ((i * 0.6180339f) % 1f)
            val y = h * ((i * 0.4142f + 0.11f * (i % 7)) % 1f)
            val tw = 0.25f + 0.75f * abs(sin(t * 6.2831855f * (1f + (i % 3)) + i))
            drawCircle(
                Color.White.copy(alpha = 0.12f + 0.6f * tw),
                (0.8f + (i % 3) * 0.5f).dp.toPx(),
                Offset(x, y)
            )
        }
        if (extras && shoot < 0.16f) {
            val q = shoot / 0.16f
            val head = Offset(w * (0.95f - 0.5f * q), h * (0.10f + 0.20f * q))
            val tail = Offset(head.x + w * 0.16f, head.y - h * 0.06f)
            drawLine(
                brush = Brush.linearGradient(listOf(Color.Transparent, Color.White), start = tail, end = head),
                start = tail,
                end = head,
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round
            )
            drawCircle(Color.White, 2.5.dp.toPx(), head)
        }
    }
}

@Composable
private fun ZzzIdle(modifier: Modifier) {
    val t by rememberInfiniteTransition(label = "zidle").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(4200, easing = LinearEasing)),
        label = "zidleT"
    )
    Box(modifier = modifier.size(width = 70.dp, height = 90.dp)) {
        listOf("z", "Z", "Z").forEachIndexed { i, ch ->
            val q = (t + i / 3f) % 1f
            Text(
                ch,
                color = SlLav.copy(alpha = sin(q * 3.1415927f)),
                fontSize = (13 + i * 5).sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.offset(
                    x = (6 + i * 16 + 6 * sin(q * 6f)).dp,
                    y = (66 - q * 62).dp
                )
            )
        }
    }
}

@Composable
private fun ZzzPop(id: Int, modifier: Modifier) {
    val prog = remember { Animatable(1f) }
    LaunchedEffect(id) {
        if (id > 0) {
            prog.snapTo(0f)
            prog.animateTo(1f, tween(1900, easing = LinearEasing))
        }
    }
    val p = prog.value
    if (id > 0 && p < 1f) {
        Box(modifier = modifier.size(width = 120.dp, height = 140.dp)) {
            listOf("z", "Z", "Z").forEachIndexed { i, ch ->
                val q = ((p - i * 0.16f) / 0.68f).coerceIn(0f, 1f)
                if (q > 0f && q < 1f) {
                    Text(
                        ch,
                        color = SlLav.copy(alpha = sin(q * 3.1415927f)),
                        fontSize = (18 + i * 8).sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.offset(
                            x = (14 + i * 28 + 10 * sin(q * 6f)).dp,
                            y = (110 - q * 100).dp
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun DreamGain(text: String, id: Int, modifier: Modifier) {
    val prog = remember { Animatable(1f) }
    LaunchedEffect(id) {
        if (id > 0) {
            prog.snapTo(0f)
            prog.animateTo(1f, tween(1500, easing = FastOutSlowInEasing))
        }
    }
    val p = prog.value
    if (id > 0 && p < 1f) {
        Text(
            text,
            modifier = modifier
                .offset(y = (-60f * p).dp)
                .alpha(if (p < 0.6f) 1f else 1f - (p - 0.6f) / 0.4f),
            style = TextStyle(
                color = SlMoon,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                shadow = Shadow(color = SlLav, offset = Offset.Zero, blurRadius = 22f)
            )
        )
    }
}

@Composable
private fun SleepCard(title: String, trailing: String?, content: @Composable ColumnScope.() -> Unit) {
    val shape = RoundedCornerShape(20.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(SlPanel.copy(alpha = 0.92f), shape)
            .border(1.dp, Brush.horizontalGradient(listOf(SlLav.copy(alpha = 0.35f), SlIndigo.copy(alpha = 0.3f))), shape)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Zz(title, SlMuted, 11, true, Modifier, 2.5.sp)
            if (trailing != null) Zz(trailing, SlMoon, 13, true)
        }
        Spacer(Modifier.height(12.dp))
        content()
    }
}

@Composable
private fun PillowButton(
    m: Int,
    chip: String,
    enabled: Boolean,
    tint: Color,
    modifier: Modifier,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(24.dp)
    Column(
        modifier = modifier
            .alpha(if (enabled) 1f else 0.45f)
            .then(if (enabled) Modifier.bounceClick { onClick() } else Modifier)
            .background(Brush.verticalGradient(listOf(tint.copy(alpha = 0.24f), SlPanel)), shape)
            .border(1.dp, tint.copy(alpha = 0.5f), shape)
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            MoonDisc((m / 90f * 0.9f + 0.1f).coerceAtMost(1f), 24.dp)
            Spacer(Modifier.width(10.dp))
            Zz(slDur(m), SlText, 18, true)
        }
        Spacer(Modifier.height(8.dp))
        Box(
            Modifier
                .background(SlMoon.copy(alpha = 0.16f), RoundedCornerShape(50))
                .padding(horizontal = 10.dp, vertical = 3.dp)
        ) {
            Zz(chip, SlMoon, 12, true)
        }
    }
}

@Composable
private fun StatMini(label: String, value: String, modifier: Modifier) {
    Column(modifier = modifier) {
        Zz(label, SlMuted, 10, true, Modifier, 1.5.sp)
        Spacer(Modifier.height(3.dp))
        Zz(value, SlText, 15, true)
    }
}

@Composable
private fun MoonWeek(days: List<DayStat>, labels: List<String>) {
    Row(modifier = Modifier.fillMaxWidth()) {
        days.forEachIndexed { i, d ->
            val ph = animatedProgress((d.minutes / 480f).coerceIn(0f, 1f), 900)
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MoonDisc(ph, 36.dp, if (d.isToday) 0.35f else 0f)
                Spacer(Modifier.height(4.dp))
                Zz(
                    labels[i],
                    if (d.isToday) SlMoon else SlMuted, 11, d.isToday
                )
                Zz(
                    when {
                        d.minutes <= 0 -> ""
                        d.minutes >= 60 -> "${d.minutes / 60}${T("h")}"
                        else -> "${d.minutes}"
                    },
                    SlMuted, 9, false, Modifier.height(12.dp)
                )
            }
        }
    }
}

@Composable
private fun StarHeat(cols: List<List<Int?>>) {
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
                    val lvl = heatLevel(m)
                    val cx = w * (c + g) + c / 2f
                    val cy = r * (c + g) + c / 2f
                    val col = when (lvl) {
                        0 -> SlLine.copy(alpha = 0.8f)
                        1 -> SlLav.copy(alpha = 0.45f)
                        2 -> SlLav
                        else -> SlMoon
                    }
                    val rad = c / 2f * (0.22f + 0.13f * lvl) * t
                    if (lvl >= 3) {
                        drawCircle(SlMoon.copy(alpha = 0.18f * t), c * 0.62f * t, Offset(cx, cy))
                    }
                    drawCircle(col.copy(alpha = col.alpha * t), rad, Offset(cx, cy))
                    if (lvl == 4) {
                        val arm = c * 0.46f * t
                        drawLine(SlMoon, Offset(cx - arm, cy), Offset(cx + arm, cy), 1.dp.toPx())
                        drawLine(SlMoon, Offset(cx, cy - arm), Offset(cx, cy + arm), 1.dp.toPx())
                    }
                }
            }
        }
    }
}

object SleepSkin : ArenaSkin {

    @Composable
    override fun PickerTile(name: String, on: Boolean, modifier: Modifier, onClick: () -> Unit) {
        val border by animateColorAsState(if (on) SlLav else SlLine, tween(200), label = "slb")
        val sc by animateFloatAsState(
            if (on) 1.05f else 1f,
            spring(dampingRatio = 0.5f, stiffness = 500f),
            label = "slsc"
        )
        val shape = RoundedCornerShape(14.dp)
        Box(
            modifier = modifier
                .height(88.dp)
                .scale(sc)
                .clip(shape)
                .background(Brush.verticalGradient(listOf(SlBg2, SlBg)))
                .border(1.dp, border, shape)
                .clickable { onClick() }
        ) {
            NightSky(Modifier.matchParentSize(), 16, false)
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MoonDisc(0.32f, 36.dp, 0.28f)
                Spacer(Modifier.height(4.dp))
                Zz(A(name), SlMoon, 13, true)
            }
            if (on) {
                PickCheck(SlLav, SlBg, Modifier.align(Alignment.TopEnd).padding(6.dp))
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
                .background(Brush.verticalGradient(listOf(SlBg2, SlBg)))
                .border(1.dp, SlLav.copy(alpha = 0.4f), shape)
                .clickable { onClick() }
        ) {
            NightSky(Modifier.matchParentSize(), 24, false)
            MoonDisc(p, 54.dp, 0.3f, Modifier.align(Alignment.TopEnd).padding(8.dp))
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp)
                    .background(SlLav.copy(alpha = 0.18f), RoundedCornerShape(50))
                    .padding(horizontal = 9.dp, vertical = 3.dp)
            ) {
                if (editing && canRemove) {
                    Icon(Icons.Outlined.Close, null, tint = Danger, modifier = Modifier.size(12.dp))
                } else {
                    Zz("${T("LV")} ${info.level}", SlLav, 12, true)
                }
            }
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp)
            ) {
                Zz(A(name), SlMoon, 15, true)
                Zz(T(Ranks.title(info.level)), SlLav, 11)
                Spacer(Modifier.height(3.dp))
                Zz("${info.into}/${info.need} XP", SlMuted, 10)
            }
        }
    }

    @Composable
    override fun Screen(s: AreaState, onBack: () -> Unit) {
        DisposableEffect(Unit) {
            Chrome.darkSurface = true
            onDispose { Chrome.darkSurface = false }
        }
        val info = s.info
        val st = s.stats()
        val barP = animatedXpBar(info.level, info.into.toFloat() / info.need)
        val xpTodayShown = animatedCount(st.xpToday)
        val typed = s.input.toIntOrNull()
        val todayIdx = (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) + 5) % 7
        val weekLabels = (6 downTo 0).map { dayLetter(((todayIdx - it) % 7 + 7) % 7) }
        val fmt = remember(Lang.tr) {
            SimpleDateFormat("d MMM  ·  HH:mm", if (Lang.tr) Locale("tr", "TR") else Locale.US)
        }
        val glow = remember { Animatable(0f) }
        val lines = remember { mutableStateListOf<String>() }

        LaunchedEffect(s.popId) {
            if (s.popId > 0) {
                launch {
                    glow.snapTo(1f)
                    glow.animateTo(0f, tween(2400, easing = FastOutSlowInEasing))
                }
                lines.add(slTr("Lights out… ${s.lastMin}m", "Işıklar söndü… ${s.lastMin}dk"))
                delay(380L)
                lines.add(slTr("Deep rest reached", "Derin dinlenmeye ulaşıldı"))
                delay(380L)
                lines.add("+${s.lastGain} XP")
                while (lines.size > 3) lines.removeAt(0)
            }
        }

        HonestyHost(s, SlPanel, SlText, SlMuted, SlLav)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(SlBg2, SlBg)))
        ) {
            NightSky(Modifier.fillMaxSize())
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(Modifier.height(10.dp))
                Zz(T("←  Back"), SlMuted, 14, false, Modifier.clickable { onBack() }.padding(vertical = 8.dp))
                Spacer(Modifier.height(8.dp))

                val heroShape = RoundedCornerShape(26.dp)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(heroShape)
                        .background(Brush.verticalGradient(listOf(SlBg2.copy(alpha = 0.95f), SlPanel.copy(alpha = 0.95f))))
                        .border(
                            1.dp,
                            Brush.horizontalGradient(listOf(SlLav.copy(alpha = 0.6f), SlIndigo.copy(alpha = 0.4f))),
                            heroShape
                        )
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Zz(slTr("REST MODE", "DİNLENME MODU"), SlLav, 10, true, Modifier, 3.sp)
                            Spacer(Modifier.height(6.dp))
                            Zz(A(s.area), SlMoon, 28, true)
                            Zz(up(T(Ranks.title(info.level))), SlMuted, 10, true, Modifier, 2.sp)
                            Spacer(Modifier.height(10.dp))
                            Box(
                                Modifier
                                    .background(SlLav.copy(alpha = 0.18f), RoundedCornerShape(50))
                                    .padding(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Zz("${T("LV")} ${info.level}", SlLav, 13, true)
                            }
                            Spacer(Modifier.height(10.dp))
                            Zz("${info.need - info.into} XP → ${T("LV")} ${info.level + 1}", SlMoon, 12, true)
                        }
                        Box(contentAlignment = Alignment.Center) {
                            MoonDisc(barP, 132.dp, 0.30f + 0.4f * glow.value)
                            ZzzIdle(Modifier.align(Alignment.TopEnd).offset(x = 14.dp, y = (-14).dp))
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .background(SlLine, RoundedCornerShape(6.dp))
                    ) {
                        Box(
                            Modifier
                                .fillMaxWidth(barP.coerceIn(0.02f, 1f))
                                .fillMaxHeight()
                                .background(Brush.horizontalGradient(listOf(SlLav, SlMoon)), RoundedCornerShape(6.dp))
                        )
                    }
                    Spacer(Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Zz("${info.into} / ${info.need} XP", SlMuted, 12)
                        Zz("☾ ${(barP * 100).toInt()}%", SlLav, 12, true)
                    }
                }

                Spacer(Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    listOf(T("Overview"), slTr("Dream journal", "Rüya günlüğü")).forEachIndexed { i, label ->
                        val on = s.tab == i
                        Column(
                            modifier = Modifier.weight(1f).clickable { s.tab = i },
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Zz(label, if (on) SlLav else SlMuted, 14, on, Modifier.padding(vertical = 10.dp))
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .height(2.dp)
                                    .background(if (on) SlLav else SlLine)
                            )
                        }
                    }
                }
                Spacer(Modifier.height(18.dp))

                if (s.tab == 0) {
                    Zz(T("LOG YOUR WORK"), SlMuted, 11, true, Modifier, 2.5.sp)
                    Spacer(Modifier.height(10.dp))
                    val mins = listOf(15, 30, 60, 90)
                    val tints = listOf(SlLav, SlIndigo, SlMint, SlRose)
                    for (r in 0..1) {
                        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)) {
                            for (c in 0..1) {
                                val idx = r * 2 + c
                                val m = mins[idx]
                                val ok = s.fits(m)
                                if (c > 0) Spacer(Modifier.width(10.dp))
                                PillowButton(
                                    m,
                                    if (ok) "+${s.gainFor(m)} XP" else "24${T("h")}",
                                    ok, tints[idx], Modifier.weight(1f)
                                ) { s.request(m) }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .background(SlPanel, RoundedCornerShape(26.dp))
                                .border(1.dp, SlLav.copy(alpha = 0.5f), RoundedCornerShape(26.dp))
                                .padding(horizontal = 18.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Zz("☾", SlMoon, 20, true)
                            Spacer(Modifier.width(10.dp))
                            BasicTextField(
                                value = s.input,
                                onValueChange = { v ->
                                    s.input = v.filter { it.isDigit() }.take(4)
                                    s.error = ""
                                },
                                singleLine = true,
                                textStyle = TextStyle(color = SlText, fontSize = 16.sp, fontWeight = FontWeight.SemiBold),
                                cursorBrush = SolidColor(SlLav),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                                decorationBox = { inner ->
                                    Box {
                                        if (s.input.isEmpty()) Zz(T("Other (min)"), SlMuted, 16)
                                        inner()
                                    }
                                }
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .bounceClick { s.submitInput() }
                                .height(52.dp)
                                .background(Brush.horizontalGradient(listOf(SlLav, SlIndigo)), RoundedCornerShape(26.dp))
                                .padding(horizontal = 18.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Zz("ZZZ", SlBg, 14, true, Modifier, 2.sp)
                        }
                    }
                    Spacer(Modifier.height(6.dp))
                    Zz(
                        when {
                            s.error.isNotEmpty() -> s.error
                            typed != null && typed >= 1 && s.fits(typed) ->
                                TF("+%d XP  ·  %d min", s.gainFor(typed), typed)
                            typed != null && typed >= 1 -> T("A day only has 24 hours.")
                            else -> T("Type any amount, for example 5.")
                        },
                        if (s.error.isNotEmpty()) Danger else SlMuted, 12
                    )

                    Spacer(Modifier.height(14.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 84.dp)
                            .background(Color(0xFF0B0F2B).copy(alpha = 0.9f), RoundedCornerShape(18.dp))
                            .border(1.dp, SlLine, RoundedCornerShape(18.dp))
                            .padding(14.dp)
                    ) {
                        Zz(slTr("DREAM LOG", "RÜYA KAYDI"), SlMuted, 10, true, Modifier, 2.sp)
                        Spacer(Modifier.height(6.dp))
                        if (lines.isEmpty()) {
                            Zz("✦ " + slTr("Waiting for the night…", "Gece bekleniyor…"), SlMuted.copy(alpha = 0.8f), 13, false, Modifier, TextUnit.Unspecified, true)
                        }
                        lines.takeLast(3).forEach { line ->
                            Zz(
                                "✦ $line",
                                if (line.startsWith("+")) SlMoon else SlLav,
                                13, line.startsWith("+")
                            )
                        }
                    }

                    Spacer(Modifier.height(18.dp))
                    SleepCard(T("TODAY'S XP"), null) {
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                "+$xpTodayShown",
                                style = TextStyle(
                                    brush = Brush.horizontalGradient(listOf(SlLav, SlMoon)),
                                    fontSize = 40.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            )
                            Spacer(Modifier.width(6.dp))
                            Zz("XP", SlMuted, 14, true, Modifier.padding(bottom = 8.dp))
                        }
                        Spacer(Modifier.height(8.dp))
                        val lit = (st.todaySec / 60f / 96f).toInt().coerceIn(0, 5)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            for (i in 0 until 5) {
                                Zz("★", if (i < lit) SlMoon else SlLine, 20)
                            }
                            Spacer(Modifier.width(10.dp))
                            Zz("${ftime(st.todaySec)} / 8${T("h")}", SlMuted, 12)
                        }
                        Spacer(Modifier.height(12.dp))
                        Box(Modifier.fillMaxWidth().height(1.dp).background(SlLine))
                        Spacer(Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth()) {
                            StatMini(T("TODAY"), ftime(st.todaySec), Modifier.weight(1f))
                            StatMini(T("TOTAL"), ftime(st.totalSec), Modifier.weight(1f))
                            StatMini(T("LOGS"), "${st.count}", Modifier.weight(1f))
                        }
                    }

                    Spacer(Modifier.height(14.dp))
                    SleepCard(T("LAST 7 DAYS"), ftime(st.weekTotal * 60)) {
                        MoonWeek(st.week, weekLabels)
                    }
                    Spacer(Modifier.height(14.dp))
                    SleepCard(T("LAST 16 WEEKS"), daysText(st.activeDays)) {
                        StarHeat(st.heat)
                    }
                } else {
                    Zz(
                        slTr("DREAM JOURNAL", "RÜYA GÜNLÜĞÜ") + "  ·  ${st.hist.size}",
                        SlMuted, 11, true, Modifier, 2.sp
                    )
                    Spacer(Modifier.height(12.dp))
                    if (st.hist.isEmpty()) {
                        Zz(T("No logs yet. Log your first session in Overview."), SlMuted, 14, false, Modifier, TextUnit.Unspecified, true)
                    }
                    st.hist.forEach { e ->
                        val shape = RoundedCornerShape(18.dp)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                                .background(SlPanel, shape)
                                .border(1.dp, SlLine, shape)
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            MoonDisc((e.minutes / 480f).coerceIn(0.08f, 1f), 38.dp)
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Zz(ftime(e.minutes * 60), SlText, 15, true)
                                Zz(fmt.format(Date(e.ts)), SlMuted, 11)
                            }
                            Zz("+${e.gain} XP", SlMoon, 14, true)
                        }
                    }
                }
                Spacer(Modifier.height(40.dp))
            }

            ZzzPop(s.popId, Modifier.align(Alignment.TopEnd).statusBarsPadding().padding(top = 110.dp, end = 24.dp))
            DreamGain(
                "+${s.lastGain} XP", s.popId,
                Modifier.align(Alignment.TopCenter).statusBarsPadding().padding(top = 200.dp)
            )
            BannerHost(
                s,
                Modifier.align(Alignment.TopCenter).statusBarsPadding().padding(top = 8.dp),
                SlPanel, SlLav, SlText
            )
            LevelUpHost(s, SleepSkin)
        }
    }

    @Composable
    override fun LevelUp(area: String, level: Int, gained: Int, onDismiss: () -> Unit) {
        val fade = remember { Animatable(0f) }
        val phase = remember { Animatable(0.12f) }
        val num = remember { Animatable(0f) }
        val burst = remember { Animatable(0f) }
        val star = remember { Animatable(0f) }
        LaunchedEffect(Unit) {
            launch { fade.animateTo(1f, tween(400)) }
            launch { star.animateTo(1f, tween(1500, easing = FastOutSlowInEasing)) }
            phase.animateTo(1f, tween(1800, easing = FastOutSlowInEasing))
            launch { burst.animateTo(1f, tween(1800, easing = FastOutSlowInEasing)) }
            num.animateTo(1f, tween(600))
        }
        BackHandler { onDismiss() }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(SlBg.copy(alpha = 0.97f * fade.value))
                .clickable { onDismiss() }
        ) {
            NightSky(Modifier.fillMaxSize().alpha(fade.value))
            Canvas(modifier = Modifier.fillMaxSize()) {
                val q = star.value
                if (q < 1f) {
                    val head = Offset(size.width * (0.95f - 0.8f * q), size.height * (0.08f + 0.35f * q))
                    val tail = Offset(head.x + size.width * 0.18f, head.y - size.height * 0.07f)
                    drawLine(
                        brush = Brush.linearGradient(listOf(Color.Transparent, Color.White), start = tail, end = head),
                        start = tail,
                        end = head,
                        strokeWidth = 3.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                    drawCircle(Color.White, 3.dp.toPx(), head)
                }
            }
            BurstParticles(burst.value, SlLav, Modifier.align(Alignment.Center).size(380.dp))
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Zz(
                    if (gained > 1) "+$gained  ${T("LV")}" else slTr("MAKE A WISH", "DİLEK TUT"),
                    SlLav, 14, true, Modifier.alpha(fade.value), 5.sp
                )
                Spacer(Modifier.height(14.dp))
                Box(contentAlignment = Alignment.Center) {
                    MoonDisc(phase.value, 230.dp, 0.5f * fade.value)
                    Text(
                        "$level",
                        modifier = Modifier.alpha(num.value),
                        style = TextStyle(
                            color = Color(0xFF2A2350),
                            fontSize = 72.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    )
                }
                Spacer(Modifier.height(16.dp))
                Zz(up(T(Ranks.title(level))), SlMoon, 17, true, Modifier.alpha(num.value), 4.sp)
                Zz(A(area), SlMuted, 14, false, Modifier.alpha(num.value))
                Spacer(Modifier.height(24.dp))
                Zz(T("Tap to continue"), SlMuted, 12, false, Modifier.alpha(num.value))
            }
        }
    }
}
