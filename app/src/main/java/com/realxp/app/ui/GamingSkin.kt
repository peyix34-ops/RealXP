package com.realxp.app.ui

import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
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
import kotlin.math.ceil
import kotlin.math.sin

private val GBg = Color(0xFF0E0722)
private val GBg2 = Color(0xFF1A0E3D)
private val GPanel = Color(0xFF1D1146)
private val GLine = Color(0xFF3B2A7A)
private val GPink = Color(0xFFFF3EA5)
private val GCyan = Color(0xFF2EE6FF)
private val GYellow = Color(0xFFFFE14D)
private val GGreen = Color(0xFF39FF88)
private val GText = Color(0xFFF2EEFF)
private val GMuted = Color(0xFF9A8CCF)

private fun tr(en: String, trk: String): String = if (Lang.tr) trk else en

@Composable
private fun G(
    text: String,
    color: Color,
    sz: Int,
    bold: Boolean = true,
    modifier: Modifier = Modifier,
    spacing: TextUnit = TextUnit.Unspecified
) {
    Text(
        text,
        color = color,
        fontSize = sz.sp,
        fontFamily = FontFamily.Monospace,
        fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
        letterSpacing = spacing,
        modifier = modifier
    )
}

private fun Modifier.pixelFrame(fill: Color, edge: Color): Modifier = this.drawBehind {
    val s = 4.dp.toPx()
    val w = size.width - s
    val h = size.height - s
    drawRect(Color.Black.copy(alpha = 0.45f), Offset(s, s), Size(w, h))
    drawRect(fill, Offset.Zero, Size(w, h))
    drawRect(edge, Offset.Zero, Size(w, h), style = Stroke(2.dp.toPx()))
}

@Composable
private fun SynthBg(modifier: Modifier) {
    val t by rememberInfiniteTransition(label = "synth").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(6000, easing = LinearEasing)),
        label = "synthT"
    )
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val hy = h * 0.66f
        for (i in 0 until 40) {
            val x = w * ((i * 0.6180339f) % 1f)
            val y = hy * ((i * 0.3819f + 0.17f) % 1f)
            val a = 0.2f + 0.5f * abs(sin(t * 6.2831855f + i))
            drawRect(Color.White.copy(alpha = a), Offset(x, y), Size(2.dp.toPx(), 2.dp.toPx()))
        }
        val r = w * 0.22f
        drawCircle(
            brush = Brush.verticalGradient(listOf(GYellow, GPink), startY = hy - 2 * r, endY = hy),
            radius = r,
            center = Offset(w / 2f, hy - r * 0.3f)
        )
        drawRect(GBg, Offset(0f, hy), Size(w, h - hy))
        for (k in 0 until 10) {
            val ph = ((k + t) % 10f) / 10f
            val y = hy + (h - hy) * ph * ph
            drawLine(GPink.copy(alpha = 0.12f + 0.4f * ph), Offset(0f, y), Offset(w, y), 1.5f)
        }
        for (j in -8..8) {
            drawLine(GCyan.copy(alpha = 0.22f), Offset(w / 2f, hy), Offset(w / 2f + j * w * 0.18f, h), 1.5f)
        }
        drawLine(GPink, Offset(0f, hy), Offset(w, hy), 2.dp.toPx())
    }
}

@Composable
private fun PadIcon(w: Dp, color: Color, accent: Color) {
    Canvas(modifier = Modifier.size(w, w * 0.62f)) {
        val bw = size.width
        val bh = size.height
        drawRoundRect(color.copy(alpha = 0.18f), Offset.Zero, size, CornerRadius(bh * 0.35f))
        drawRoundRect(color, Offset.Zero, size, CornerRadius(bh * 0.35f), style = Stroke(2.dp.toPx()))
        val cx = bw * 0.28f
        val cy = bh * 0.5f
        val a = bh * 0.16f
        drawRect(color, Offset(cx - a * 1.6f, cy - a * 0.5f), Size(a * 3.2f, a))
        drawRect(color, Offset(cx - a * 0.5f, cy - a * 1.6f), Size(a, a * 3.2f))
        drawCircle(accent, bh * 0.11f, Offset(bw * 0.74f, cy - bh * 0.1f))
        drawCircle(color, bh * 0.11f, Offset(bw * 0.84f, cy + bh * 0.08f))
    }
}

