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
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.realxp.app.data.DayStat
import com.realxp.app.data.Levels
import com.realxp.app.data.Ranks
import com.realxp.app.data.Store
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private val MBg = Color(0xFF123830)
private val MBg2 = Color(0xFF0F2E27)
private val MBoard = Color(0xFF0B211C)
private val MWood = Color(0xFF9A6B3F)
private val MChalk = Color(0xFFEDEFE6)
private val MYellow = Color(0xFFFFD76A)
private val MPink = Color(0xFFFF9EC4)
private val MBlue = Color(0xFF8FD3FF)
private val MMuted = Color(0xFF8FB0A5)
private val MLine = Color(0xFF2E5A4E)

private fun tr(en: String, trk: String): String = if (Lang.tr) trk else en

@Composable
private fun Ch(
    text: String,
    color: Color,
    sz: Int,
    bold: Boolean = false,
    modifier: Modifier = Modifier,
    italic: Boolean = false,
    spacing: TextUnit = TextUnit.Unspecified
) {
    Text(
        text,
        color = color,
        fontSize = sz.sp,
        fontFamily = FontFamily.Serif,
        fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
        fontStyle = if (italic) FontStyle.Italic else FontStyle.Normal,
        letterSpacing = spacing,
        modifier = modifier
    )
}

private fun Modifier.chalkBorder(color: Color, radius: Dp, width: Dp = 1.5.dp, dash: Boolean = true): Modifier =
    this.drawBehind {
        drawRoundRect(
            color = color,
            cornerRadius = CornerRadius(radius.toPx()),
            style = Stroke(
                width = width.toPx(),
                pathEffect = if (dash) PathEffect.dashPathEffect(floatArrayOf(14f, 10f), 0f) else null
            )
        )
    }

@Composable
private fun MathBg(modifier: Modifier) {
    val t by rememberInfiniteTransition(label = "mbg").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(16000, easing = LinearEasing)),
        label = "mbgT"
    )
    val measurer = rememberTextMeasurer()
    val syms = remember { listOf("π", "∑", "√", "∫", "÷", "±", "∞", "θ", "Δ", "%", "=", "×", "x²", "a²") }
    Canvas(modifier = modifier) {
        val step = 28.dp.toPx()
        var gx = 0f
        while (gx < size.width) {
            drawLine(MChalk.copy(alpha = 0.035f), Offset(gx, 0f), Offset(gx, size.height), 1f)
            gx += step
        }
        var gy = 0f
        while (gy < size.height) {
            drawLine(MChalk.copy(alpha = 0.035f), Offset(0f, gy), Offset(size.width, gy), 1f)
            gy += step
        }
        syms.forEachIndexed { i, sym ->
            val px = size.width * ((i * 0.143f + 0.05f) % 0.92f)
            val py = size.height * ((i * 0.231f + t) % 1f)
            drawText(
                textMeasurer = measurer,
                text = sym,
                topLeft = Offset(px, py),
                style = TextStyle(
                    color = MChalk.copy(alpha = 0.07f + (i % 3) * 0.02f),
                    fontSize = (20 + (i % 4) * 8).sp,
                    fontFamily = FontFamily.Serif
                )
            )
        }
    }
}

@Composable
private fun ChalkWrite(text: String, color: Color, sz: Int, bold: Boolean, delayMs: Int, key: Any) {
    val prog = remember(key) { Animatable(0f) }
    LaunchedEffect(key) {
        prog.snapTo(0f)
        delay(delayMs.toLong())
        prog.animateTo(1f, tween(450, easing = LinearEasing))
    }
    Box(
        modifier = Modifier.drawWithContent {
            clipRect(right = size.width * prog.value) {
                this@drawWithContent.drawContent()
            }
        }
    ) {
        Ch(text, color, sz, bold)
    }
}

