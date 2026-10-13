package com.realxp.app.ui

import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.realxp.app.data.DayStat
import com.realxp.app.data.Levels
import com.realxp.app.data.Ranks
import com.realxp.app.data.Store
import com.realxp.app.data.Xp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.sin

private val FnBg = Color(0xFF070D0C)
private val FnBg2 = Color(0xFF0D1A17)
private val FnPanel = Color(0xFF101E1A)
private val FnLine = Color(0xFF1F3A33)
private val FnGreen = Color(0xFF1DDCA0)
private val FnGold = Color(0xFFE8C468)
private val FnGold2 = Color(0xFFB8892B)
private val FnRed = Color(0xFFFF5C6A)
private val FnText = Color(0xFFEAF6F1)
private val FnMuted = Color(0xFF7FA397)

private fun tr(en: String, trk: String): String = if (Lang.tr) trk else en

@Composable
private fun Fm(
    text: String,
    color: Color,
    sz: Int,
    bold: Boolean = false,
    modifier: Modifier = Modifier,
    spacing: TextUnit = TextUnit.Unspecified,
    mono: Boolean = false
) {
    Text(
        text,
        color = color,
        fontSize = sz.sp,
        fontFamily = if (mono) FontFamily.Monospace else FontFamily.Default,
        fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
        letterSpacing = spacing,
        modifier = modifier
    )
}

@Composable
private fun CoinStack(n: Int, boxW: Dp, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(boxW, boxW * 0.8f)) {
        val w = size.width
        val h = size.height
        val ch = w * 0.16f
        val rx = w * 0.34f
        val ry = w * 0.11f
        for (i in 0 until n.coerceAtLeast(1)) {
            val cy = h * 0.82f - i * ch * 0.9f
            val cx = w / 2f
            drawRect(FnGold2, Offset(cx - rx, cy), Size(2 * rx, ch * 0.55f))
            drawOval(FnGold2, Offset(cx - rx, cy + ch * 0.55f - ry), Size(2 * rx, 2 * ry))
            drawOval(FnGold, Offset(cx - rx, cy - ry), Size(2 * rx, 2 * ry))
            drawOval(
                FnGold2.copy(alpha = 0.6f),
                Offset(cx - rx * 0.6f, cy - ry * 0.6f), Size(rx * 1.2f, ry * 1.2f),
                style = Stroke(1.dp.toPx())
            )
        }
    }
}

@Composable
private fun ChipIcon() {
    Canvas(modifier = Modifier.size(width = 42.dp, height = 31.dp)) {
        val w = size.width
        val h = size.height
        drawRoundRect(
            Brush.linearGradient(listOf(FnGold, FnGold2)),
            Offset.Zero, size, CornerRadius(6.dp.toPx())
        )
        val line = FnBg.copy(alpha = 0.5f)
        drawLine(line, Offset(w * 0.34f, 0f), Offset(w * 0.34f, h), 1.dp.toPx())
        drawLine(line, Offset(w * 0.66f, 0f), Offset(w * 0.66f, h), 1.dp.toPx())
        drawLine(line, Offset(0f, h * 0.5f), Offset(w, h * 0.5f), 1.dp.toPx())
        drawRoundRect(
            line, Offset(w * 0.34f, h * 0.28f), Size(w * 0.32f, h * 0.44f),
            CornerRadius(3.dp.toPx()), style = Stroke(1.dp.toPx())
        )
    }
}