@Composable
private fun PixelBar(p: Float, n: Int, h: Dp) {
    Canvas(modifier = Modifier.fillMaxWidth().height(h)) {
        val gap = 2.dp.toPx()
        val bw = (size.width - gap * (n - 1)) / n
        val lit = p.coerceIn(0f, 1f) * n
        for (i in 0 until n) {
            val f = (lit - i).coerceIn(0f, 1f)
            val x = i * (bw + gap)
            drawRect(GLine.copy(alpha = 0.7f), Offset(x, 0f), Size(bw, size.height))
            if (f > 0f) {
                drawRect(
                    lerp(GPink, GCyan, i / (n - 1f)).copy(alpha = 0.35f + 0.65f * f),
                    Offset(x, 0f), Size(bw, size.height)
                )
            }
        }
    }
}

@Composable
private fun ArcadeButton(
    m: Int,
    chip: String,
    enabled: Boolean,
    color: Color,
    modifier: Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier.alpha(if (enabled) 1f else 0.45f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(66.dp)
                .then(if (enabled) Modifier.bounceClick { onClick() } else Modifier),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val r = size.minDimension / 2f - 4.dp.toPx()
                val c = Offset(size.width / 2f, size.height / 2f)
                drawCircle(Color.Black.copy(alpha = 0.5f), r, Offset(c.x, c.y + 5.dp.toPx()))
                drawCircle(color.copy(alpha = 0.55f), r, Offset(c.x, c.y + 3.dp.toPx()))
                drawCircle(
                    Brush.radialGradient(
                        listOf(color, color.copy(alpha = 0.72f)),
                        center = Offset(size.width * 0.4f, size.height * 0.35f),
                        radius = r * 1.3f
                    ),
                    r, c
                )
                drawCircle(Color.White.copy(alpha = 0.5f), r * 0.26f, Offset(size.width * 0.38f, size.height * 0.32f))
            }
            G("$m${T("m")}", Color.White, 15)
        }
        Spacer(Modifier.height(6.dp))
        G(chip, color, 11)
    }
}