@Composable
private fun ChalkNum(n: Int, boxSize: Dp) {
    val prog = animatedProgress(1f, 1100)
    Box(modifier = Modifier.size(boxSize), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val pad = 4.dp.toPx()
            drawArc(
                color = MYellow,
                startAngle = -110f,
                sweepAngle = 350f * prog,
                useCenter = false,
                topLeft = Offset(pad, pad),
                size = Size(size.width - 2 * pad, size.height - 2 * pad),
                style = Stroke(3.dp.toPx(), cap = StrokeCap.Round)
            )
            drawArc(
                color = MYellow.copy(alpha = 0.5f),
                startAngle = -95f,
                sweepAngle = 335f * prog,
                useCenter = false,
                topLeft = Offset(pad + 3.dp.toPx(), pad + 1.dp.toPx()),
                size = Size(size.width - 2 * pad - 5.dp.toPx(), size.height - 2 * pad - 3.dp.toPx()),
                style = Stroke(1.8.dp.toPx(), cap = StrokeCap.Round)
            )
        }
        Ch("$n", MChalk, (boxSize.value * 0.42f).toInt(), true)
    }
}

@Composable
private fun NumberLine(p: Float, tall: Boolean) {
    Canvas(modifier = Modifier.fillMaxWidth().height(if (tall) 40.dp else 22.dp)) {
        val y = size.height * 0.55f
        val pad = 6.dp.toPx()
        val w = (size.width - 2 * pad) * 0.96f
        val chalk = MChalk.copy(alpha = 0.55f)
        drawLine(chalk, Offset(pad, y), Offset(size.width - pad, y), 2.dp.toPx(), StrokeCap.Round)
        val ax = size.width - pad
        drawLine(chalk, Offset(ax, y), Offset(ax - 6.dp.toPx(), y - 4.dp.toPx()), 2.dp.toPx(), StrokeCap.Round)
        drawLine(chalk, Offset(ax, y), Offset(ax - 6.dp.toPx(), y + 4.dp.toPx()), 2.dp.toPx(), StrokeCap.Round)
        for (i in 0..4) {
            val x = pad + w * i / 4f
            drawLine(chalk, Offset(x, y - 4.dp.toPx()), Offset(x, y + 4.dp.toPx()), 1.5.dp.toPx())
        }
        val px = pad + w * p.coerceIn(0f, 1f)
        if (p > 0.01f) {
            drawLine(MYellow, Offset(pad, y), Offset(px, y), 4.dp.toPx(), StrokeCap.Round)
        }
        drawCircle(MYellow.copy(alpha = 0.25f), 9.dp.toPx(), Offset(px, y))
        drawCircle(MYellow, 5.dp.toPx(), Offset(px, y))
        drawCircle(MBoard, 2.dp.toPx(), Offset(px, y))
    }
}

@Composable
private fun ChalkButton(m: Int, chip: String, enabled: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Column(
        modifier = modifier
            .alpha(if (enabled) 1f else 0.45f)
            .then(if (enabled) Modifier.bounceClick { onClick() } else Modifier)
            .background(MBoard.copy(alpha = 0.7f), RoundedCornerShape(14.dp))
            .chalkBorder(if (enabled) MChalk.copy(alpha = 0.75f) else MMuted, 14.dp)
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Ch("$m ${T("min")}", MChalk, 18, true)
        Spacer(Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Ch("→", MYellow, 14, true)
            Spacer(Modifier.width(6.dp))
            Ch(chip, MYellow, 15, true)
        }
    }
}

@Composable
private fun BoardCard(title: String, trailing: String?, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MBoard.copy(alpha = 0.78f), RoundedCornerShape(16.dp))
            .chalkBorder(MLine, 16.dp, 1.5.dp)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Ch(title, MMuted, 11, true, spacing = 2.sp)
            if (trailing != null) Ch(trailing, MYellow, 13, true)
        }
        Spacer(Modifier.height(12.dp))
        content()
    }
}

