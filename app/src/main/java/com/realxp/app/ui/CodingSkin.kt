package com.realxp.app.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
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
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.realxp.app.data.Levels
import com.realxp.app.data.Ranks
import com.realxp.app.data.Store
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private val CBg = Color(0xFF070B0A)
private val CBg2 = Color(0xFF0C1412)
private val CPanel = Color(0xFF0E1816)
private val CLine = Color(0xFF1B3B33)
private val CGreen = Color(0xFF3DDC84)
private val CCyan = Color(0xFF4DD0E1)
private val CAmber = Color(0xFFFFB454)
private val CRed = Color(0xFFFF5F56)
private val CText = Color(0xFFCDEFE0)
private val CMuted = Color(0xFF6E9487)
private val Mono = FontFamily.Monospace

private fun codeTone(level: Int): Color = when {
    level < 10 -> CGreen
    level < 20 -> Color(0xFFD9904F)
    level < 30 -> Color(0xFFC9D3E6)
    level < 40 -> Color(0xFFFFC93C)
    else -> Color(0xFF7DF0FF)
}

private fun asciiBar(p: Float, n: Int = 20): String {
    val f = (p * n).toInt().coerceIn(0, n)
    return "█".repeat(f) + "░".repeat(n - f)
}

private fun dayAbbr(i: Int): String =
    if (Lang.tr) listOf("Pzt", "Sal", "Çar", "Per", "Cum", "Cmt", "Paz")[i]
    else listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")[i]

@Composable
private fun M(
    text: String,
    color: Color,
    size: Int,
    bold: Boolean = false,
    modifier: Modifier = Modifier,
    spacing: TextUnit = TextUnit.Unspecified
) {
    Text(
        text,
        color = color,
        fontSize = size.sp,
        fontFamily = Mono,
        fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
        letterSpacing = spacing,
        modifier = modifier
    )
}

@Composable
private fun Dot(c: Color) {
    Box(modifier = Modifier.padding(end = 4.dp).size(7.dp).background(c, CircleShape))
}

@Composable
private fun Blink(color: Color, size: Int) {
    val v by rememberInfiniteTransition(label = "blink").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(520), RepeatMode.Reverse),
        label = "blinkV"
    )
    M("█", color.copy(alpha = if (v > 0.5f) 1f else 0f), size)
}

@Composable
private fun Typed(text: String, color: Color, size: Int, key: Any = Unit, startDelay: Long = 0L, bold: Boolean = false) {
    var n by remember(key) { mutableStateOf(0) }
    LaunchedEffect(key) {
        n = 0
        delay(startDelay)
        for (i in 1..text.length) {
            n = i
            delay(38L)
        }
    }
    M(text.take(n), color, size, bold)
}

@Composable
private fun GridBg(modifier: Modifier) {
    Canvas(modifier = modifier) {
        val step = 22.dp.toPx()
        var x = 0f
        while (x < size.width) {
            drawLine(CGreen.copy(alpha = 0.05f), Offset(x, 0f), Offset(x, size.height), 1f)
            x += step
        }
        var y = 0f
        while (y < size.height) {
            drawLine(CGreen.copy(alpha = 0.05f), Offset(0f, y), Offset(size.width, y), 1f)
            y += step
        }
    }
}

@Composable
private fun RainBg(modifier: Modifier) {
    val t by rememberInfiniteTransition(label = "rain").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(9000, easing = LinearEasing)),
        label = "rainT"
    )
    Canvas(modifier = modifier) {
        val step = 22.dp.toPx()
        var gx = 0f
        while (gx < size.width) {
            drawLine(CGreen.copy(alpha = 0.04f), Offset(gx, 0f), Offset(gx, size.height), 1f)
            gx += step
        }
        val cols = (size.width / step).toInt()
        val span = size.height + 160.dp.toPx()
        for (i in 0 until cols) {
            val speed = 1 + (i % 3)
            val seed = ((i * 37) % 100) / 100f
            val head = ((t * speed + seed) % 1f) * span - 80.dp.toPx()
            for (k in 0 until 7) {
                val y = head - k * 14.dp.toPx()
                val a = (0.20f - k * 0.027f).coerceAtLeast(0f)
                drawRoundRect(
                    color = CGreen.copy(alpha = a),
                    topLeft = Offset(i * step + 6.dp.toPx(), y),
                    size = Size(6.dp.toPx(), 9.dp.toPx()),
                    cornerRadius = CornerRadius(2.dp.toPx())
                )
            }
        }
    }
}

