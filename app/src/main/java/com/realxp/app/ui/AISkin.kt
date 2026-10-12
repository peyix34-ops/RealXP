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
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.material.icons.outlined.ArrowUpward
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

private val AB = Color(0xFF05070F)
private val AB2 = Color(0xFF0A1024)
private val AP = Color(0xFF0E1530)
private val AL = Color(0xFF22305F)
private val ACy = Color(0xFF3CE0FF)
private val AVi = Color(0xFF8B5CFF)
private val AMg = Color(0xFFFF4FD8)
private val AT = Color(0xFFE6EEFF)
private val AM = Color(0xFF7F8DB5)
private val AGreen = Color(0xFF3DFFA0)
private val AMono = FontFamily.Monospace

private fun aiBrush(): Brush = Brush.horizontalGradient(listOf(ACy, AVi, AMg))

@Composable
private fun Ai(
    text: String,
    color: Color,
    size: Int,
    bold: Boolean = false,
    modifier: Modifier = Modifier,
    spacing: TextUnit = TextUnit.Unspecified,
    mono: Boolean = false
) {
    Text(
        text,
        color = color,
        fontSize = size.sp,
        fontFamily = if (mono) AMono else FontFamily.Default,
        fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
        letterSpacing = spacing,
        modifier = modifier
    )
}

@Composable
private fun GradText(text: String, size: Int, modifier: Modifier = Modifier, mono: Boolean = false) {
    Text(
        text,
        modifier = modifier,
        maxLines = 2,
        style = TextStyle(
            brush = aiBrush(),
            fontSize = size.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = if (mono) AMono else FontFamily.Default
        )
    )
}

@Composable
private fun PulseDot(color: Color) {
    val v by rememberInfiniteTransition(label = "pulse").animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse),
        label = "pulseV"
    )
    Box(Modifier.size(8.dp).background(color.copy(alpha = v), CircleShape))
}

@Composable
private fun NeuralNet(modifier: Modifier, boost: Float, layers: List<Int> = listOf(4, 5, 5, 3)) {
    val t by rememberInfiniteTransition(label = "nn").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(3200, easing = LinearEasing)),
        label = "nnT"
    )
    val tones = listOf(ACy, AVi, AVi, AMg)
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val padX = 14.dp.toPx()
        val padY = 12.dp.toPx()
        val cols = layers.size
        fun pos(l: Int, i: Int): Offset {
            val n = layers[l]
            val x = padX + (w - 2 * padX) * l / (cols - 1)
            val y = padY + (h - 2 * padY) * (i + 0.5f) / n
            return Offset(x, y)
        }
        for (l in 0 until cols - 1) {
            for (i in 0 until layers[l]) {
                for (j in 0 until layers[l + 1]) {
                    val a = pos(l, i)
                    val b = pos(l + 1, j)
                    drawLine(AL.copy(alpha = 0.40f + 0.3f * boost), a, b, 1f)
                    if ((i + j + l) % 2 == 0) {
                        val ph = (t + ((l * 7 + i * 3 + j * 5) % 10) / 10f) % 1f
                        val sx = a.x + (b.x - a.x) * ph
                        val sy = a.y + (b.y - a.y) * ph
                        drawCircle(
                            ACy.copy(alpha = (0.45f + 0.55f * boost).coerceAtMost(1f)),
                            2.2.dp.toPx(), Offset(sx, sy)
                        )
                    }
                }
            }
        }
        for (l in 0 until cols) {
            for (i in 0 until layers[l]) {
                val p = pos(l, i)
                val tone = tones[l.coerceAtMost(tones.size - 1)]
                val flick = (0.5f + 0.5f * kotlin.math.sin((t * 6.2831855f) + l + i)).coerceIn(0f, 1f)
                drawCircle(tone.copy(alpha = 0.12f + 0.12f * flick + 0.2f * boost), 9.dp.toPx(), p)
                drawCircle(AP, 4.6.dp.toPx(), p)
                drawCircle(tone, 4.6.dp.toPx(), p, style = Stroke(1.6.dp.toPx()))
            }
        }
    }
}

