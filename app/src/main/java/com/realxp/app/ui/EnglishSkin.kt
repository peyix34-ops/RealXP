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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.realxp.app.data.LogEntry
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
import kotlin.math.sqrt

private val EBlue: Color
    get() = if (RX.dark) Color(0xFF6C9BFF) else Color(0xFF2F6BFF)
private val ESky: Color
    get() = if (RX.dark) Color(0xFF0F1D3F) else Color(0xFFE3EEFF)
private val ECoral = Color(0xFFFF6B6B)
private val EGreen = Color(0xFF2FBF8F)

private fun bubble(tailLeft: Boolean) = RoundedCornerShape(
    topStart = 18.dp,
    topEnd = 18.dp,
    bottomEnd = if (tailLeft) 18.dp else 4.dp,
    bottomStart = if (tailLeft) 4.dp else 18.dp
)

private fun praise(ts: Long): String {
    val en = listOf("Great job!", "Nice work!", "Keep it up!", "Well done!")
    val tr = listOf("Harika!", "Güzel iş!", "Böyle devam!", "Aferin!")
    val i = (ts % 4).toInt()
    return if (Lang.tr) tr[i] else en[i]
}

@Composable
private fun Tx(
    text: String,
    color: Color,
    size: Int,
    bold: Boolean = false,
    modifier: Modifier = Modifier
) {
    Text(
        text,
        color = color,
        fontSize = size.sp,
        fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
        modifier = modifier
    )
}

@Composable
private fun Globe(boxSize: Dp, color: Color, modifier: Modifier = Modifier) {
    val t by rememberInfiniteTransition(label = "globe").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(9000, easing = LinearEasing)),
        label = "globeT"
    )
    Canvas(modifier = modifier.size(boxSize)) {
        val r = this.size.minDimension / 2f * 0.92f
        val c = Offset(this.size.width / 2f, this.size.height / 2f)
        drawCircle(color.copy(alpha = 0.10f), r, c)
        drawCircle(color, r, c, style = Stroke(1.6.dp.toPx()))
        for (i in 0 until 3) {
            val phase = (t + i / 3f) * 3.1415927f
            val w = r * abs(cos(phase))
            drawOval(
                color = color.copy(alpha = 0.55f),
                topLeft = Offset(c.x - w, c.y - r),
                size = Size(2f * w, 2f * r),
                style = Stroke(1.2.dp.toPx())
            )
        }
        for (k in listOf(-0.55f, 0f, 0.55f)) {
            val y = c.y + r * k
            val half = sqrt(r * r - (r * k) * (r * k))
            drawLine(color.copy(alpha = 0.45f), Offset(c.x - half, y), Offset(c.x + half, y), 1.2.dp.toPx())
        }
    }
}

@Composable
private fun WordBubbles(modifier: Modifier) {
    val t by rememberInfiniteTransition(label = "words").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(5200, easing = LinearEasing)),
        label = "wordsT"
    )
    val words = if (Lang.tr) listOf("Merhaba!", "Harika!", "Aferin!") else listOf("Hello!", "Nice!", "Well done!")
    val xs = listOf(48f, -6f, 38f)
    val ys = listOf(6f, 56f, 104f)
    Box(modifier = modifier.size(width = 150.dp, height = 140.dp)) {
        words.forEachIndexed { i, w ->
            val ph = (t + i / 3f) % 1f
            val wave = sin(ph * 6.2831855f)
            Box(
                modifier = Modifier
                    .offset(x = xs[i].dp, y = (ys[i] + wave * 6f).dp)
                    .alpha(0.6f + 0.4f * (wave + 1f) / 2f)
                    .background(RX.Panel, bubble(i % 2 == 0))
                    .border(1.dp, EBlue.copy(alpha = 0.4f), bubble(i % 2 == 0))
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Tx(w, EBlue, 12, true)
            }
        }
    }
}

@Composable
private fun TypingDots() {
    val t by rememberInfiniteTransition(label = "typing").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(900, easing = LinearEasing)),
        label = "typingT"
    )
    Row(
        modifier = Modifier
            .background(RX.Line.copy(alpha = 0.7f), bubble(true))
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        for (i in 0..2) {
            val ph = (t + i * 0.2f) % 1f
            val up = sin(ph * 3.1415927f)
            Box(
                modifier = Modifier
                    .padding(horizontal = 2.5.dp)
                    .offset(y = (-4f * up).dp)
                    .size(7.dp)
                    .background(RX.Muted.copy(alpha = 0.5f + 0.5f * up), CircleShape)
            )
        }
    }
}