@Composable
private fun Spark(values: List<Int>, color: Color, modifier: Modifier) {
    val prog = animatedProgress(1f, 900)
    Canvas(modifier = modifier) {
        val n = values.size
        val mx = maxOf(30, values.maxOrNull() ?: 0).toFloat()
        val pts = values.mapIndexed { i, v ->
            Offset(
                size.width * i / (n - 1f),
                size.height - 3.dp.toPx() - (size.height - 6.dp.toPx()) * (v / mx) * prog
            )
        }
        val line = Path().apply {
            moveTo(pts[0].x, pts[0].y)
            for (i in 1 until n) lineTo(pts[i].x, pts[i].y)
        }
        val fill = Path().apply {
            addPath(line)
            lineTo(pts.last().x, size.height)
            lineTo(pts[0].x, size.height)
            close()
        }
        drawPath(fill, Brush.verticalGradient(listOf(color.copy(alpha = 0.32f), Color.Transparent)))
        drawPath(line, color, style = Stroke(2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
        drawCircle(color.copy(alpha = 0.3f), 7.dp.toPx(), pts.last())
        drawCircle(color, 3.dp.toPx(), pts.last())
    }
}

@Composable
private fun FnBgChart(modifier: Modifier) {
    val t by rememberInfiniteTransition(label = "fbg").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(9000, easing = LinearEasing)),
        label = "fbgT"
    )
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        for (k in 1..6) {
            drawLine(FnLine.copy(alpha = 0.35f), Offset(0f, h * k / 7f), Offset(w, h * k / 7f), 1f)
        }
        val path = Path()
        var x = 0f
        while (x <= w) {
            val u = x / w
            val y = h * 0.58f - sin(u * 7f + t * 6.2831855f) * h * 0.05f - u * h * 0.2f
            if (x == 0f) path.moveTo(x, y) else path.lineTo(x, y)
            x += 6f
        }
        drawPath(path, FnGreen.copy(alpha = 0.10f), style = Stroke(10.dp.toPx(), cap = StrokeCap.Round))
        drawPath(path, FnGreen.copy(alpha = 0.18f), style = Stroke(2.dp.toPx(), cap = StrokeCap.Round))
    }
}

@Composable
private fun Ticker(items: List<Pair<String, Color>>) {
    val t by rememberInfiniteTransition(label = "tick").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(14000, easing = LinearEasing)),
        label = "tickT"
    )
    var w by remember { mutableStateOf(1) }
    val shape = RoundedCornerShape(10.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(30.dp)
            .clip(shape)
            .background(FnPanel.copy(alpha = 0.9f))
            .border(1.dp, FnLine, shape)
            .clipToBounds()
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .wrapContentWidth(align = Alignment.Start, unbounded = true)
                .offset { IntOffset(-(t * w).toInt(), 0) }
        ) {
            repeat(3) { rep ->
                Row(modifier = if (rep == 0) Modifier.onSizeChanged { w = it.width } else Modifier) {
                    items.forEach { (txt, col) ->
                        Fm("$txt        ", col, 12, true, Modifier, TextUnit.Unspecified, true)
                    }
                }
            }
        }
    }
}

@Composable
private fun DepositChip(
    m: Int,
    chip: String,
    enabled: Boolean,
    coins: Int,
    modifier: Modifier,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(16.dp)
    Column(
        modifier = modifier
            .alpha(if (enabled) 1f else 0.5f)
            .then(if (enabled) Modifier.bounceClick { onClick() } else Modifier)
            .background(Brush.verticalGradient(listOf(FnPanel, FnBg2)), shape)
            .border(1.dp, FnGold.copy(alpha = 0.55f), shape)
            .padding(top = 8.dp, bottom = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CoinStack(coins, 40.dp)
        Fm("$m${T("m")}", FnText, 16, true)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Fm("▲", FnGreen, 9, true)
            Spacer(Modifier.width(3.dp))
            Fm(chip, FnGreen, 12, true)
        }
    }
}