@Composable
private fun SegBar(p: Float, n: Int, h: Dp) {
    val lit = p.coerceIn(0f, 1f) * n
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(3.dp)) {
        for (i in 0 until n) {
            val f = (lit - i).coerceIn(0f, 1f)
            Box(
                Modifier
                    .weight(1f)
                    .height(h)
                    .background(AL.copy(alpha = 0.6f), RoundedCornerShape(3.dp))
            ) {
                if (f > 0f) {
                    Box(
                        Modifier
                            .fillMaxWidth(f)
                            .fillMaxHeight()
                            .background(
                                Brush.horizontalGradient(listOf(ACy, AVi)),
                                RoundedCornerShape(3.dp)
                            )
                    )
                }
            }
        }
    }
}

@Composable
private fun AGlass(title: String, trailing: String?, content: @Composable ColumnScope.() -> Unit) {
    val shape = RoundedCornerShape(16.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(AP.copy(alpha = 0.9f), shape)
            .border(1.dp, Brush.horizontalGradient(listOf(ACy.copy(alpha = 0.35f), AVi.copy(alpha = 0.35f))), shape)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Ai(title, AM, 11, true, spacing = 2.5.sp)
            if (trailing != null) Ai(trailing, ACy, 13, true, mono = true)
        }
        Spacer(Modifier.height(12.dp))
        content()
    }
}