@Composable
private fun Bub(text: String, left: Boolean, bg: Color, fg: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (left) Arrangement.Start else Arrangement.End
    ) {
        Box(
            modifier = Modifier
                .background(bg, bubble(left))
                .padding(horizontal = 14.dp, vertical = 9.dp)
        ) {
            Tx(text, fg, 14, true)
        }
    }
}

@Composable
private fun ChatFeed(hist: List<LogEntry>, typing: Boolean) {
    val last = hist.take(2).reversed()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(RX.Panel.copy(alpha = 0.92f), RoundedCornerShape(20.dp))
            .border(1.dp, RX.Line, RoundedCornerShape(20.dp))
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(26.dp).background(EBlue, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Tx("E", Color.White, 13, true)
            }
            Spacer(Modifier.width(8.dp))
            Tx(if (Lang.tr) "Koç" else "Coach", RX.Text, 13, true)
        }
        Spacer(Modifier.height(10.dp))
        if (last.isEmpty()) {
            Bub(
                if (Lang.tr) "Merhaba! İlk çalışmanı kaydet, seni alkışlayayım." else "Hi! Log your first session and I'll cheer you on.",
                true, RX.Line.copy(alpha = 0.7f), RX.Text
            )
        }
        last.forEachIndexed { j, e ->
            Bub("${ftime(e.minutes * 60)} ✓", false, EBlue, Color.White)
            Spacer(Modifier.height(6.dp))
            if (typing && j == last.lastIndex) {
                Row { TypingDots() }
            } else {
                Bub("+${e.gain} XP · ${praise(e.ts)}", true, RX.Line.copy(alpha = 0.7f), RX.Text)
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun ChatBar(p: Float) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth().height(34.dp)) {
        val w = maxWidth
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .height(8.dp)
                .background(RX.Line, RoundedCornerShape(8.dp))
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth(p.coerceIn(0.03f, 1f))
                .height(8.dp)
                .background(Brush.horizontalGradient(listOf(EBlue, ECoral)), RoundedCornerShape(8.dp))
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = (w - 46.dp) * p.coerceIn(0f, 1f))
                .background(EBlue, bubble(true))
                .padding(horizontal = 7.dp, vertical = 2.dp)
        ) {
            Tx("${(p * 100).toInt()}%", Color.White, 10, true)
        }
    }
}

@Composable
private fun BubbleButton(
    m: Int,
    chip: String,
    enabled: Boolean,
    color: Color,
    tailLeft: Boolean,
    modifier: Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .alpha(if (enabled) 1f else 0.5f)
            .then(if (enabled) Modifier.bounceClick { onClick() } else Modifier)
            .background(Brush.horizontalGradient(listOf(color, color.copy(alpha = 0.82f))), bubble(tailLeft))
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Tx("$m${T("m")}", Color.White, 18, true)
        Tx(chip, Color.White.copy(alpha = 0.9f), 12, true)
    }
}

@Composable
private fun FlashCard(label: String, value: String, stripe: Color, modifier: Modifier) {
    val shape = RoundedCornerShape(14.dp)
    Column(
        modifier = modifier
            .clip(shape)
            .background(RX.Panel)
            .border(1.dp, RX.Line, shape)
    ) {
        Box(Modifier.fillMaxWidth().height(5.dp).background(stripe))
        Column(modifier = Modifier.padding(12.dp)) {
            Tx(label, RX.Muted, 10)
            Spacer(Modifier.height(4.dp))
            Tx(value, RX.Text, 15, true)
        }
    }
}

@Composable
private fun BubbleFloat(text: String, id: Int, modifier: Modifier) {
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
                .offset { IntOffset(0, (-70.dp.toPx() * p).toInt()) }
                .alpha(if (p < 0.6f) 1f else 1f - (p - 0.6f) / 0.4f)
                .background(ECoral, bubble(true))
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Tx(text, Color.White, 20, true)
        }
    }
}

object EnglishSkin : ArenaSkin {