@Composable
private fun PixelBars(days: List<DayStat>, labels: List<String>) {
    val maxM = maxOf(60, days.maxOf { it.minutes })
    val unit = maxOf(15, ceil(maxM / 6f).toInt())
    val prog = animatedProgress(1f, 900)
    Column {
        Canvas(modifier = Modifier.fillMaxWidth().height(100.dp)) {
            val n = days.size
            val slot = size.width / n
            val bw = slot * 0.56f
            val bh = 14.dp.toPx()
            val gap = 3.dp.toPx()
            for (i in 0 until n) {
                val blocks = ceil(days[i].minutes / unit.toFloat()).toInt().coerceAtMost(6)
                val shown = blocks * prog
                val x = i * slot + (slot - bw) / 2f
                for (b in 0 until blocks) {
                    if (b + 1 > shown + 0.01f) break
                    val y = size.height - (b + 1) * (bh + gap)
                    val col = when {
                        days[i].isToday -> if (b == blocks - 1) GYellow else GPink
                        else -> if (b == blocks - 1) GCyan else GCyan.copy(alpha = 0.6f)
                    }
                    drawRect(col, Offset(x, y), Size(bw, bh))
                }
                if (days[i].minutes == 0) {
                    drawRect(GLine, Offset(x, size.height - 4.dp.toPx()), Size(bw, 4.dp.toPx()))
                }
            }
        }
        Spacer(Modifier.height(4.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            labels.forEachIndexed { i, l ->
                G(
                    l, if (days[i].isToday) GYellow else GMuted, 11, days[i].isToday,
                    Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun PixelHeat(cols: List<List<Int?>>) {
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
                        0 -> GLine.copy(alpha = 0.6f)
                        1 -> GCyan.copy(alpha = 0.45f)
                        2 -> GCyan
                        3 -> GPink
                        else -> GYellow
                    }
                    drawRect(
                        col.copy(alpha = col.alpha * t),
                        Offset(w * (c + g), r * (c + g)), Size(c, c)
                    )
                }
            }
        }
    }
}

@Composable
private fun PixelPop(text: String, id: Int, modifier: Modifier) {
    val prog = remember { Animatable(1f) }
    LaunchedEffect(id) {
        if (id > 0) {
            prog.snapTo(0f)
            prog.animateTo(1f, tween(1300, easing = FastOutSlowInEasing))
        }
    }
    val p = prog.value
    if (id > 0 && p < 1f) {
        Text(
            text,
            modifier = modifier
                .offset(y = (-80f * p).dp)
                .alpha(if (p < 0.6f) 1f else 1f - (p - 0.6f) / 0.4f)
                .scale(0.7f + 0.5f * (1f - p)),
            style = TextStyle(
                color = GYellow,
                fontSize = 32.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.ExtraBold,
                shadow = Shadow(color = GPink, offset = Offset(5f, 5f), blurRadius = 0f)
            )
        )
    }
}

object GamingSkin : ArenaSkin {

    @Composable
    override fun PickerTile(name: String, on: Boolean, modifier: Modifier, onClick: () -> Unit) {
        val sc by animateFloatAsState(
            if (on) 1.05f else 1f,
            spring(dampingRatio = 0.5f, stiffness = 500f),
            label = "gsc"
        )
        val shape = RoundedCornerShape(10.dp)
        Box(
            modifier = modifier
                .height(88.dp)
                .scale(sc)
                .clip(shape)
                .background(Brush.verticalGradient(listOf(GBg2, GBg)))
                .border(2.dp, if (on) GYellow else GPink.copy(alpha = 0.6f), shape)
                .clickable { onClick() }
        ) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PadIcon(40.dp, GCyan, GPink)
                Spacer(Modifier.height(6.dp))
                G(A(name), GCyan, 12)
            }
            if (on) {
                PickCheck(GYellow, GBg, Modifier.align(Alignment.TopEnd).padding(6.dp))
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
        val shape = RoundedCornerShape(12.dp)
        Box(
            modifier = modifier
                .height(126.dp)
                .clip(shape)
                .background(Brush.verticalGradient(listOf(GBg2, GBg)))
                .border(2.dp, Brush.horizontalGradient(listOf(GPink, GCyan)), shape)
                .clickable { onClick() }
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    G("P1", GYellow, 12)
                    if (editing && canRemove) {
                        Icon(Icons.Outlined.Close, null, tint = Danger, modifier = Modifier.size(14.dp))
                    } else {
                        Box(
                            Modifier
                                .background(GPink, RoundedCornerShape(3.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            G("${T("LV")} ${info.level}", Color.White, 11)
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                G(A(name).uppercase(), GText, 14)
                G(up(T(Ranks.title(info.level))), GMuted, 10, false)
                Spacer(Modifier.weight(1f))
                PixelBar(p, 12, 8.dp)
                Spacer(Modifier.height(3.dp))
                G("${info.into}/${info.need} XP", GMuted, 10, false)
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
            SimpleDateFormat("d MMM  HH:mm", if (Lang.tr) Locale("tr", "TR") else Locale.US)
        }
        val midnight = remember(st.hist.size) {
            Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
        }
        val combo = st.hist.count { it.ts >= midnight }
        val lines = remember { mutableStateListOf<String>() }
        val dust = remember { Animatable(1f) }

        LaunchedEffect(s.popId) {
            if (s.popId > 0) {
                launch {
                    dust.snapTo(0f)
                    dust.animateTo(1f, tween(1000, easing = FastOutSlowInEasing))
                }
                lines.add(tr("QUEST COMPLETE!", "GÖREV TAMAM!"))
                delay(300L)
                lines.add("+${s.lastGain} XP")
                delay(300L)
                lines.add("COMBO x$combo")
                while (lines.size > 3) lines.removeAt(0)
            }
        }

        HonestyHost(s, GPanel, GText, GMuted, GCyan)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(GBg2, GBg)))
        ) {
            SynthBg(Modifier.fillMaxSize())
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(Modifier.height(10.dp))
                G(T("←  Back"), GMuted, 14, false, Modifier.clickable { onBack() }.padding(vertical = 8.dp))
                Spacer(Modifier.height(8.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .pixelFrame(GPanel, GPink)
                        .padding(start = 14.dp, top = 14.dp, end = 18.dp, bottom = 18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(72.dp).pixelFrame(GBg, GYellow),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                G(T("LV"), GPink, 10, true, Modifier, 2.sp)
                                G("${info.level}", GYellow, 28)
                            }
                        }
                        Spacer(Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            G("PLAYER 1", GCyan, 10, true, Modifier, 3.sp)
                            G(A(s.area).uppercase(), GText, 24)
                            G(up(T(Ranks.title(info.level))), GYellow, 12)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            G("SCORE", GMuted, 10, true, Modifier, 2.sp)
                            G("%06d".format(s.xp), GGreen, 16)
                        }
                    }
                    Spacer(Modifier.height(14.dp))
                    PixelBar(barP, 20, 14.dp)
                    Spacer(Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        G("${info.into}/${info.need} XP", GMuted, 11, false)
                        G("${T("LV")} ${info.level + 1}: ${info.need - info.into} XP", GPink, 11)
                    }
                }

                Spacer(Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    listOf(T("Overview").uppercase(), tr("HIGH SCORES", "REKORLAR")).forEachIndexed { i, label ->
                        val on = s.tab == i
                        if (i > 0) Spacer(Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { s.tab = i }
                                .background(if (on) GPink else Color.Transparent)
                                .border(2.dp, if (on) GPink else GLine)
                                .padding(vertical = 9.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            G(label, if (on) Color.White else GMuted, 12)
                        }
                    }
                }
                Spacer(Modifier.height(18.dp))

                if (s.tab == 0) {
                    G("// " + tr("SELECT QUEST", "GÖREV SEÇ"), GMuted, 12, false)
                    Spacer(Modifier.height(10.dp))
                    val mins = listOf(15, 30, 60, 90)
                    val cols = listOf(GPink, GCyan, GYellow, GGreen)
                    Row(modifier = Modifier.fillMaxWidth()) {
                        mins.forEachIndexed { i, m ->
                            val ok = s.fits(m)
                            ArcadeButton(
                                m,
                                if (ok) "+${s.gainFor(m)}" else "24${T("h")}",
                                ok, cols[i], Modifier.weight(1f)
                            ) { s.request(m) }
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .pixelFrame(GPanel, GCyan)
                            .padding(start = 12.dp, top = 6.dp, end = 16.dp, bottom = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            Modifier.size(22.dp).background(GYellow, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            G("$", GBg, 12)
                        }
                        Spacer(Modifier.width(8.dp))
                        G(tr("INSERT", "GİR") + " ▸", GMuted, 12)
                        Spacer(Modifier.width(8.dp))
                        BasicTextField(
                            value = s.input,
                            onValueChange = { v ->
                                s.input = v.filter { it.isDigit() }.take(4)
                                s.error = ""
                            },
                            singleLine = true,
                            textStyle = TextStyle(
                                color = GYellow, fontFamily = FontFamily.Monospace,
                                fontSize = 20.sp, fontWeight = FontWeight.Bold
                            ),
                            cursorBrush = SolidColor(GYellow),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f).padding(vertical = 10.dp),
                            decorationBox = { inner ->
                                Box {
                                    if (s.input.isEmpty()) G("5", GMuted.copy(alpha = 0.6f), 20)
                                    inner()
                                }
                            }
                        )
                        Box(
                            modifier = Modifier
                                .bounceClick { s.submitInput() }
                                .pixelFrame(GPink, GText)
                                .padding(start = 10.dp, top = 8.dp, end = 14.dp, bottom = 12.dp)
                        ) {
                            G(tr("START", "BAŞLA"), GText, 12)
                        }
                    }
                    Spacer(Modifier.height(6.dp))
                    G(
                        "// " + when {
                            s.error.isNotEmpty() -> s.error
                            typed != null && typed >= 1 && s.fits(typed) ->
                                "+${s.gainFor(typed)} XP  ·  $typed ${T("min")}"
                            typed != null && typed >= 1 -> T("A day only has 24 hours.")
                            else -> T("Type any amount, for example 5.")
                        },
                        if (s.error.isNotEmpty()) Danger else GMuted, 11, false
                    )

                    Spacer(Modifier.height(14.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .pixelFrame(Color(0xFF0A0518), GLine)
                            .padding(start = 12.dp, top = 12.dp, end = 16.dp, bottom = 16.dp)
                    ) {
                        G("// QUEST LOG", GMuted, 10, false)
                        Spacer(Modifier.height(6.dp))
                        if (lines.isEmpty()) {
                            Row {
                                G("▸ ", GCyan, 12)
                                G(tr("Awaiting your quest…", "Görev bekleniyor…"), GMuted, 12, false)
                            }
                        }
                        lines.takeLast(3).forEach { line ->
                            G(
                                "▸ $line",
                                when {
                                    line.startsWith("+") -> GYellow
                                    line.startsWith("COMBO") -> GPink
                                    else -> GGreen
                                },
                                13
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .pixelFrame(GPanel, GYellow)
                            .padding(start = 14.dp, top = 14.dp, end = 18.dp, bottom = 18.dp)
                    ) {
                        G(T("TODAY'S XP"), GMuted, 11, true, Modifier, 2.sp)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                Modifier.size(26.dp).background(GYellow, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                G("$", GBg, 14)
                            }
                            Spacer(Modifier.width(10.dp))
                            G("+$xpTodayShown", GYellow, 38)
                            Spacer(Modifier.width(6.dp))
                            G("XP", GMuted, 14, true, Modifier.padding(top = 12.dp))
                        }
                        Spacer(Modifier.height(10.dp))
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Column(Modifier.weight(1f)) {
                                G(T("TODAY"), GMuted, 10)
                                G(ftime(st.todaySec), GText, 14)
                            }
                            Column(Modifier.weight(1f)) {
                                G(T("TOTAL"), GMuted, 10)
                                G(ftime(st.totalSec), GText, 14)
                            }
                            Column(Modifier.weight(1f)) {
                                G(T("LOGS"), GMuted, 10)
                                G("${st.count}", GText, 14)
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .pixelFrame(GPanel, GCyan)
                            .padding(start = 14.dp, top = 14.dp, end = 18.dp, bottom = 18.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            G(T("LAST 7 DAYS"), GMuted, 11, true, Modifier, 2.sp)
                            G(ftime(st.weekTotal * 60), GCyan, 12)
                        }
                        Spacer(Modifier.height(12.dp))
                        PixelBars(st.week, weekLabels)
                    }

                    Spacer(Modifier.height(16.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .pixelFrame(GPanel, GPink)
                            .padding(start = 14.dp, top = 14.dp, end = 18.dp, bottom = 18.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            G(T("LAST 16 WEEKS"), GMuted, 11, true, Modifier, 2.sp)
                            G(daysText(st.activeDays), GPink, 12)
                        }
                        Spacer(Modifier.height(12.dp))
                        PixelHeat(st.heat)
                    }
                } else {
                    G(tr("TOP SCORES", "EN YÜKSEK SKORLAR") + "  ·  ${st.hist.size}", GMuted, 11, true, Modifier, 2.sp)
                    Spacer(Modifier.height(12.dp))
                    if (st.hist.isEmpty()) {
                        G(T("No logs yet. Log your first session in Overview."), GMuted, 13, false)
                    }
                    st.hist.sortedByDescending { it.gain }.take(15).forEachIndexed { i, e ->
                        val rc = when (i) {
                            0 -> Color(0xFFFFD54A)
                            1 -> Color(0xFFC0C7D6)
                            2 -> Color(0xFFD9904F)
                            else -> GMuted
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 10.dp)
                                .pixelFrame(GPanel, if (i < 3) rc else GLine)
                                .padding(start = 12.dp, top = 12.dp, end = 16.dp, bottom = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            G(if (i == 0) "♛" else "#${i + 1}", rc, 20, true, Modifier.width(42.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                G("+${e.gain} XP", GYellow, 16)
                                G("${ftime(e.minutes * 60)}  ·  ${fmt.format(Date(e.ts))}", GMuted, 11, false)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(40.dp))
            }

            if (dust.value < 1f) {
                BurstParticles(dust.value, GYellow, Modifier.align(Alignment.Center).size(300.dp))
            }
            PixelPop(
                "+${s.lastGain} XP", s.popId,
                Modifier.align(Alignment.TopCenter).statusBarsPadding().padding(top = 170.dp)
            )
            BannerHost(
                s,
                Modifier.align(Alignment.TopCenter).statusBarsPadding().padding(top = 8.dp),
                GPanel, GYellow, GText, mono = true
            )
            LevelUpHost(s, GamingSkin)
        }
    }

    @Composable
    override fun LevelUp(area: String, level: Int, gained: Int, onDismiss: () -> Unit) {
        val flash = remember { Animatable(1f) }
        val pop = remember { Animatable(0.2f) }
        val burst = remember { Animatable(0f) }
        val fall by rememberInfiniteTransition(label = "fall").animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(tween(4500, easing = LinearEasing)),
            label = "fallT"
        )
        val blink by rememberInfiniteTransition(label = "gblink").animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(tween(500), RepeatMode.Reverse),
            label = "gblinkT"
        )
        LaunchedEffect(Unit) {
            launch { flash.animateTo(0f, tween(450)) }
            launch { pop.animateTo(1f, spring(dampingRatio = 0.4f, stiffness = 300f)) }
            launch { burst.animateTo(1f, tween(1700, easing = FastOutSlowInEasing)) }
        }
        BackHandler { onDismiss() }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(GBg.copy(alpha = 0.97f))
                .clickable { onDismiss() }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                for (i in 0 until 36) {
                    val x = size.width * ((i * 0.6180339f) % 1f)
                    val sp = 0.6f + (i % 5) * 0.15f
                    val y = ((fall * sp + i * 0.071f) % 1f) * (size.height + 20.dp.toPx()) - 10.dp.toPx()
                    val col = when (i % 4) {
                        0 -> GPink
                        1 -> GCyan
                        2 -> GYellow
                        else -> GGreen
                    }
                    drawRect(col.copy(alpha = 0.9f), Offset(x, y), Size(7.dp.toPx(), 7.dp.toPx()))
                }
            }
            BurstParticles(burst.value, GYellow, Modifier.align(Alignment.Center).size(380.dp))
            Column(
                modifier = Modifier.align(Alignment.Center).scale(pop.value),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    if (gained > 1) "+$gained LEVELS!" else "LEVEL UP!",
                    style = TextStyle(
                        brush = Brush.horizontalGradient(listOf(GPink, GYellow, GCyan)),
                        fontSize = 38.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.ExtraBold
                    )
                )
                Spacer(Modifier.height(16.dp))
                Box(
                    modifier = Modifier.size(150.dp).pixelFrame(GPanel, GYellow),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        G(T("LV"), GPink, 14, true, Modifier, 3.sp)
                        Text(
                            "$level",
                            style = TextStyle(
                                brush = Brush.verticalGradient(listOf(GYellow, GPink)),
                                fontSize = 72.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.ExtraBold
                            )
                        )
                    }
                }
                Spacer(Modifier.height(14.dp))
                G(tr("PLAYER RANK", "OYUNCU RÜTBESİ"), GMuted, 11, true, Modifier, 2.sp)
                G(up(T(Ranks.title(level))), GYellow, 18)
                Spacer(Modifier.height(8.dp))
                G("★ NEW HIGH SCORE ★", GCyan.copy(alpha = if (blink > 0.5f) 1f else 0.3f), 13)
                G(A(area).uppercase(), GMuted, 12, false)
                Spacer(Modifier.height(22.dp))
                G(
                    "▸ " + T("Tap to continue"),
                    GText.copy(alpha = if (blink > 0.5f) 1f else 0.25f), 12
                )
            }
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color.White.copy(alpha = flash.value * 0.85f))
            )
        }
    }
}