@Composable
private fun PromptChip(m: Int, chip: String, enabled: Boolean, modifier: Modifier, onClick: () -> Unit) {
    val shape = RoundedCornerShape(14.dp)
    val a = if (enabled) 0.9f else 0.3f
    Row(
        modifier = modifier
            .alpha(if (enabled) 1f else 0.6f)
            .then(if (enabled) Modifier.bounceClick { onClick() } else Modifier)
            .background(AP, shape)
            .border(
                1.dp,
                Brush.horizontalGradient(listOf(ACy.copy(alpha = a), AVi.copy(alpha = a), AMg.copy(alpha = a))),
                shape
            )
            .padding(horizontal = 14.dp, vertical = 13.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Ai("$m${T("m")}", AT, 17, true)
        Box(
            Modifier
                .background(ACy.copy(alpha = 0.14f), RoundedCornerShape(6.dp))
                .padding(horizontal = 7.dp, vertical = 3.dp)
        ) {
            Ai(chip, ACy, 11, true, mono = true)
        }
    }
}

@Composable
private fun NeonChart(days: List<DayStat>, labels: List<String>) {
    val maxM = maxOf(60, days.maxOf { it.minutes })
    val prog = animatedProgress(1f, 1000)
    Column {
        Canvas(modifier = Modifier.fillMaxWidth().height(100.dp)) {
            val n = days.size
            val w = size.width
            val h = size.height
            val pts = days.mapIndexed { i, d ->
                Offset(
                    w * (i + 0.5f) / n,
                    h - 10.dp.toPx() - (h - 26.dp.toPx()) * (d.minutes.toFloat() / maxM) * prog
                )
            }
            val line = Path().apply {
                moveTo(pts[0].x, pts[0].y)
                for (i in 1 until n) {
                    val p0 = pts[i - 1]
                    val p1 = pts[i]
                    val cx = (p0.x + p1.x) / 2f
                    cubicTo(cx, p0.y, cx, p1.y, p1.x, p1.y)
                }
            }
            val fill = Path().apply {
                addPath(line)
                lineTo(pts.last().x, h)
                lineTo(pts.first().x, h)
                close()
            }
            drawPath(fill, Brush.verticalGradient(listOf(ACy.copy(alpha = 0.30f), Color.Transparent)))
            drawPath(line, ACy.copy(alpha = 0.18f), style = Stroke(8.dp.toPx(), cap = StrokeCap.Round))
            drawPath(line, aiBrush(), style = Stroke(2.5.dp.toPx(), cap = StrokeCap.Round))
            pts.forEachIndexed { i, p ->
                val today = days[i].isToday
                drawCircle(AB, if (today) 6.dp.toPx() else 4.dp.toPx(), p)
                drawCircle(if (today) AMg else ACy, if (today) 6.dp.toPx() else 4.dp.toPx(), p, style = Stroke(2.dp.toPx()))
            }
        }
        Spacer(Modifier.height(4.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            labels.forEachIndexed { i, l ->
                Ai(
                    l, if (days[i].isToday) ACy else AM, 11, days[i].isToday,
                    Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun NeonHeat(cols: List<List<Int?>>) {
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
                        0 -> AL.copy(alpha = 0.55f)
                        1 -> ACy.copy(alpha = 0.40f)
                        2 -> ACy
                        3 -> AVi
                        else -> AMg
                    }
                    drawRoundRect(
                        color = col.copy(alpha = col.alpha * t),
                        topLeft = Offset(w * (c + g), r * (c + g)),
                        size = Size(c, c),
                        cornerRadius = CornerRadius(3.dp.toPx())
                    )
                }
            }
        }
    }
}

object AISkin : ArenaSkin {

    @Composable
    override fun PickerTile(name: String, on: Boolean, modifier: Modifier, onClick: () -> Unit) {
        val border by animateColorAsState(if (on) ACy else AL, tween(200), label = "ab")
        val sc by animateFloatAsState(
            if (on) 1.05f else 1f,
            spring(dampingRatio = 0.5f, stiffness = 500f),
            label = "asc"
        )
        val shape = RoundedCornerShape(14.dp)
        Box(
            modifier = modifier
                .height(88.dp)
                .scale(sc)
                .clip(shape)
                .background(Brush.verticalGradient(listOf(AB2, AB)))
                .border(1.dp, border, shape)
                .clickable { onClick() }
        ) {
            NeuralNet(
                Modifier.align(Alignment.TopCenter).padding(top = 6.dp).size(width = 74.dp, height = 44.dp),
                0f, listOf(3, 3, 2)
            )
            Ai(
                A(name), ACy, 13, true,
                Modifier.align(Alignment.BottomCenter).padding(bottom = 7.dp)
            )
            if (on) {
                PickCheck(ACy, AB, Modifier.align(Alignment.TopEnd).padding(6.dp))
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
                .background(Brush.verticalGradient(listOf(AP, AB)))
                .border(
                    1.dp,
                    Brush.horizontalGradient(listOf(ACy.copy(alpha = 0.55f), AVi.copy(alpha = 0.55f))),
                    shape
                )
                .clickable { onClick() }
        ) {
            NeuralNet(
                Modifier.align(Alignment.TopEnd).padding(top = 6.dp, end = 6.dp).size(width = 92.dp, height = 56.dp),
                0f, listOf(3, 4, 3)
            )
            Row(
                modifier = Modifier.align(Alignment.TopStart).padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PulseDot(AGreen)
                Spacer(Modifier.width(6.dp))
                if (editing && canRemove) {
                    Icon(Icons.Outlined.Close, null, tint = Danger, modifier = Modifier.size(14.dp))
                } else {
                    Ai("${T("LV")} ${info.level}", ACy, 12, true, mono = true)
                }
            }
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Ai(A(name), AT, 15, true)
                Ai("model v${info.level}.0", AM, 11, false, mono = true)
                Spacer(Modifier.height(6.dp))
                SegBar(p, 10, 5.dp)
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
        val typed = s.input.toIntOrNull()
        val xpTodayShown = animatedCount(st.xpToday)
        val todayIdx = (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) + 5) % 7
        val weekLabels = (6 downTo 0).map { dayLetter(((todayIdx - it) % 7 + 7) % 7) }
        val fmt = remember(Lang.tr) {
            SimpleDateFormat("d MMM  HH:mm", if (Lang.tr) Locale("tr", "TR") else Locale.US)
        }
        val boost = remember { Animatable(0f) }
        val logLines = remember { mutableStateListOf<String>() }
        val loss = 0.05f + (1f - barP) * 0.9f

        LaunchedEffect(s.popId) {
            if (s.popId > 0) {
                launch {
                    boost.snapTo(1f)
                    boost.animateTo(0f, tween(2400, easing = FastOutSlowInEasing))
                }
                logLines.add("forward pass… ok")
                delay(320L)
                logLines.add("loss ↓ ${"%.2f".format(loss)}")
                delay(320L)
                logLines.add("weights updated  +${s.lastGain} xp")
                while (logLines.size > 3) logLines.removeAt(0)
            }
        }

        HonestyHost(s, AP, AT, AM, ACy)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(AB2, AB)))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(Modifier.height(10.dp))
                Ai("←  ${T("Back").removePrefix("←  ")}".let { if (T("←  Back").startsWith("←")) T("←  Back") else it }, AM, 14, false,
                    Modifier.clickable { onBack() }.padding(vertical = 8.dp))
                Spacer(Modifier.height(8.dp))

                val heroShape = RoundedCornerShape(22.dp)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(heroShape)
                        .background(Brush.verticalGradient(listOf(AP, AB2)))
                        .border(
                            1.dp,
                            Brush.horizontalGradient(listOf(ACy.copy(alpha = 0.7f), AVi.copy(alpha = 0.7f))),
                            heroShape
                        )
                ) {
                    Row(modifier = Modifier.fillMaxWidth().height(178.dp)) {
                        Column(modifier = Modifier.weight(0.46f).padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                PulseDot(AGreen)
                                Spacer(Modifier.width(6.dp))
                                Ai(
                                    if (Lang.tr) "EĞİTİLİYOR" else "TRAINING",
                                    ACy, 10, true, spacing = 2.sp
                                )
                            }
                            Spacer(Modifier.height(10.dp))
                            GradText(A(s.area), 20)
                            Spacer(Modifier.height(4.dp))
                            Ai("v${info.level}.0", ACy, 30, true, mono = true)
                            Ai(up(T(Ranks.title(info.level))), AM, 10, true, spacing = 2.sp)
                        }
                        Box(modifier = Modifier.weight(0.54f).fillMaxHeight()) {
                            NeuralNet(Modifier.fillMaxSize().padding(8.dp), boost.value)
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))
                AGlass(if (Lang.tr) "EĞİTİM" else "TRAINING", "${(barP * 100).toInt()}%") {
                    SegBar(barP, 14, 10.dp)
                    Spacer(Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Ai("loss ↓ ${"%.2f".format(loss)}", AGreen, 12, true, mono = true)
                        Ai("acc ${(barP * 100).toInt()}%", ACy, 12, true, mono = true)
                        Ai("${info.need - info.into} XP → ${T("LV")} ${info.level + 1}", AMg, 12, true, mono = true)
                    }
                }

                Spacer(Modifier.height(14.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    listOf(T("Overview"), T("History")).forEachIndexed { i, label ->
                        val on = s.tab == i
                        if (i > 0) Spacer(Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { s.tab = i }
                                .background(
                                    if (on) ACy.copy(alpha = 0.12f) else Color.Transparent,
                                    RoundedCornerShape(12.dp)
                                )
                                .border(1.dp, if (on) ACy else AL, RoundedCornerShape(12.dp))
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Ai(label, if (on) ACy else AM, 14, on)
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))

                if (s.tab == 0) {
                    Ai(T("LOG YOUR WORK"), AM, 11, true, spacing = 2.5.sp)
                    Spacer(Modifier.height(10.dp))
                    val mins = listOf(15, 30, 60, 90)
                    for (r in 0..1) {
                        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                            for (c in 0..1) {
                                val m = mins[r * 2 + c]
                                val ok = s.fits(m)
                                if (c > 0) Spacer(Modifier.width(8.dp))
                                PromptChip(
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
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .background(AP, RoundedCornerShape(26.dp))
                                .border(
                                    1.dp,
                                    Brush.horizontalGradient(listOf(ACy.copy(alpha = 0.8f), AVi.copy(alpha = 0.8f))),
                                    RoundedCornerShape(26.dp)
                                )
                                .padding(horizontal = 18.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Ai("›", ACy, 20, true)
                            Spacer(Modifier.width(8.dp))
                            BasicTextField(
                                value = s.input,
                                onValueChange = { v ->
                                    s.input = v.filter { it.isDigit() }.take(4)
                                    s.error = ""
                                },
                                singleLine = true,
                                textStyle = TextStyle(
                                    color = AT, fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold, fontFamily = AMono
                                ),
                                cursorBrush = SolidColor(ACy),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                                decorationBox = { inner ->
                                    Box {
                                        if (s.input.isEmpty()) Ai(T("Other (min)"), AM, 15)
                                        inner()
                                    }
                                }
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .bounceClick { s.submitInput() }
                                .size(52.dp)
                                .background(Brush.linearGradient(listOf(ACy, AVi)), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Outlined.ArrowUpward, null, tint = AB, modifier = Modifier.size(24.dp))
                        }
                    }
                    Spacer(Modifier.height(6.dp))
                    Ai(
                        when {
                            s.error.isNotEmpty() -> s.error
                            typed != null && typed >= 1 && s.fits(typed) ->
                                TF("+%d XP  ·  %d min", s.gainFor(typed), typed)
                            typed != null && typed >= 1 -> T("A day only has 24 hours.")
                            else -> T("Type any amount, for example 5.")
                        },
                        if (s.error.isNotEmpty()) Danger else AM, 12
                    )

                    Spacer(Modifier.height(10.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 70.dp)
                            .background(Color(0xFF070B1A), RoundedCornerShape(12.dp))
                            .border(1.dp, AL, RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Ai("// training log", AM, 10, false, mono = true)
                        Spacer(Modifier.height(4.dp))
                        if (logLines.isEmpty()) {
                            Ai("› waiting for input…", AM.copy(alpha = 0.7f), 12, false, mono = true)
                        }
                        logLines.takeLast(3).forEach { line ->
                            Ai(
                                "› $line",
                                when {
                                    line.startsWith("weights") -> AMg
                                    line.startsWith("loss") -> AGreen
                                    else -> ACy
                                },
                                12, false, mono = true
                            )
                        }
                    }

                    Spacer(Modifier.height(18.dp))
                    AGlass(T("TODAY'S XP"), null) {
                        Row(verticalAlignment = Alignment.Bottom) {
                            GradText("+$xpTodayShown", 40, mono = true)
                            Spacer(Modifier.width(6.dp))
                            Ai("XP", AM, 14, true, Modifier.padding(bottom = 8.dp))
                        }
                        Spacer(Modifier.height(12.dp))
                        Box(Modifier.fillMaxWidth().height(1.dp).background(AL))
                        Spacer(Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Column(Modifier.weight(1f)) {
                                Ai(T("TODAY"), AM, 10, true, spacing = 1.5.sp)
                                Ai(ftime(st.todaySec), AT, 15, true, mono = true)
                            }
                            Column(Modifier.weight(1f)) {
                                Ai(T("TOTAL"), AM, 10, true, spacing = 1.5.sp)
                                Ai(ftime(st.totalSec), AT, 15, true, mono = true)
                            }
                            Column(Modifier.weight(1f)) {
                                Ai(T("LOGS"), AM, 10, true, spacing = 1.5.sp)
                                Ai("${st.count}", AT, 15, true, mono = true)
                            }
                        }
                    }

                    Spacer(Modifier.height(14.dp))
                    AGlass(T("LAST 7 DAYS"), ftime(st.weekTotal * 60)) {
                        NeonChart(st.week, weekLabels)
                    }
                    Spacer(Modifier.height(14.dp))
                    AGlass(T("LAST 16 WEEKS"), daysText(st.activeDays)) {
                        NeonHeat(st.heat)
                    }
                } else {
                    Ai(
                        (if (Lang.tr) "EĞİTİM KOŞULARI" else "TRAINING RUNS") + "  ·  ${st.hist.size}",
                        AM, 11, true, spacing = 2.sp
                    )
                    Spacer(Modifier.height(12.dp))
                    if (st.hist.isEmpty()) {
                        Ai(T("No logs yet. Log your first session in Overview."), AM, 14)
                    }
                    st.hist.forEachIndexed { idx, e ->
                        val shape = RoundedCornerShape(14.dp)
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                                .background(AP, shape)
                                .border(1.dp, AL, shape)
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Ai("RUN #${st.hist.size - idx}", ACy, 12, true, mono = true)
                                Ai(fmt.format(Date(e.ts)), AM, 11)
                            }
                            Spacer(Modifier.height(6.dp))
                            SegBar((e.minutes / 120f).coerceIn(0.05f, 1f), 12, 5.dp)
                            Spacer(Modifier.height(6.dp))
                            Ai("${ftime(e.minutes * 60)}  ·  +${e.gain} XP", AT, 14, true, mono = true)
                        }
                    }
                }
                Spacer(Modifier.height(40.dp))
            }

            BannerHost(
                s,
                Modifier.align(Alignment.TopCenter).statusBarsPadding().padding(top = 8.dp),
                AP, ACy, AT
            )
            LevelUpHost(s, AISkin)
        }
    }

    @Composable
    override fun LevelUp(area: String, level: Int, gained: Int, onDismiss: () -> Unit) {
        val fade = remember { Animatable(0f) }
        val boost = remember { Animatable(0f) }
        val pop = remember { Animatable(0.5f) }
        val wave = remember { Animatable(0f) }
        val burst = remember { Animatable(0f) }
        LaunchedEffect(Unit) {
            launch { fade.animateTo(1f, tween(300)) }
            launch { boost.animateTo(1f, tween(1200)) }
            launch { pop.animateTo(1f, spring(dampingRatio = 0.5f, stiffness = 250f)) }
            launch { wave.animateTo(1f, tween(1500, easing = FastOutSlowInEasing)) }
            launch { burst.animateTo(1f, tween(1800, easing = FastOutSlowInEasing)) }
        }
        BackHandler { onDismiss() }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF05070F).copy(alpha = 0.94f * fade.value))
                .clickable { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            NeuralNet(
                Modifier.size(width = 330.dp, height = 320.dp).alpha(0.45f * fade.value),
                boost.value, listOf(4, 6, 6, 4)
            )
            Canvas(modifier = Modifier.size(340.dp)) {
                val maxR = size.minDimension / 2
                for (i in 0..2) {
                    val t = (wave.value - i * 0.2f).coerceIn(0f, 1f)
                    drawCircle(
                        color = (if (i % 2 == 0) ACy else AMg).copy(alpha = (1f - t) * 0.6f),
                        radius = maxR * t,
                        style = Stroke(2.dp.toPx())
                    )
                }
            }
            BurstParticles(burst.value, AMg, Modifier.size(380.dp))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.scale(pop.value).alpha(fade.value)
            ) {
                Ai(
                    if (gained > 1) "+$gained  ${T("LV")}" else "MODEL UPGRADED",
                    ACy, 13, true, spacing = 6.sp, mono = true
                )
                Spacer(Modifier.height(6.dp))
                GradText("v$level.0", 72, mono = true)
                Ai(up(T(Ranks.title(level))), AT, 16, true, spacing = 4.sp)
                Spacer(Modifier.height(6.dp))
                Ai(A(area), AM, 14)
                Spacer(Modifier.height(26.dp))
                Ai(T("Tap to continue"), AM, 12)
            }
        }
    }
}