@Composable
private fun Candles(days: List<DayStat>, labels: List<String>) {
    val maxM = maxOf(60, days.maxOf { it.minutes }).toFloat()
    val prog = animatedProgress(1f, 1000)
    Column {
        Canvas(modifier = Modifier.fillMaxWidth().height(120.dp)) {
            val n = days.size
            val slot = size.width / n
            val bw = slot * 0.46f
            val top0 = 4.dp.toPx()
            val h = size.height - 8.dp.toPx()
            fun y(m: Float): Float = top0 + h - h * (m / maxM) * prog
            for (k in 0..3) {
                val gy = top0 + h * k / 3f
                drawLine(FnLine.copy(alpha = 0.6f), Offset(0f, gy), Offset(size.width, gy), 1f)
            }
            for (i in 0 until n) {
                val close = days[i].minutes.toFloat()
                val open = if (i == 0) close else days[i - 1].minutes.toFloat()
                val up = close >= open
                val col = if (up) FnGreen else FnRed
                val hi = maxOf(open, close) + maxM * 0.06f
                val lo = maxOf(0f, minOf(open, close) - maxM * 0.04f)
                val cx = i * slot + slot / 2f
                drawLine(col, Offset(cx, y(hi)), Offset(cx, y(lo)), 1.5.dp.toPx())
                val bt = y(maxOf(open, close))
                val bb = y(minOf(open, close))
                val bh = maxOf(3.dp.toPx(), bb - bt)
                drawRect(col, Offset(cx - bw / 2f, bt), Size(bw, bh))
                if (days[i].isToday) {
                    drawRect(
                        FnGold,
                        Offset(cx - bw / 2f - 2.dp.toPx(), bt - 2.dp.toPx()),
                        Size(bw + 4.dp.toPx(), bh + 4.dp.toPx()),
                        style = Stroke(1.5.dp.toPx())
                    )
                }
            }
        }
        Spacer(Modifier.height(4.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            labels.forEachIndexed { i, l ->
                Fm(l, if (days[i].isToday) FnGold else FnMuted, 11, days[i].isToday, Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun GoldHeat(cols: List<List<Int?>>) {
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
                        0 -> FnLine.copy(alpha = 0.6f)
                        1 -> FnGreen.copy(alpha = 0.30f)
                        2 -> FnGreen.copy(alpha = 0.55f)
                        3 -> FnGreen.copy(alpha = 0.85f)
                        else -> FnGold
                    }
                    drawRoundRect(
                        col.copy(alpha = col.alpha * t),
                        Offset(w * (c + g), r * (c + g)), Size(c, c),
                        CornerRadius(3.dp.toPx())
                    )
                }
            }
        }
    }
}

@Composable
private fun CoinRain(id: Int, modifier: Modifier) {
    val p = remember { Animatable(1f) }
    LaunchedEffect(id) {
        if (id > 0) {
            p.snapTo(0f)
            p.animateTo(1f, tween(1500, easing = LinearEasing))
        }
    }
    if (id > 0 && p.value < 1f) {
        Canvas(modifier = modifier) {
            for (i in 0 until 9) {
                val q = ((p.value - i * 0.07f) / 0.55f).coerceIn(0f, 1f)
                if (q > 0f && q < 1f) {
                    val x = size.width * (0.15f + 0.7f * ((i * 0.37f) % 1f))
                    val y = -20.dp.toPx() + size.height * 0.9f * q * q
                    val a = 1f - q * q * q
                    drawCircle(FnGold.copy(alpha = a), 9.dp.toPx(), Offset(x, y))
                    drawCircle(FnGold2.copy(alpha = a), 9.dp.toPx(), Offset(x, y), style = Stroke(2.dp.toPx()))
                    drawCircle(FnGold2.copy(alpha = a), 3.dp.toPx(), Offset(x, y))
                }
            }
        }
    }
}

@Composable
private fun GainFloat(text: String, id: Int, modifier: Modifier) {
    val prog = remember { Animatable(1f) }
    LaunchedEffect(id) {
        if (id > 0) {
            prog.snapTo(0f)
            prog.animateTo(1f, tween(1300, easing = FastOutSlowInEasing))
        }
    }
    val p = prog.value
    if (id > 0 && p < 1f) {
        Row(
            modifier = modifier
                .offset(y = (-70f * p).dp)
                .alpha(if (p < 0.6f) 1f else 1f - (p - 0.6f) / 0.4f)
                .background(FnBg2.copy(alpha = 0.95f), RoundedCornerShape(50))
                .border(1.dp, FnGreen, RoundedCornerShape(50))
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Fm("▲", FnGreen, 14, true)
            Spacer(Modifier.width(6.dp))
            Fm(text, FnGreen, 22, true)
        }
    }
}

object FinanceSkin : ArenaSkin {

    @Composable
    override fun PickerTile(name: String, on: Boolean, modifier: Modifier, onClick: () -> Unit) {
        val sc by animateFloatAsState(
            if (on) 1.05f else 1f,
            spring(dampingRatio = 0.5f, stiffness = 500f),
            label = "fsc"
        )
        val shape = RoundedCornerShape(14.dp)
        Box(
            modifier = modifier
                .height(88.dp)
                .scale(sc)
                .clip(shape)
                .background(Brush.verticalGradient(listOf(FnBg2, FnBg)))
                .border(1.dp, if (on) FnGold else FnLine, shape)
                .clickable { onClick() }
        ) {
            Spark(
                listOf(2, 3, 2, 5, 4, 7, 9), FnGreen.copy(alpha = 0.55f),
                Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(34.dp)
            )
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CoinStack(3, 34.dp)
                Spacer(Modifier.height(2.dp))
                Fm(A(name), FnGold, 13, true)
            }
            if (on) {
                PickCheck(FnGreen, FnBg, Modifier.align(Alignment.TopEnd).padding(6.dp))
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
        val week = remember(tick) { store.week(name).map { it.minutes } }
        val shape = RoundedCornerShape(18.dp)
        Box(
            modifier = modifier
                .height(126.dp)
                .clip(shape)
                .background(Brush.linearGradient(listOf(Color(0xFF14251F), Color(0xFF0A1512), Color(0xFF1E1A0C))))
                .border(1.dp, FnGold.copy(alpha = 0.6f), shape)
                .clickable { onClick() }
        ) {
            Spark(
                week, FnGreen,
                Modifier.align(Alignment.CenterEnd).padding(end = 8.dp).size(width = 74.dp, height = 40.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                ChipIcon()
                if (editing && canRemove) {
                    Icon(Icons.Outlined.Close, null, tint = Danger, modifier = Modifier.size(14.dp))
                } else {
                    Fm("${T("LV")} ${info.level}", FnGold, 12, true)
                }
            }
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Fm(A(name), FnText, 15, true)
                Fm(T(Ranks.title(info.level)), FnGold, 11)
                Spacer(Modifier.height(6.dp))
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .background(FnLine, RoundedCornerShape(4.dp))
                ) {
                    Box(
                        Modifier
                            .fillMaxWidth(p.coerceIn(0.03f, 1f))
                            .fillMaxHeight()
                            .background(Brush.horizontalGradient(listOf(FnGold2, FnGold)), RoundedCornerShape(4.dp))
                    )
                }
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
        val balance = animatedCount(s.xp)
        val typed = s.input.toIntOrNull()
        val todayIdx = (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) + 5) % 7
        val weekLabels = (6 downTo 0).map { dayLetter(((todayIdx - it) % 7 + 7) % 7) }
        val fmt = remember(Lang.tr) {
            SimpleDateFormat("d MMM yyyy  ·  HH:mm", if (Lang.tr) Locale("tr", "TR") else Locale.US)
        }
        val xpTodayShown = animatedCount(st.xpToday)
        val yesterdayXp = Xp.forMinutes(st.week[5].minutes.toDouble())
        val pct = if (yesterdayXp > 0) (st.xpToday - yesterdayXp) * 100 / yesterdayXp else 0
        val sweep by rememberInfiniteTransition(label = "shine").animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(tween(4200, easing = LinearEasing)),
            label = "shineT"
        )

        HonestyHost(s, FnPanel, FnText, FnMuted, FnGold)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(FnBg2, FnBg)))
        ) {
            FnBgChart(Modifier.fillMaxSize())
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(Modifier.height(10.dp))
                Fm(T("←  Back"), FnMuted, 14, false, Modifier.clickable { onBack() }.padding(vertical = 8.dp))
                Spacer(Modifier.height(4.dp))
                Ticker(
                    listOf(
                        "▲ ${T("TODAY")} +${st.xpToday} XP" to FnGreen,
                        "${A(s.area).uppercase()} ${T("LV")} ${info.level}" to FnGold,
                        "${st.count} ${T("LOGS")}" to FnMuted,
                        "▲ ${ftime(st.weekTotal * 60)}" to FnGreen
                    )
                )
                Spacer(Modifier.height(12.dp))

                val heroShape = RoundedCornerShape(22.dp)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(heroShape)
                        .background(Brush.linearGradient(listOf(Color(0xFF14251F), Color(0xFF0A1512), Color(0xFF211B0A))))
                        .border(
                            1.dp,
                            Brush.horizontalGradient(listOf(FnGold, FnGold2.copy(alpha = 0.4f), FnGold)),
                            heroShape
                        )
                ) {
                    Canvas(modifier = Modifier.matchParentSize()) {
                        val x = size.width * (sweep * 1.6f - 0.3f)
                        drawRect(
                            Brush.linearGradient(
                                listOf(Color.Transparent, Color.White.copy(alpha = 0.07f), Color.Transparent),
                                start = Offset(x - 90.dp.toPx(), 0f),
                                end = Offset(x + 90.dp.toPx(), size.height)
                            )
                        )
                    }
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ChipIcon()
                            Column(horizontalAlignment = Alignment.End) {
                                Fm(tr("TIER", "KADEME"), FnMuted, 10, true, Modifier, 2.sp)
                                Fm("${T("LV")} ${info.level}", FnGold, 20, true)
                            }
                        }
                        Spacer(Modifier.height(14.dp))
                        Fm(tr("PORTFOLIO", "PORTFÖY"), FnMuted, 11, true, Modifier, 3.sp)
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                "$balance",
                                style = TextStyle(
                                    brush = Brush.horizontalGradient(listOf(FnGold, Color(0xFFFFF0B0), FnGold2)),
                                    fontSize = 42.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            )
                            Spacer(Modifier.width(6.dp))
                            Fm("XP", FnMuted, 14, true, Modifier.padding(bottom = 9.dp))
                        }
                        Spacer(Modifier.height(8.dp))
                        Spark(
                            st.week.map { it.minutes }, FnGreen,
                            Modifier.fillMaxWidth().height(58.dp)
                        )
                        Spacer(Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Fm(up(T(Ranks.title(info.level))), FnGold, 11, true, Modifier, 2.sp)
                            Fm("${info.need - info.into} XP → ${T("LV")} ${info.level + 1}", FnMuted, 11)
                        }
                        Spacer(Modifier.height(6.dp))
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .height(5.dp)
                                .background(FnLine, RoundedCornerShape(5.dp))
                        ) {
                            Box(
                                Modifier
                                    .fillMaxWidth(barP.coerceIn(0.02f, 1f))
                                    .fillMaxHeight()
                                    .background(Brush.horizontalGradient(listOf(FnGold2, FnGold)), RoundedCornerShape(5.dp))
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    listOf(T("Overview"), tr("Ledger", "Hareketler")).forEachIndexed { i, label ->
                        val on = s.tab == i
                        Column(
                            modifier = Modifier.weight(1f).clickable { s.tab = i },
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Fm(label, if (on) FnGold else FnMuted, 14, on, Modifier.padding(vertical = 10.dp))
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .height(2.dp)
                                    .background(if (on) FnGold else FnLine)
                            )
                        }
                    }
                }
                Spacer(Modifier.height(18.dp))

                if (s.tab == 0) {
                    Fm(tr("DEPOSIT", "PARA GİRİŞİ"), FnMuted, 11, true, Modifier, 2.5.sp)
                    Spacer(Modifier.height(10.dp))
                    val mins = listOf(15, 30, 60, 90)
                    Row(modifier = Modifier.fillMaxWidth()) {
                        mins.forEachIndexed { i, m ->
                            if (i > 0) Spacer(Modifier.width(8.dp))
                            val ok = s.fits(m)
                            DepositChip(
                                m,
                                if (ok) "+${s.gainFor(m)}" else "24${T("h")}",
                                ok, i + 1, Modifier.weight(1f)
                            ) { s.request(m) }
                        }
                    }

                    Spacer(Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .background(FnPanel, RoundedCornerShape(16.dp))
                                .border(1.dp, FnGold.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Fm(T("MIN"), FnMuted, 11, true, Modifier, 2.sp)
                            Spacer(Modifier.width(10.dp))
                            BasicTextField(
                                value = s.input,
                                onValueChange = { v ->
                                    s.input = v.filter { it.isDigit() }.take(4)
                                    s.error = ""
                                },
                                singleLine = true,
                                textStyle = TextStyle(color = FnGold, fontSize = 22.sp, fontWeight = FontWeight.Bold),
                                cursorBrush = SolidColor(FnGold),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth().padding(vertical = 14.dp),
                                decorationBox = { inner ->
                                    Box {
                                        if (s.input.isEmpty()) Fm(T("Other (min)"), FnMuted, 16)
                                        inner()
                                    }
                                }
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .bounceClick { s.submitInput() }
                                .height(54.dp)
                                .background(Brush.horizontalGradient(listOf(FnGold, FnGold2)), RoundedCornerShape(16.dp))
                                .padding(horizontal = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Fm(tr("DEPOSIT", "YATIR"), FnBg, 13, true, Modifier, 1.sp)
                        }
                    }
                    Spacer(Modifier.height(6.dp))
                    Fm(
                        when {
                            s.error.isNotEmpty() -> s.error
                            typed != null && typed >= 1 && s.fits(typed) ->
                                TF("+%d XP  ·  %d min", s.gainFor(typed), typed)
                            typed != null && typed >= 1 -> T("A day only has 24 hours.")
                            else -> T("Type any amount, for example 5.")
                        },
                        if (s.error.isNotEmpty()) Danger else FnMuted, 12
                    )

                    Spacer(Modifier.height(18.dp))
                    val cardShape = RoundedCornerShape(18.dp)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(FnPanel.copy(alpha = 0.92f), cardShape)
                            .border(1.dp, FnLine, cardShape)
                            .padding(16.dp)
                    ) {
                        Fm(tr("TODAY'S RETURN", "BUGÜNKÜ GETİRİ"), FnMuted, 11, true, Modifier, 2.5.sp)
                        Spacer(Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Fm("▲", FnGreen, 20, true)
                            Spacer(Modifier.width(6.dp))
                            Fm("+$xpTodayShown", FnGreen, 38, true)
                            Spacer(Modifier.width(6.dp))
                            Fm("XP", FnMuted, 14, true, Modifier.padding(top = 12.dp))
                            Spacer(Modifier.weight(1f))
                            if (yesterdayXp > 0) {
                                val up = pct >= 0
                                Box(
                                    Modifier
                                        .background((if (up) FnGreen else FnRed).copy(alpha = 0.15f), RoundedCornerShape(50))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Fm(
                                        (if (up) "▲ " else "▼ ") + pctText(kotlin.math.abs(pct)),
                                        if (up) FnGreen else FnRed, 12, true
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        Box(Modifier.fillMaxWidth().height(1.dp).background(FnLine))
                        Spacer(Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Column(Modifier.weight(1f)) {
                                Fm(T("TODAY"), FnMuted, 10, true, Modifier, 1.5.sp)
                                Fm(ftime(st.todaySec), FnText, 15, true)
                            }
                            Column(Modifier.weight(1f)) {
                                Fm(T("TOTAL"), FnMuted, 10, true, Modifier, 1.5.sp)
                                Fm(ftime(st.totalSec), FnText, 15, true)
                            }
                            Column(Modifier.weight(1f)) {
                                Fm(T("LOGS"), FnMuted, 10, true, Modifier, 1.5.sp)
                                Fm("${st.count}", FnText, 15, true)
                            }
                        }
                    }

                    Spacer(Modifier.height(14.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(FnPanel.copy(alpha = 0.92f), cardShape)
                            .border(1.dp, FnLine, cardShape)
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Fm(T("LAST 7 DAYS"), FnMuted, 11, true, Modifier, 2.5.sp)
                            Fm(ftime(st.weekTotal * 60), FnGold, 13, true)
                        }
                        Spacer(Modifier.height(12.dp))
                        Candles(st.week, weekLabels)
                    }

                    Spacer(Modifier.height(14.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(FnPanel.copy(alpha = 0.92f), cardShape)
                            .border(1.dp, FnLine, cardShape)
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Fm(T("LAST 16 WEEKS"), FnMuted, 11, true, Modifier, 2.5.sp)
                            Fm(daysText(st.activeDays), FnGold, 13, true)
                        }
                        Spacer(Modifier.height(12.dp))
                        GoldHeat(st.heat)
                    }
                } else {
                    Fm(
                        tr("TRANSACTIONS", "HESAP HAREKETLERİ") + "  ·  ${st.hist.size}",
                        FnMuted, 11, true, Modifier, 2.sp
                    )
                    Spacer(Modifier.height(12.dp))
                    if (st.hist.isEmpty()) {
                        Fm(T("No logs yet. Log your first session in Overview."), FnMuted, 14)
                    }
                    var bal = s.xp
                    st.hist.forEach { e ->
                        val after = bal
                        bal -= e.gain
                        val shape = RoundedCornerShape(14.dp)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                                .background(FnPanel, shape)
                                .border(1.dp, FnLine, shape)
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .background(FnGreen.copy(alpha = 0.14f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Fm("↑", FnGreen, 18, true)
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Fm(tr("Deposit", "Para girişi") + "  ·  " + ftime(e.minutes * 60), FnText, 14, true)
                                Fm(fmt.format(Date(e.ts)), FnMuted, 11)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Fm("+${e.gain} XP", FnGreen, 15, true)
                                Fm("Bal. $after", FnMuted, 11, false, Modifier, TextUnit.Unspecified, true)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(40.dp))
            }

            CoinRain(s.popId, Modifier.align(Alignment.TopCenter).fillMaxWidth().height(300.dp).statusBarsPadding())
            GainFloat(
                "+${s.lastGain} XP", s.popId,
                Modifier.align(Alignment.TopCenter).statusBarsPadding().padding(top = 190.dp)
            )
            BannerHost(
                s,
                Modifier.align(Alignment.TopCenter).statusBarsPadding().padding(top = 8.dp),
                FnPanel, FnGold, FnText
            )
            LevelUpHost(s, FinanceSkin)
        }
    }

    @Composable
    override fun LevelUp(area: String, level: Int, gained: Int, onDismiss: () -> Unit) {
        val line = remember { Animatable(0f) }
        val flip = remember { Animatable(0f) }
        val burst = remember { Animatable(0f) }
        val fade = remember { Animatable(0f) }
        LaunchedEffect(Unit) {
            launch { fade.animateTo(1f, tween(300)) }
            line.animateTo(1f, tween(1100, easing = FastOutSlowInEasing))
            launch { flip.animateTo(1f, spring(dampingRatio = 0.55f, stiffness = 220f)) }
            launch { burst.animateTo(1f, tween(1800, easing = FastOutSlowInEasing)) }
        }
        BackHandler { onDismiss() }
        val ys = listOf(0.9f, 0.8f, 0.84f, 0.7f, 0.74f, 0.6f, 0.64f, 0.5f, 0.52f, 0.38f, 0.3f, 0.15f)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(FnBg.copy(alpha = 0.96f * fade.value))
                .clickable { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val n = ys.size
                val upto = line.value * (n - 1)
                val path = Path()
                path.moveTo(0f, size.height * ys[0])
                for (i in 0 until n - 1) {
                    val frac = (upto - i).coerceIn(0f, 1f)
                    if (frac <= 0f) break
                    val x0 = size.width * i / (n - 1f)
                    val x1 = size.width * (i + 1) / (n - 1f)
                    val y0 = size.height * ys[i]
                    val y1 = size.height * ys[i + 1]
                    path.lineTo(x0 + (x1 - x0) * frac, y0 + (y1 - y0) * frac)
                }
                drawPath(path, FnGreen.copy(alpha = 0.18f), style = Stroke(12.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
                drawPath(path, FnGreen, style = Stroke(3.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
            }
            BurstParticles(burst.value, FnGold, Modifier.size(400.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Fm(
                    if (gained > 1) "+$gained  ${T("LV")}" else "PORTFOLIO UPGRADED",
                    FnGreen, 13, true, Modifier.alpha(fade.value), 4.sp
                )
                Spacer(Modifier.height(18.dp))
                val cardShape = RoundedCornerShape(22.dp)
                Box(
                    modifier = Modifier
                        .size(width = 290.dp, height = 176.dp)
                        .scale(flip.value.coerceAtLeast(0.02f), 1f)
                        .clip(cardShape)
                        .background(Brush.linearGradient(listOf(Color(0xFF14251F), Color(0xFF0A1512), Color(0xFF2A210A))))
                        .border(
                            1.5.dp,
                            Brush.horizontalGradient(listOf(FnGold, FnGold2, FnGold)),
                            cardShape
                        )
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ChipIcon()
                            Fm(A(area).uppercase(), FnMuted, 11, true, Modifier, 2.sp)
                        }
                        Spacer(Modifier.weight(1f))
                        Fm(up(T(Ranks.title(level))), FnGold, 13, true, Modifier, 3.sp)
                        Text(
                            "${T("LV")} $level",
                            style = TextStyle(
                                brush = Brush.horizontalGradient(listOf(FnGold, Color(0xFFFFF0B0), FnGold2)),
                                fontSize = 46.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        )
                    }
                }
                Spacer(Modifier.height(24.dp))
                Fm(T("Tap to continue"), FnMuted, 12)
            }
        }
    }
}