@Composable
private fun TermIcon(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(width = 46.dp, height = 32.dp)
            .background(CBg, RoundedCornerShape(5.dp))
            .border(1.dp, CGreen.copy(alpha = 0.7f), RoundedCornerShape(5.dp))
    ) {
        Row(modifier = Modifier.padding(start = 5.dp, top = 5.dp)) {
            Box(Modifier.padding(end = 3.dp).size(4.dp).background(CRed, CircleShape))
            Box(Modifier.padding(end = 3.dp).size(4.dp).background(CAmber, CircleShape))
            Box(Modifier.size(4.dp).background(CGreen, CircleShape))
        }
        Row(
            modifier = Modifier.align(Alignment.BottomStart).padding(start = 6.dp, bottom = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            M(">", CGreen, 11, true)
            Blink(CGreen, 11)
        }
    }
}

@Composable
private fun KV(key: String, value: String, color: Color) {
    Row {
        M("${key.padEnd(6)}: ", CMuted, 12)
        M(value, color, 12, true)
    }
}

@Composable
private fun TermButton(label: String, chip: String, enabled: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Row(
        modifier = modifier
            .alpha(if (enabled) 1f else 0.45f)
            .then(if (enabled) Modifier.bounceClick { onClick() } else Modifier)
            .background(CPanel, RoundedCornerShape(10.dp))
            .border(1.dp, CGreen.copy(alpha = if (enabled) 0.5f else 0.2f), RoundedCornerShape(10.dp))
            .padding(horizontal = 12.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        M(label, CGreen, 15, true)
        Box(
            modifier = Modifier
                .background(CGreen.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            M(chip, CAmber, 11, true)
        }
    }
}

@Composable
private fun StatCell(label: String, value: String, modifier: Modifier) {
    Column(modifier = modifier) {
        M(label, CMuted, 10, false, Modifier, 1.sp)
        Spacer(Modifier.height(3.dp))
        M(value, CText, 15, true)
    }
}

@Composable
private fun TermFloat(text: String, id: Int, modifier: Modifier) {
    val prog = remember { Animatable(1f) }
    LaunchedEffect(id) {
        if (id > 0) {
            prog.snapTo(0f)
            prog.animateTo(1f, tween(1300, easing = FastOutSlowInEasing))
        }
    }
    val p = prog.value
    if (id > 0 && p < 1f) {
        M(
            text, CGreen, 24, true,
            modifier
                .offset { IntOffset(0, (-70.dp.toPx() * p).toInt()) }
                .alpha(if (p < 0.6f) 1f else 1f - (p - 0.6f) / 0.4f)
        )
    }
}

@Composable
private fun GreenHeat(cols: List<List<Int?>>) {
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
                        0 -> CLine.copy(alpha = 0.6f)
                        1 -> CGreen.copy(alpha = 0.25f)
                        2 -> CGreen.copy(alpha = 0.5f)
                        3 -> CGreen.copy(alpha = 0.75f)
                        else -> CGreen
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

object CodingSkin : ArenaSkin {

    @Composable
    override fun PickerTile(name: String, on: Boolean, modifier: Modifier, onClick: () -> Unit) {
        val border by animateColorAsState(if (on) CGreen else CLine, tween(200), label = "cb")
        val sc by animateFloatAsState(
            if (on) 1.05f else 1f,
            spring(dampingRatio = 0.5f, stiffness = 500f),
            label = "csc"
        )
        val shape = RoundedCornerShape(14.dp)
        Box(
            modifier = modifier
                .height(88.dp)
                .scale(sc)
                .clip(shape)
                .background(Brush.verticalGradient(listOf(CBg2, CBg)))
                .border(1.dp, border, shape)
                .clickable { onClick() }
        ) {
            GridBg(Modifier.matchParentSize())
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TermIcon()
                Spacer(Modifier.height(8.dp))
                M(A(name), CGreen, 13, true)
            }
            if (on) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .size(16.dp)
                        .background(CGreen, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    M("✓", CBg, 10, true)
                }
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
        val tone = codeTone(info.level)
        val p = animatedProgress(info.into.toFloat() / info.need, 900)
        val shape = RoundedCornerShape(18.dp)
        Box(
            modifier = modifier
                .height(126.dp)
                .clip(shape)
                .background(Brush.verticalGradient(listOf(CBg2, CBg)))
                .border(1.dp, if (info.level >= 10) tone.copy(alpha = 0.6f) else CLine, shape)
                .clickable { onClick() }
        ) {
            GridBg(Modifier.matchParentSize())
            Column(modifier = Modifier.padding(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Dot(CRed)
                    Dot(CAmber)
                    Dot(CGreen)
                    Spacer(Modifier.weight(1f))
                    if (editing && canRemove) {
                        M("[x]", Danger, 12, true)
                    } else {
                        M("${T("LV")} ${info.level}", tone, 12, true)
                    }
                }
                Spacer(Modifier.height(10.dp))
                M("> ${A(name).lowercase()}", CText, 13, true)
                M("rank: ${T(Ranks.title(info.level)).lowercase()}", CMuted, 10)
                Spacer(Modifier.weight(1f))
                M(asciiBar(p, 14), tone, 11)
                M("${info.into}/${info.need} xp", CMuted, 10)
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
        val tone = codeTone(info.level)
        val xpTodayShown = animatedCount(st.xpToday)
        val typed = s.input.toIntOrNull()
        val todayIdx = (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) + 5) % 7
        val fmt = remember { SimpleDateFormat("MMM d  HH:mm", Locale.US) }
        val console = remember { mutableStateListOf<String>() }

        LaunchedEffect(s.popId) {
            if (s.popId > 0) {
                console.add("$ ./log ${s.lastMin}m")
                delay(260L)
                console.add("compiling…")
                delay(420L)
                console.add("✔ tests passed")
                delay(300L)
                console.add("+${s.lastGain} xp committed")
                while (console.size > 8) console.removeAt(0)
            }
        }

        HonestyHost(s, CPanel, CText, CMuted, CGreen)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(CBg2, CBg)))
        ) {
            RainBg(Modifier.fillMaxSize())
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(modifier = Modifier.clickable { onBack() }.padding(6.dp)) {
                        Dot(CRed)
                        Dot(CAmber)
                        Dot(CGreen)
                    }
                    Spacer(Modifier.width(8.dp))
                    M("~/realxp/${s.area.lowercase()} — zsh", CMuted, 11)
                    Spacer(Modifier.weight(1f))
                    M("cd ..", CGreen, 12, true, Modifier.clickable { onBack() }.padding(8.dp))
                }

                Spacer(Modifier.height(10.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CPanel.copy(alpha = 0.92f), RoundedCornerShape(14.dp))
                        .border(1.dp, CLine, RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
                    Typed("$ neofetch --xp", CCyan, 12)
                    Spacer(Modifier.height(10.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TermIcon(Modifier.scale(1.25f))
                        Spacer(Modifier.width(18.dp))
                        Column {
                            KV("lang", A(s.area), CText)
                            KV("rank", up(T(Ranks.title(info.level))), tone)
                            KV("level", "${info.level}", tone)
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    M("xp [${asciiBar(barP, 18)}]", tone, 13, true)
                    Spacer(Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        M("${info.into}/${info.need}", CMuted, 11)
                        M("next: ${info.need - info.into} xp → ${T("LV")} ${info.level + 1}", CAmber, 11)
                    }
                }

                Spacer(Modifier.height(14.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    listOf(T("Overview").lowercase(), "git log").forEachIndexed { i, label ->
                        val on = s.tab == i
                        if (i > 0) Spacer(Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { s.tab = i }
                                .background(
                                    if (on) CGreen.copy(alpha = 0.12f) else Color.Transparent,
                                    RoundedCornerShape(8.dp)
                                )
                                .border(1.dp, if (on) CGreen else CLine, RoundedCornerShape(8.dp))
                                .padding(vertical = 9.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            M(if (on) "[ $label ]" else label, if (on) CGreen else CMuted, 13, on)
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))

                if (s.tab == 0) {
                    M("// ${T("Log your work").lowercase()}", CMuted, 12)
                    Spacer(Modifier.height(8.dp))
                    val mins = listOf(15, 30, 60, 90)
                    for (r in 0..1) {
                        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                            for (c in 0..1) {
                                val m = mins[r * 2 + c]
                                val ok = s.fits(m)
                                if (c > 0) Spacer(Modifier.width(8.dp))
                                TermButton(
                                    "$ $m${T("m")}",
                                    if (ok) "+${s.gainFor(m)}xp" else "24${T("h")}",
                                    ok, Modifier.weight(1f)
                                ) { s.request(m) }
                            }
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(CPanel, RoundedCornerShape(10.dp))
                            .border(1.dp, CLine, RoundedCornerShape(10.dp))
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        M("$ log --min ", CMuted, 13)
                        BasicTextField(
                            value = s.input,
                            onValueChange = { v ->
                                s.input = v.filter { it.isDigit() }.take(4)
                                s.error = ""
                            },
                            singleLine = true,
                            textStyle = TextStyle(
                                color = CGreen, fontFamily = Mono,
                                fontSize = 16.sp, fontWeight = FontWeight.Bold
                            ),
                            cursorBrush = SolidColor(CGreen),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f).padding(vertical = 14.dp),
                            decorationBox = { inner ->
                                Box {
                                    if (s.input.isEmpty()) M("5", CMuted.copy(alpha = 0.6f), 16)
                                    inner()
                                }
                            }
                        )
                        Box(
                            modifier = Modifier
                                .bounceClick { s.submitInput() }
                                .background(CGreen, RoundedCornerShape(6.dp))
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            M("RUN ⏎", CBg, 12, true)
                        }
                    }
                    Spacer(Modifier.height(6.dp))
                    M(
                        "// " + when {
                            s.error.isNotEmpty() -> s.error
                            typed != null && typed >= 1 && s.fits(typed) ->
                                "+${s.gainFor(typed)} xp  ·  $typed ${T("min")}"
                            typed != null && typed >= 1 -> T("A day only has 24 hours.")
                            else -> T("Type any amount, for example 5.")
                        },
                        if (s.error.isNotEmpty()) Danger else CMuted, 11
                    )

                    Spacer(Modifier.height(14.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 104.dp)
                            .background(Color(0xFF050807), RoundedCornerShape(10.dp))
                            .border(1.dp, CLine, RoundedCornerShape(10.dp))
                            .padding(12.dp)
                    ) {
                        M("// output", CMuted, 10)
                        Spacer(Modifier.height(4.dp))
                        if (console.isEmpty()) {
                            Row {
                                M("$ ", CCyan, 12)
                                Blink(CGreen, 12)
                            }
                        }
                        console.takeLast(7).forEach { line ->
                            M(
                                line,
                                when {
                                    line.startsWith("✔") -> CGreen
                                    line.startsWith("+") -> CAmber
                                    line.startsWith("$") -> CCyan
                                    else -> CMuted
                                },
                                12
                            )
                        }
                    }

                    Spacer(Modifier.height(18.dp))
                    M("// ${T("Today's XP").lowercase()}", CMuted, 12)
                    Row(verticalAlignment = Alignment.Bottom) {
                        M("+$xpTodayShown", CGreen, 36, true)
                        Spacer(Modifier.width(6.dp))
                        M("xp", CMuted, 14, false, Modifier.padding(bottom = 6.dp))
                    }
                    Spacer(Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(CPanel, RoundedCornerShape(10.dp))
                            .border(1.dp, CLine, RoundedCornerShape(10.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StatCell(T("TODAY"), ftime(st.todaySec), Modifier.weight(1f))
                        Box(Modifier.width(1.dp).height(30.dp).background(CLine))
                        Spacer(Modifier.width(12.dp))
                        StatCell(T("TOTAL"), ftime(st.totalSec), Modifier.weight(1f))
                        Box(Modifier.width(1.dp).height(30.dp).background(CLine))
                        Spacer(Modifier.width(12.dp))
                        StatCell(T("LOGS"), "${st.count}", Modifier.weight(1f))
                    }

                    Spacer(Modifier.height(18.dp))
                    M("// ${T("Last 7 days").lowercase()}  ·  ${ftime(st.weekTotal * 60)}", CMuted, 12)
                    Spacer(Modifier.height(6.dp))
                    val maxM = maxOf(60, st.week.maxOf { it.minutes })
                    st.week.forEachIndexed { i, d ->
                        val frac = animatedProgress(d.minutes.toFloat() / maxM, 700)
                        val wi = ((todayIdx - (6 - i)) % 7 + 7) % 7
                        M(
                            "${dayAbbr(wi)} ${asciiBar(frac, 14)} ${d.minutes}${T("m")}",
                            if (d.isToday) CGreen else CMuted, 12, d.isToday
                        )
                    }

                    Spacer(Modifier.height(18.dp))
                    M("// ${T("Last 16 weeks").lowercase()}  ·  ${daysText(st.activeDays)}", CMuted, 12)
                    Spacer(Modifier.height(8.dp))
                    GreenHeat(st.heat)
                } else {
                    M("$ git log --oneline  ·  ${st.hist.size}", CCyan, 12)
                    Spacer(Modifier.height(10.dp))
                    if (st.hist.isEmpty()) {
                        M("// ${T("No logs yet. Log your first session in Overview.")}", CMuted, 13)
                    }
                    st.hist.forEach { e ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                                .background(CPanel, RoundedCornerShape(10.dp))
                                .border(1.dp, CLine, RoundedCornerShape(10.dp))
                                .padding(12.dp)
                        ) {
                            Row {
                                M("commit ", CMuted, 12)
                                M(java.lang.Long.toHexString(e.ts).takeLast(7), CAmber, 12, true)
                            }
                            M(fmt.format(Date(e.ts)), CMuted, 11)
                            M("    +${e.gain} xp  ·  ${ftime(e.minutes * 60)}", CGreen, 13, true)
                        }
                    }
                }
                Spacer(Modifier.height(40.dp))
            }

            TermFloat(
                "+${s.lastGain} xp", s.popId,
                Modifier.align(Alignment.TopCenter).statusBarsPadding().padding(top = 150.dp)
            )
            BannerHost(
                s,
                Modifier.align(Alignment.TopCenter).statusBarsPadding().padding(top = 8.dp),
                CPanel, CGreen, CText, mono = true
            )
            LevelUpHost(s, CodingSkin)
        }
    }

    @Composable
    override fun LevelUp(area: String, level: Int, gained: Int, onDismiss: () -> Unit) {
        val tone = codeTone(level)
        var step by remember { mutableStateOf(0) }
        LaunchedEffect(Unit) {
            delay(900L)
            step = 1
            delay(500L)
            step = 2
            delay(600L)
            step = 3
            delay(500L)
            step = 4
        }
        BackHandler { onDismiss() }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xF2050807))
                .clickable { onDismiss() }
        ) {
            RainBg(Modifier.fillMaxSize())
            Column(
                modifier = Modifier.align(Alignment.Center).padding(24.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Typed("$ npm run level-up", CCyan, 14)
                Spacer(Modifier.height(6.dp))
                if (step >= 1) M("> compiling modules…", CMuted, 13)
                if (step >= 2) M("> ${asciiBar(1f, 20)} 100%", CGreen, 13)
                AnimatedVisibility(
                    visible = step >= 3,
                    enter = fadeIn(tween(300)) + scaleIn(spring(dampingRatio = 0.5f, stiffness = 300f), initialScale = 0.6f)
                ) {
                    Column {
                        Spacer(Modifier.height(18.dp))
                        M("BUILD SUCCESSFUL", CGreen, 18, true, Modifier, 2.sp)
                        Text(
                            "${T("LV")} $level",
                            style = TextStyle(
                                color = tone,
                                fontSize = 64.sp,
                                fontFamily = Mono,
                                fontWeight = FontWeight.ExtraBold,
                                shadow = Shadow(color = tone.copy(alpha = 0.7f), offset = Offset.Zero, blurRadius = 28f)
                            )
                        )
                        M(up(T(Ranks.title(level))), tone, 16, true, Modifier, 3.sp)
                        if (gained > 1) M("+$gained ${T("LV")}", CAmber, 14, true)
                    }
                }
                if (step >= 4) {
                    Spacer(Modifier.height(20.dp))
                    Row {
                        M("// ${T("Tap to continue")}", CMuted, 12)
                        Spacer(Modifier.width(6.dp))
                        Blink(CGreen, 12)
                    }
                }
            }
        }
    }
}