    @Composable
    override fun PickerTile(name: String, on: Boolean, modifier: Modifier, onClick: () -> Unit) {
        val border by animateColorAsState(if (on) EBlue else RX.Line, tween(200), label = "cb")
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
                .background(Brush.verticalGradient(listOf(ESky, RX.Panel)))
                .border(1.dp, border, shape)
                .clickable { onClick() }
        ) {
            Globe(54.dp, EBlue, Modifier.align(Alignment.TopCenter).padding(top = 6.dp))
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 8.dp, end = 6.dp)
                    .background(EBlue, bubble(true))
                    .padding(horizontal = 5.dp, vertical = 1.dp)
            ) {
                Tx("ABC", Color.White, 8, true)
            }
            Tx(
                A(name), EBlue, 13, true,
                Modifier.align(Alignment.BottomCenter).padding(bottom = 6.dp)
            )
            if (on) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(6.dp)
                        .size(16.dp)
                        .background(EBlue, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Tx("✓", Color.White, 10, true)
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
        val info = com.realxp.app.data.Levels.of(xp)
        val tone = RankColors.tone(info.level)
        val shape = RoundedCornerShape(18.dp)
        val dots = ((info.into.toFloat() / info.need) * 10f).toInt().coerceIn(0, 10)
        Box(
            modifier = modifier
                .height(126.dp)
                .clip(shape)
                .background(Brush.verticalGradient(listOf(ESky, RX.Panel)))
                .border(
                    1.dp,
                    if (info.level >= 10) tone.copy(alpha = 0.55f) else EBlue.copy(alpha = 0.3f),
                    shape
                )
                .clickable { onClick() }
        ) {
            Globe(46.dp, EBlue, Modifier.align(Alignment.TopStart).padding(10.dp))
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp)
                    .background(
                        (if (editing && canRemove) Danger else tone).copy(alpha = 0.14f),
                        RoundedCornerShape(50)
                    )
                    .padding(horizontal = 9.dp, vertical = 3.dp)
            ) {
                if (editing && canRemove) {
                    Tx("✕", Danger, 12, true)
                } else {
                    Tx("${T("LV")} ${info.level}", tone, 12, true)
                }
            }
            Column(modifier = Modifier.align(Alignment.BottomStart).padding(12.dp)) {
                RankText(A(name), info.level, 15.sp, fontWeight = FontWeight.SemiBold)
                Tx(T(Ranks.title(info.level)), if (info.level >= 10) tone else RX.Muted, 11)
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    for (i in 0 until 10) {
                        Box(
                            modifier = Modifier
                                .padding(end = 3.dp)
                                .size(7.dp)
                                .background(if (i < dots) EBlue else RX.Line, CircleShape)
                        )
                    }
                }
            }
        }
    }

    @Composable
    override fun Screen(s: AreaState, onBack: () -> Unit) {
        val info = s.info
        val st = s.stats()
        val barP = animatedXpBar(info.level, info.into.toFloat() / info.need)
        val tone = RankColors.tone(info.level)
        val typed = s.input.toIntOrNull()
        val xpTodayShown = animatedCount(st.xpToday)
        val todayIdx = (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) + 5) % 7
        val weekLabels = (6 downTo 0).map { dayLetter(((todayIdx - it) % 7 + 7) % 7) }
        val fmt = remember(Lang.tr) {
            SimpleDateFormat("MMM d  ·  HH:mm", if (Lang.tr) Locale("tr", "TR") else Locale.US)
        }
        var typing by remember { mutableStateOf(false) }
        LaunchedEffect(s.popId) {
            if (s.popId > 0) {
                typing = true
                delay(1000L)
                typing = false
            }
        }

        HonestyHost(s, RX.Panel, RX.Text, RX.Muted, EBlue)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(ESky, RX.Bg)))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(Modifier.height(10.dp))
                Tx(
                    T("←  Back"), RX.Muted, 14, false,
                    Modifier.clickable { onBack() }.padding(vertical = 8.dp)
                )
                Spacer(Modifier.height(8.dp))

                val heroShape = RoundedCornerShape(24.dp)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(heroShape)
                        .background(Brush.horizontalGradient(listOf(EBlue.copy(alpha = 0.20f), ESky.copy(alpha = 0.6f))))
                        .border(1.dp, EBlue.copy(alpha = 0.3f), heroShape)
                ) {
                    Globe(112.dp, EBlue, Modifier.align(Alignment.CenterEnd).padding(end = 18.dp))
                    WordBubbles(Modifier.align(Alignment.TopEnd))
                    Column(modifier = Modifier.fillMaxWidth(0.62f).padding(18.dp)) {
                        RankText(A(s.area), info.level, 26.sp, fontWeight = FontWeight.Bold)
                        RankText(
                            up(T(Ranks.title(info.level))), info.level, 11.sp,
                            letterSpacing = 2.sp, fontWeight = FontWeight.Medium
                        )
                        Spacer(Modifier.height(8.dp))
                        LevelPill(info.level)
                        Spacer(Modifier.height(14.dp))
                        ChatBar(barP)
                        Spacer(Modifier.height(4.dp))
                        Tx("${info.into} / ${info.need} XP", RX.Muted, 12)
                        Tx(
                            "${info.need - info.into} XP → ${T("LV")} ${info.level + 1}",
                            EBlue, 12, true
                        )
                    }
                }

                Spacer(Modifier.height(14.dp))
                TabBar(s.tab) { s.tab = it }
                Spacer(Modifier.height(18.dp))

                if (s.tab == 0) {
                    ChatFeed(st.hist, typing)
                    Spacer(Modifier.height(18.dp))
                    Tx(T("LOG YOUR WORK"), RX.Muted, 11)
                    Spacer(Modifier.height(10.dp))
                    val mins = listOf(15, 30, 60, 90)
                    for (r in 0..1) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            for (c in 0..1) {
                                val m = mins[r * 2 + c]
                                val ok = s.fits(m)
                                if (c > 0) Spacer(Modifier.width(10.dp))
                                BubbleButton(
                                    m,
                                    if (ok) "+${s.gainFor(m)} XP" else "24${T("h")}",
                                    ok,
                                    if ((r + c) % 2 == 0) EBlue else ECoral,
                                    c == 0,
                                    Modifier.weight(1f)
                                ) { s.request(m) }
                            }
                        }
                        Spacer(Modifier.height(10.dp))
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .background(RX.Panel, RoundedCornerShape(26.dp))
                                .border(1.dp, RX.Line, RoundedCornerShape(26.dp))
                                .padding(horizontal = 20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            BasicTextField(
                                value = s.input,
                                onValueChange = { v ->
                                    s.input = v.filter { it.isDigit() }.take(4)
                                    s.error = ""
                                },
                                singleLine = true,
                                textStyle = TextStyle(color = RX.Text, fontSize = 16.sp, fontWeight = FontWeight.SemiBold),
                                cursorBrush = SolidColor(EBlue),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                                decorationBox = { inner ->
                                    Box {
                                        if (s.input.isEmpty()) Tx(T("Other (min)"), RX.Muted, 16)
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
                                .background(Brush.linearGradient(listOf(EBlue, ECoral)), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Tx("↑", Color.White, 22, true)
                        }
                    }
                    Spacer(Modifier.height(6.dp))
                    Tx(
                        when {
                            s.error.isNotEmpty() -> s.error
                            typed != null && typed >= 1 && s.fits(typed) ->
                                TF("+%d XP  ·  %d min", s.gainFor(typed), typed)
                            typed != null && typed >= 1 -> T("A day only has 24 hours.")
                            else -> T("Type any amount, for example 5.")
                        },
                        if (s.error.isNotEmpty()) Danger else RX.Muted, 12
                    )

                    Spacer(Modifier.height(20.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Tx(T("TODAY'S XP"), RX.Muted, 11)
                        Box(
                            modifier = Modifier
                                .background(ECoral, bubble(false))
                                .padding(horizontal = 18.dp, vertical = 8.dp)
                        ) {
                            Tx("+$xpTodayShown XP", Color.White, 24, true)
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        FlashCard(T("TODAY"), ftime(st.todaySec), EBlue, Modifier.weight(1f))
                        Spacer(Modifier.width(8.dp))
                        FlashCard(T("TOTAL"), ftime(st.totalSec), ECoral, Modifier.weight(1f))
                        Spacer(Modifier.width(8.dp))
                        FlashCard(T("LOGS"), "${st.count}", EGreen, Modifier.weight(1f))
                    }

                    Spacer(Modifier.height(14.dp))
                    SectionCard(T("LAST 7 DAYS"), ftime(st.weekTotal * 60)) {
                        WeekChart(st.week, EBlue, weekLabels)
                    }
                    Spacer(Modifier.height(14.dp))
                    SectionCard(T("LAST 16 WEEKS"), daysText(st.activeDays)) {
                        Heatmap(st.heat)
                        Spacer(Modifier.height(8.dp))
                        HeatLegend()
                    }
                } else {
                    Tx(TF("YOUR LOGS  ·  %d", st.hist.size), RX.Muted, 11)
                    Spacer(Modifier.height(12.dp))
                    if (st.hist.isEmpty()) {
                        Tx(T("No logs yet. Log your first session in Overview."), RX.Muted, 14)
                    }
                    st.hist.forEach { e ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                            Tx(fmt.format(Date(e.ts)), RX.Muted, 11)
                        }
                        Spacer(Modifier.height(4.dp))
                        Bub("${ftime(e.minutes * 60)} ✓", false, EBlue, Color.White)
                        Spacer(Modifier.height(4.dp))
                        Bub("+${e.gain} XP · ${praise(e.ts)}", true, RX.Line.copy(alpha = 0.7f), RX.Text)
                        Spacer(Modifier.height(12.dp))
                    }
                }
                Spacer(Modifier.height(32.dp))
            }

            BubbleFloat(
                s.popText, s.popId,
                Modifier.align(Alignment.TopCenter).statusBarsPadding().padding(top = 160.dp)
            )
            BannerHost(
                s,
                Modifier.align(Alignment.TopCenter).statusBarsPadding().padding(top = 8.dp),
                RX.Panel, EBlue, RX.Text
            )
            LevelUpHost(s, EnglishSkin)
        }
    }

    @Composable
    override fun LevelUp(area: String, level: Int, gained: Int, onDismiss: () -> Unit) {
        val pop = remember { Animatable(0.3f) }
        val fade = remember { Animatable(0f) }
        val burst = remember { Animatable(0f) }
        val t by rememberInfiniteTransition(label = "rise").animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(tween(4200, easing = LinearEasing)),
            label = "riseT"
        )
        LaunchedEffect(Unit) {
            launch { fade.animateTo(1f, tween(300)) }
            launch { pop.animateTo(1f, spring(dampingRatio = 0.45f, stiffness = 220f)) }
            launch { burst.animateTo(1f, tween(1700, easing = FastOutSlowInEasing)) }
        }
        BackHandler { onDismiss() }
        val words = if (Lang.tr) listOf("Harika!", "Muhteşem!", "Aferin!", "Süper!") else listOf("Great!", "Awesome!", "Well done!", "Fantastic!")
        val xs = listOf(-110f, 70f, -50f, 100f)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(RX.Bg.copy(alpha = 0.96f * fade.value))
                .clickable { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            BurstParticles(burst.value, EBlue, Modifier.size(380.dp))
            words.forEachIndexed { i, w ->
                val ph = (t + i / 4f) % 1f
                Box(
                    modifier = Modifier
                        .offset(x = xs[i].dp, y = (120f - ph * 300f).dp)
                        .alpha(sin(ph * 3.1415927f))
                        .background(if (i % 2 == 0) ECoral else EBlue, bubble(i % 2 == 0))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Tx(w, Color.White, 14, true)
                }
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.scale(pop.value).alpha(fade.value)
            ) {
                Box(
                    modifier = Modifier
                        .background(Brush.verticalGradient(listOf(EBlue, EBlue.copy(alpha = 0.8f))), bubble(true))
                        .padding(horizontal = 40.dp, vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Tx(
                            if (gained > 1) TF("+%d LEVELS", gained)
                            else if (Lang.tr) "Tebrikler!" else "Congratulations!",
                            Color.White, 16, true
                        )
                        Text(
                            "$level",
                            style = TextStyle(color = Color.White, fontSize = 84.sp, fontWeight = FontWeight.ExtraBold)
                        )
                        Tx(up(T(Ranks.title(level))), Color.White.copy(alpha = 0.9f), 14, true)
                    }
                }
                Spacer(Modifier.height(14.dp))
                Tx(A(area), RX.Muted, 14)
                Spacer(Modifier.height(24.dp))
                Tx(T("Tap to continue"), RX.Muted, 12)
            }
        }
    }
}