@Composable
private fun HatchChart(days: List<DayStat>, labels: List<String>) {
    val maxM = maxOf(60, days.maxOf { it.minutes })
    val fr = days.map { animatedProgress(it.minutes.toFloat() / maxM, 900) }
    Column {
        Canvas(modifier = Modifier.fillMaxWidth().height(110.dp)) {
            val n = days.size
            val slot = size.width / n
            val bw = slot * 0.52f
            val base = size.height - 2.dp.toPx()
            drawLine(MChalk.copy(alpha = 0.5f), Offset(0f, base), Offset(size.width, base), 2.dp.toPx(), StrokeCap.Round)
            for (i in 0 until n) {
                val h = 6.dp.toPx() + (size.height - 14.dp.toPx()) * fr[i]
                val x = i * slot + (slot - bw) / 2f
                val col = when {
                    days[i].isToday -> MYellow
                    days[i].minutes == 0 -> MLine
                    else -> MChalk.copy(alpha = 0.8f)
                }
                val top = base - h
                drawRect(col, Offset(x, top), Size(bw, h), style = Stroke(1.6.dp.toPx()))
                clipRect(left = x, top = top, right = x + bw, bottom = base) {
                    var k = -h
                    while (k < bw) {
                        drawLine(
                            col.copy(alpha = 0.55f),
                            Offset(x + k, base), Offset(x + k + h, top),
                            1.dp.toPx()
                        )
                        k += 6.dp.toPx()
                    }
                }
            }
        }
        Spacer(Modifier.height(4.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            labels.forEachIndexed { i, l ->
                Ch(l, if (days[i].isToday) MYellow else MMuted, 11, days[i].isToday, Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun DotHeat(cols: List<List<Int?>>) {
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
                    val col = when (lvl) {
                        0 -> MLine.copy(alpha = 0.7f)
                        1 -> MChalk.copy(alpha = 0.45f)
                        2 -> MChalk.copy(alpha = 0.8f)
                        3 -> MYellow.copy(alpha = 0.85f)
                        else -> MYellow
                    }
                    val rad = c / 2f * (0.26f + 0.2f * lvl) * t
                    drawCircle(col, rad, Offset(w * (c + g) + c / 2f, r * (c + g) + c / 2f))
                }
            }
        }
    }
}

@Composable
private fun ChalkFloat(text: String, id: Int, modifier: Modifier) {
    val prog = remember { Animatable(1f) }
    LaunchedEffect(id) {
        if (id > 0) {
            prog.snapTo(0f)
            prog.animateTo(1f, tween(1300, easing = FastOutSlowInEasing))
        }
    }
    val p = prog.value
    if (id > 0 && p < 1f) {
        Box(
            modifier = modifier
                .offset(y = (-70f * p).dp)
                .alpha(if (p < 0.6f) 1f else 1f - (p - 0.6f) / 0.4f)
                .size(92.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawArc(
                    color = MYellow,
                    startAngle = -100f,
                    sweepAngle = 350f * (p * 3f).coerceAtMost(1f),
                    useCenter = false,
                    topLeft = Offset(4.dp.toPx(), 4.dp.toPx()),
                    size = Size(size.width - 8.dp.toPx(), size.height - 8.dp.toPx()),
                    style = Stroke(3.dp.toPx(), cap = StrokeCap.Round)
                )
            }
            Ch(text, MYellow, 26, true)
        }
    }
}

object MathSkin : ArenaSkin {

    @Composable
    override fun PickerTile(name: String, on: Boolean, modifier: Modifier, onClick: () -> Unit) {
        val sc by animateFloatAsState(
            if (on) 1.05f else 1f,
            spring(dampingRatio = 0.5f, stiffness = 500f),
            label = "msc"
        )
        val shape = RoundedCornerShape(14.dp)
        Box(
            modifier = modifier
                .height(88.dp)
                .scale(sc)
                .clip(shape)
                .background(Brush.verticalGradient(listOf(MBg, MBoard)))
                .border(1.dp, if (on) MYellow else MWood.copy(alpha = 0.6f), shape)
                .clickable { onClick() }
        ) {
            Ch("∑", MChalk.copy(alpha = 0.12f), 34, true, Modifier.align(Alignment.TopStart).padding(start = 8.dp, top = 2.dp))
            Ch("√", MChalk.copy(alpha = 0.12f), 28, true, Modifier.align(Alignment.BottomEnd).padding(end = 8.dp, bottom = 18.dp))
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Ch("π", MYellow, 30, true)
                Spacer(Modifier.height(2.dp))
                Ch(A(name), MChalk, 13, true)
            }
            if (on) {
                PickCheck(MYellow, MBoard, Modifier.align(Alignment.TopEnd).padding(6.dp))
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
                .background(Brush.verticalGradient(listOf(MBg, MBoard)))
                .border(2.dp, MWood.copy(alpha = 0.8f), shape)
                .clickable { onClick() }
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Ch("π", MYellow, 24, true)
                    Spacer(Modifier.weight(1f))
                    if (editing && canRemove) {
                        Icon(Icons.Outlined.Close, null, tint = Danger, modifier = Modifier.size(16.dp))
                    } else {
                        Ch("${T("LV")} ${info.level}", MChalk, 13, true)
                    }
                }
                Ch(A(name), MChalk, 16, true)
                Ch(T(Ranks.title(info.level)), MBlue, 11, false, Modifier, true)
                Spacer(Modifier.weight(1f))
                NumberLine(p, false)
                Ch("${info.into}/${info.need}", MMuted, 10)
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
        val dust = remember { Animatable(1f) }
        LaunchedEffect(s.popId) {
            if (s.popId > 0) {
                dust.snapTo(0f)
                dust.animateTo(1f, tween(1000, easing = FastOutSlowInEasing))
            }
        }

        HonestyHost(s, MBoard, MChalk, MMuted, MYellow)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(MBg, MBoard)))
        ) {
            MathBg(Modifier.fillMaxSize())
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(Modifier.height(10.dp))
                Ch(T("←  Back"), MMuted, 15, false, Modifier.clickable { onBack() }.padding(vertical = 8.dp))
                Spacer(Modifier.height(8.dp))

                val heroShape = RoundedCornerShape(20.dp)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(heroShape)
                        .background(Brush.verticalGradient(listOf(MBg2, MBoard)))
                        .border(3.dp, MWood, heroShape)
                ) {
                    Canvas(modifier = Modifier.matchParentSize()) {
                        val path = Path()
                        var x = 0f
                        path.moveTo(0f, size.height)
                        while (x <= size.width) {
                            val u = x / size.width
                            path.lineTo(x, size.height - size.height * 0.95f * u * u)
                            x += 6f
                        }
                        drawPath(path, MChalk.copy(alpha = 0.08f), style = Stroke(2.dp.toPx()))
                    }
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            ChalkNum(info.level, 76.dp)
                            Spacer(Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Ch(A(s.area), MChalk, 28, true)
                                Ch(T(Ranks.title(info.level)), MBlue, 14, false, Modifier, true)
                                Ch("f(x) = x²", MMuted, 12, false, Modifier, true)
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        NumberLine(barP, true)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Ch("${info.into} / ${info.need} XP", MMuted, 12)
                            Ch("${info.need - info.into} XP → ${T("LV")} ${info.level + 1}", MYellow, 12, true)
                        }
                    }
                }

                Spacer(Modifier.height(14.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    listOf(T("Overview"), tr("Problem set", "Soru defteri")).forEachIndexed { i, label ->
                        val on = s.tab == i
                        Column(
                            modifier = Modifier.weight(1f).clickable { s.tab = i },
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Ch(label, if (on) MYellow else MMuted, 15, on, Modifier.padding(vertical = 10.dp))
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .height(2.dp)
                                    .background(if (on) MYellow else MLine)
                            )
                        }
                    }
                }
                Spacer(Modifier.height(18.dp))

                if (s.tab == 0) {
                    Ch("// ${T("Log your work").lowercase()}", MMuted, 12, false, Modifier, true)
                    Spacer(Modifier.height(8.dp))
                    val mins = listOf(15, 30, 60, 90)
                    for (r in 0..1) {
                        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)) {
                            for (c in 0..1) {
                                val m = mins[r * 2 + c]
                                val ok = s.fits(m)
                                if (c > 0) Spacer(Modifier.width(10.dp))
                                ChalkButton(
                                    m,
                                    if (ok) "+${s.gainFor(m)} XP" else "24${T("h")}",
                                    ok, Modifier.weight(1f)
                                ) { s.request(m) }
                            }
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MBoard.copy(alpha = 0.8f), RoundedCornerShape(12.dp))
                            .chalkBorder(MChalk.copy(alpha = 0.6f), 12.dp, 1.5.dp)
                            .padding(start = 14.dp, end = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Ch("x =", MYellow, 20, true)
                        Spacer(Modifier.width(10.dp))
                        BasicTextField(
                            value = s.input,
                            onValueChange = { v ->
                                s.input = v.filter { it.isDigit() }.take(4)
                                s.error = ""
                            },
                            singleLine = true,
                            textStyle = TextStyle(
                                color = MChalk, fontFamily = FontFamily.Serif,
                                fontSize = 20.sp, fontWeight = FontWeight.Bold
                            ),
                            cursorBrush = SolidColor(MYellow),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f).padding(vertical = 16.dp),
                            decorationBox = { inner ->
                                Box {
                                    if (s.input.isEmpty()) Ch("?", MMuted, 20, true)
                                    inner()
                                }
                            }
                        )
                        Box(
                            modifier = Modifier
                                .bounceClick { s.submitInput() }
                                .background(MYellow, RoundedCornerShape(8.dp))
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Ch(tr("SOLVE", "ÇÖZ"), MBoard, 13, true, spacing = 1.sp)
                        }
                    }
                    Spacer(Modifier.height(6.dp))
                    Ch(
                        "// " + when {
                            s.error.isNotEmpty() -> s.error
                            typed != null && typed >= 1 && s.fits(typed) ->
                                TF("+%d XP  ·  %d min", s.gainFor(typed), typed)
                            typed != null && typed >= 1 -> T("A day only has 24 hours.")
                            else -> T("Type any amount, for example 5.")
                        },
                        if (s.error.isNotEmpty()) Danger else MMuted, 12, false, Modifier, true
                    )

                    Spacer(Modifier.height(14.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 104.dp)
                            .background(MBoard.copy(alpha = 0.9f), RoundedCornerShape(12.dp))
                            .chalkBorder(MLine, 12.dp, 1.5.dp)
                            .padding(14.dp)
                    ) {
                        Ch("// board", MMuted, 10, false, Modifier, true)
                        Spacer(Modifier.height(6.dp))
                        if (s.popId == 0) {
                            Ch("x = ?", MChalk.copy(alpha = 0.5f), 22, true)
                        } else {
                            ChalkWrite("x = ${s.lastMin} ${T("min")}", MBlue, 15, false, 0, s.popId)
                            Spacer(Modifier.height(2.dp))
                            ChalkWrite("xp = f(x)", MChalk, 15, false, 350, s.popId)
                            Spacer(Modifier.height(2.dp))
                            ChalkWrite("= +${s.lastGain} XP  ✓", MYellow, 24, true, 700, s.popId)
                        }
                    }

                    Spacer(Modifier.height(18.dp))
                    BoardCard(T("TODAY'S XP"), null) {
                        Row(verticalAlignment = Alignment.Bottom) {
                            Ch("∑ xp = ", MMuted, 15, false, Modifier.padding(bottom = 6.dp), true)
                            Ch("+$xpTodayShown", MYellow, 40, true)
                        }
                        Spacer(Modifier.height(12.dp))
                        Box(Modifier.fillMaxWidth().height(1.dp).background(MLine))
                        Spacer(Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Column(Modifier.weight(1f)) {
                                Ch(T("TODAY"), MMuted, 10, true, spacing = 1.5.sp)
                                Ch(ftime(st.todaySec), MChalk, 16, true)
                            }
                            Column(Modifier.weight(1f)) {
                                Ch(T("TOTAL"), MMuted, 10, true, spacing = 1.5.sp)
                                Ch(ftime(st.totalSec), MChalk, 16, true)
                            }
                            Column(Modifier.weight(1f)) {
                                Ch(T("LOGS"), MMuted, 10, true, spacing = 1.5.sp)
                                Ch("${st.count}", MChalk, 16, true)
                            }
                        }
                    }

                    Spacer(Modifier.height(14.dp))
                    BoardCard(T("LAST 7 DAYS"), ftime(st.weekTotal * 60)) {
                        HatchChart(st.week, weekLabels)
                    }
                    Spacer(Modifier.height(14.dp))
                    BoardCard(T("LAST 16 WEEKS"), daysText(st.activeDays)) {
                        DotHeat(st.heat)
                    }
                } else {
                    Ch(
                        tr("PROBLEM SET", "SORU DEFTERİ") + "  ·  ${st.hist.size}",
                        MMuted, 11, true, spacing = 2.sp
                    )
                    Spacer(Modifier.height(12.dp))
                    if (st.hist.isEmpty()) {
                        Ch(T("No logs yet. Log your first session in Overview."), MMuted, 14, false, Modifier, true)
                    }
                    st.hist.forEachIndexed { idx, e ->
                        val grade = when {
                            e.minutes < 15 -> "C"
                            e.minutes < 30 -> "B"
                            e.minutes < 60 -> "A"
                            else -> "A+"
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 10.dp)
                                .background(MBoard.copy(alpha = 0.78f), RoundedCornerShape(12.dp))
                                .chalkBorder(MLine, 12.dp, 1.2.dp)
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Ch("${tr("Problem", "Soru")} ${st.hist.size - idx}", MYellow, 13, true)
                                Ch("${ftime(e.minutes * 60)}  →  +${e.gain} XP", MChalk, 15, true)
                                Ch(fmt.format(Date(e.ts)), MMuted, 11, false, Modifier, true)
                            }
                            Box(modifier = Modifier.size(46.dp), contentAlignment = Alignment.Center) {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    drawCircle(MPink, size.minDimension / 2f - 3.dp.toPx(), style = Stroke(2.dp.toPx()))
                                }
                                Ch(grade, MPink, 16, true, Modifier.rotate(-8f))
                            }
                        }
                    }
                }
                Spacer(Modifier.height(40.dp))
            }

            if (dust.value < 1f) {
                BurstParticles(dust.value, MChalk, Modifier.align(Alignment.Center).size(280.dp))
            }
            ChalkFloat(
                "+${s.lastGain}", s.popId,
                Modifier.align(Alignment.TopCenter).statusBarsPadding().padding(top = 170.dp)
            )
            BannerHost(
                s,
                Modifier.align(Alignment.TopCenter).statusBarsPadding().padding(top = 8.dp),
                MBoard, MYellow, MChalk
            )
            LevelUpHost(s, MathSkin)
        }
    }

    @Composable
    override fun LevelUp(area: String, level: Int, gained: Int, onDismiss: () -> Unit) {
        var step by remember { mutableStateOf(0) }
        val qed = remember { Animatable(0f) }
        LaunchedEffect(Unit) {
            delay(1500L)
            step = 1
            delay(900L)
            step = 2
            qed.animateTo(1f, spring(dampingRatio = 0.4f, stiffness = 400f))
        }
        BackHandler { onDismiss() }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xF20A1C17))
                .clickable { onDismiss() }
        ) {
            MathBg(Modifier.fillMaxSize())
            Column(
                modifier = Modifier.align(Alignment.Center).padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ChalkWrite("${T("LV")} ${level - gained}  +  $gained", MBlue, 22, false, 300, "a")
                Spacer(Modifier.height(6.dp))
                ChalkWrite("=  $level", MYellow, 52, true, 900, "b")
                Spacer(Modifier.height(10.dp))
                if (step >= 1) {
                    ChalkNum(level, 130.dp)
                    Ch(up(T(Ranks.title(level))), MBlue, 16, true, spacing = 3.sp)
                    Ch(A(area), MMuted, 14, false, Modifier, true)
                }
                if (step >= 2) {
                    Spacer(Modifier.height(10.dp))
                    Ch(
                        "Q.E.D.  ∎", MPink, 30, true,
                        Modifier.rotate(-8f).scale(qed.value)
                    )
                    Spacer(Modifier.height(16.dp))
                    Ch(T("Tap to continue"), MMuted, 12, false, Modifier, true)
                }
            }
            if (step >= 1) {
                val b = remember { Animatable(0f) }
                LaunchedEffect(Unit) { b.animateTo(1f, tween(1700, easing = FastOutSlowInEasing)) }
                BurstParticles(b.value, MYellow, Modifier.align(Alignment.Center).size(380.dp))
            }
        }
    }
}
