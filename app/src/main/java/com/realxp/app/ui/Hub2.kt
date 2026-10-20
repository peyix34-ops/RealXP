package com.realxp.app.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.realxp.app.data.Levels
import com.realxp.app.data.Ranks
import com.realxp.app.data.Store
import com.realxp.app.data.Xp
import java.util.Calendar
import kotlin.math.cos
import kotlin.math.sin

private fun tr(en: String, trk: String): String = if (Lang.tr) trk else en

private fun greeting(): String {
    val h = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when {
        h < 5 -> tr("Late night", "Gece geç saat")
        h < 12 -> tr("Good morning", "Günaydın")
        h < 18 -> tr("Good afternoon", "İyi günler")
        h < 22 -> tr("Good evening", "İyi akşamlar")
        else -> tr("Good night", "İyi geceler")
    }
}

private fun dailyQuote(): String {
    val en = listOf(
        "Small steps, every day.",
        "Earn it. One session at a time.",
        "Consistency beats intensity.",
        "Future you is watching.",
        "Don't break the chain.",
        "Progress, not perfection.",
        "Show up today."
    )
    val trs = listOf(
        "Her gün küçük adımlar.",
        "Kazan. Her seferinde bir seans.",
        "İstikrar, yoğunluğu yener.",
        "Gelecekteki sen izliyor.",
        "Zinciri kırma.",
        "Mükemmellik değil, ilerleme.",
        "Bugün de ortaya çık."
    )
    val d = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
    return if (Lang.tr) trs[d % trs.size] else en[d % en.size]
}

@Composable
private fun LiveBackdrop(modifier: Modifier) {
    val t by rememberInfiniteTransition(label = "hbg").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(14000, easing = LinearEasing), RepeatMode.Reverse),
        label = "hbgT"
    )
    val tw by rememberInfiniteTransition(label = "htw").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(6000, easing = LinearEasing)),
        label = "htwT"
    )
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val k = if (RX.dark) 1f else 0.75f
        fun orb(cx: Float, cy: Float, r: Float, col: Color) {
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(col, Color.Transparent),
                    center = Offset(cx, cy),
                    radius = r
                ),
                radius = r,
                center = Offset(cx, cy)
            )
        }
        orb(w * (0.88f - 0.12f * t), h * (0.07f + 0.05f * t), w * 0.75f, RX.Accent.copy(alpha = 0.17f * k))
        orb(w * (0.05f + 0.10f * t), h * (0.45f - 0.05f * t), w * 0.70f, Color(0xFF8B5CFF).copy(alpha = 0.11f * k))
        orb(w * (0.70f - 0.15f * t), h * (0.92f - 0.04f * t), w * 0.80f, RX.Blue.copy(alpha = 0.13f * k))
        if (RX.dark) {
            for (i in 0 until 46) {
                val x = w * ((i * 0.6180339f) % 1f)
                val y = h * ((i * 0.4142f + 0.09f * (i % 6)) % 1f)
                val a = 0.12f + 0.55f * kotlin.math.abs(sin(tw * 6.2831855f * (1f + i % 3) + i))
                drawCircle(Color.White.copy(alpha = a), (0.7f + (i % 3) * 0.45f).dp.toPx(), Offset(x, y))
            }
        }
    }
}

@Composable
private fun OrbitRing(p: Float, level: Int, empty: Boolean) {
    val tone = RankColors.tone(level)
    val orbit by rememberInfiniteTransition(label = "orbit").animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(9000, easing = LinearEasing)),
        label = "orbitT"
    )
    val breathe by rememberInfiniteTransition(label = "breathe").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2200, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "breatheT"
    )
    Box(modifier = Modifier.size(188.dp), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val c = Offset(size.width / 2f, size.height / 2f)
            val stroke = 10.dp.toPx()
            val r = size.minDimension / 2f - 24.dp.toPx()
            val tl = Offset(c.x - r, c.y - r)
            val sz = Size(2f * r, 2f * r)

            for (i in 0 until 60) {
                val ang = Math.toRadians(i * 6.0 - 90.0)
                val lit = i / 60f < p
                val major = i % 5 == 0
                val r1 = r + 13.dp.toPx()
                val r2 = r1 + (if (major) 7.dp.toPx() else 4.dp.toPx())
                drawLine(
                    color = if (lit) tone.copy(alpha = 0.95f) else RX.Line.copy(alpha = 0.7f),
                    start = Offset(c.x + (r1 * cos(ang)).toFloat(), c.y + (r1 * sin(ang)).toFloat()),
                    end = Offset(c.x + (r2 * cos(ang)).toFloat(), c.y + (r2 * sin(ang)).toFloat()),
                    strokeWidth = if (major) 2.dp.toPx() else 1.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }

            if (empty) {
                drawCircle(
                    color = tone.copy(alpha = 0.10f + 0.12f * breathe),
                    radius = r,
                    center = c,
                    style = Stroke(stroke * (1.3f + 1.4f * breathe))
                )
            } else if (p > 0.01f) {
                drawArc(
                    color = tone.copy(alpha = 0.11f + 0.07f * breathe),
                    startAngle = -90f, sweepAngle = 360f * p, useCenter = false,
                    topLeft = tl, size = sz,
                    style = Stroke(stroke * 1.9f, cap = StrokeCap.Round)
                )
            }

            drawArc(
                color = RX.Line,
                startAngle = -90f, sweepAngle = 360f, useCenter = false,
                topLeft = tl, size = sz, style = Stroke(stroke)
            )
            if (p > 0.01f) {
                rotate(-90f, c) {
                    drawArc(
                        brush = Brush.sweepGradient(
                            listOf(if (level >= 10) tone.copy(alpha = 0.55f) else RX.Blue, tone)
                        ),
                        startAngle = 0f, sweepAngle = 360f * p, useCenter = false,
                        topLeft = tl, size = sz,
                        style = Stroke(stroke, cap = StrokeCap.Round)
                    )
                }
                val ea = Math.toRadians((-90f + 360f * p).toDouble())
                val ex = c.x + r * cos(ea).toFloat()
                val ey = c.y + r * sin(ea).toFloat()
                drawCircle(tone.copy(alpha = 0.35f), stroke * 0.7f, Offset(ex, ey))
                drawCircle(Color.White, stroke * 0.3f, Offset(ex, ey))
            }

            val oa = Math.toRadians((orbit - 90f).toDouble())
            val ox = c.x + r * cos(oa).toFloat()
            val oy = c.y + r * sin(oa).toFloat()
            drawCircle(tone.copy(alpha = 0.28f), 9.dp.toPx(), Offset(ox, oy))
            drawCircle(tone, 3.5.dp.toPx(), Offset(ox, oy))
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(T("LEVEL"), color = RX.Muted, fontSize = 10.sp, letterSpacing = 3.sp)
            Text("$level", color = RX.Text, fontSize = 52.sp, fontWeight = FontWeight.Bold)
            RankText(
                up(T(Ranks.title(level))), level, 10.sp,
                fontWeight = FontWeight.Bold, letterSpacing = 2.sp
            )
        }
    }
}

@Composable
private fun HeroStat(label: String, value: String, tint: Color, modifier: Modifier, flame: Boolean = false) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (flame) {
                FlameIcon(18.dp)
                Spacer(Modifier.width(4.dp))
            }
            Text(value, color = tint, fontSize = 17.sp, fontWeight = FontWeight.Bold, maxLines = 1)
        }
        Spacer(Modifier.height(2.dp))
        Text(label, color = RX.Muted, fontSize = 10.sp, letterSpacing = 1.5.sp, maxLines = 1)
    }
}

@Composable
private fun WeekStrip(col: List<Int?>) {
    val todayIdx = (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) + 5) % 7
    val active = col.count { (it ?: 0) > 0 }
    SectionCard(tr("THIS WEEK", "BU HAFTA"), "$active/7") {
        Row(modifier = Modifier.fillMaxWidth()) {
            for (i in 0 until 7) {
                val m = col.getOrNull(i)
                val today = i == todayIdx
                val lvl = if (m == null) -1 else heatLevel(m)
                val fill = when (lvl) {
                    -1 -> Color.Transparent
                    0 -> RX.Line.copy(alpha = 0.6f)
                    1 -> RX.Accent.copy(alpha = 0.35f)
                    2 -> RX.Accent.copy(alpha = 0.6f)
                    3 -> RX.Accent.copy(alpha = 0.85f)
                    else -> RX.Accent
                }
                Column(
                    modifier = Modifier.weight(1f).staggerIn(i, 40),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .background(fill, CircleShape)
                            .then(
                                when {
                                    today -> Modifier.border(2.dp, RX.Accent, CircleShape)
                                    lvl == -1 -> Modifier.border(1.dp, RX.Line, CircleShape)
                                    else -> Modifier
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (lvl >= 1) {
                            Icon(
                                Icons.Outlined.Check, null,
                                tint = if (lvl >= 3) Color.White else RX.Accent,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(
                        dayLetter(i),
                        color = if (today) RX.Accent else RX.Muted,
                        fontSize = 11.sp,
                        fontWeight = if (today) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

@Composable
private fun ContinueCard(area: String, store: Store, tick: Int, onClick: () -> Unit) {
    val xp = remember(tick) { store.xp(area) }
    val info = Levels.of(xp)
    val accent = arenaAccent(area)
    val p = animatedProgress(info.into.toFloat() / info.need, 900)
    val shape = RoundedCornerShape(20.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .bounceClick { onClick() }
            .background(
                Brush.horizontalGradient(listOf(accent.copy(alpha = 0.18f), RX.Panel.copy(alpha = 0.85f))),
                shape
            )
            .border(1.dp, accent.copy(alpha = 0.38f), shape)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ProgressIcon(area, p, 52)
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                tr("CONTINUE", "DEVAM ET"),
                color = accent, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp
            )
            RankText(A(area), info.level, 17.sp, fontWeight = FontWeight.SemiBold)
            Text(
                "${T("LV")} ${info.level}  ·  ${T(Ranks.title(info.level))}",
                color = RX.Muted, fontSize = 12.sp
            )
        }
        Box(
            modifier = Modifier.size(38.dp).background(accent, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Outlined.ChevronRight, null, tint = Color.White, modifier = Modifier.size(22.dp))
        }
    }
}

@Composable
private fun AddTile(remaining: Int, modifier: Modifier, onClick: () -> Unit) {
    val pulse by rememberInfiniteTransition(label = "addp").animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(tween(1400, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "addpT"
    )
    val shape = RoundedCornerShape(18.dp)
    Column(
        modifier = modifier
            .height(126.dp)
            .clickable { onClick() }
            .background(RX.Accent.copy(alpha = 0.05f), shape)
            .drawBehind {
                drawRoundRect(
                    color = RX.Accent.copy(alpha = 0.45f),
                    cornerRadius = CornerRadius(18.dp.toPx()),
                    style = Stroke(
                        width = 1.5.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 10f), 0f)
                    )
                )
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .scale(pulse)
                .background(
                    Brush.linearGradient(listOf(RX.Accent.copy(alpha = 0.25f), RX.Blue.copy(alpha = 0.15f))),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Outlined.Add, null, tint = RX.Accent, modifier = Modifier.size(26.dp))
        }
        Spacer(Modifier.height(8.dp))
        Text(T("Add Arena"), color = RX.Text, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        Text(
            tr("$remaining more to explore", "$remaining alan daha var"),
            color = RX.Muted, fontSize = 10.sp
        )
    }
}

@Composable
fun ArenaPicker2(
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
                                skinFor(name).PickerTile(
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
fun HomeHub2(
    store: Store,
    chosen: List<String>,
    tick: Int,
    onOpen: (String) -> Unit,
    onAdd: () -> Unit,
    onRemove: (String, Boolean) -> Unit,
    onToggleDark: () -> Unit,
    onSetLang: (Boolean) -> Unit,
    onProfile: () -> Unit,
    onReport: (Int, Int) -> Unit
) {
    val total = remember(tick) { store.totalXp() }
    val investedSec = remember(tick) { store.allSeconds() }
    val streak = remember(tick) { store.streak() }
    val pending = remember(tick) { store.pendingReport() }
    val todayXp = remember(tick) { chosen.sumOf { Xp.forMinutes(store.secondsToday(it) / 60.0) } }
    val weekCol = remember(tick) { store.heat(1, null).firstOrNull() ?: List<Int?>(7) { null } }
    val lastArea = remember(tick) {
        chosen
            .mapNotNull { a -> store.history(a, 1).firstOrNull()?.let { a to it.ts } }
            .maxByOrNull { it.second }
            ?.first
    }
    val info = Levels.overall(total)
    val p = animatedProgress(info.into.toFloat() / info.need, 1400)
    val intoShown = countUp(info.into, 1200)
    var editing by remember { mutableStateOf(false) }
    var toRemove by remember { mutableStateOf<String?>(null) }
    var showSettings by remember { mutableStateOf(false) }

    val rm = toRemove
    if (rm != null) {
        AlertDialog(
            onDismissRequest = { toRemove = null },
            containerColor = RX.Panel,
            title = { Text(TF("Remove %s?", A(rm)), color = RX.Text) },
            text = {
                Column {
                    Text(
                        T("Keep your progress hidden, or erase it for good?"),
                        color = RX.Muted, fontSize = 14.sp
                    )
                    Spacer(Modifier.height(16.dp))
                    DialogButton(T("Remove, keep progress"), RX.Accent) {
                        toRemove = null
                        onRemove(rm, false)
                    }
                    DialogButton(T("Remove and erase progress"), Danger) {
                        toRemove = null
                        onRemove(rm, true)
                    }
                    DialogButton(T("Cancel"), RX.Muted) { toRemove = null }
                }
            },
            confirmButton = {}
        )
    }

    if (showSettings) {
        AlertDialog(
            onDismissRequest = { showSettings = false },
            containerColor = RX.Panel,
            title = { Text(T("Settings"), color = RX.Text, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(T("Dark mode"), color = RX.Text, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                            Text(T("Easier on the eyes at night."), color = RX.Muted, fontSize = 12.sp)
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
                    Spacer(Modifier.height(20.dp))
                    Text(T("Language"), color = RX.Text, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(10.dp))
                    Row {
                        MonthChip("English", !Lang.tr) { onSetLang(false) }
                        Spacer(Modifier.width(8.dp))
                        MonthChip("Türkçe", Lang.tr) { onSetLang(true) }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSettings = false }) {
                    Text(T("Done"), color = RX.Accent)
                }
            }
        )
    }

    val cells = buildList<String?> {
        addAll(chosen)
        if (!editing && chosen.size < Catalog.all.size) add(null)
    }

    Box(modifier = Modifier.fillMaxSize().background(RX.Page)) {
        LiveBackdrop(Modifier.fillMaxSize())
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(greeting(), color = RX.Muted, fontSize = 13.sp)
                    Text(
                        buildAnnotatedString {
                            withStyle(SpanStyle(color = RX.Text)) { append("REAL ") }
                            withStyle(SpanStyle(color = RX.Accent)) { append("XP") }
                        },
                        fontSize = 22.sp, fontWeight = FontWeight.ExtraBold
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RoundIconButton(Icons.Outlined.Person) { onProfile() }
                    Spacer(Modifier.width(8.dp))
                    RoundIconButton(Icons.Outlined.Settings) { showSettings = true }
                }
            }

            Spacer(Modifier.height(14.dp))
            val heroShape = RoundedCornerShape(28.dp)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .staggerIn(0)
                    .background(
                        Brush.verticalGradient(
                            listOf(RX.Panel.copy(alpha = 0.78f), RX.Panel.copy(alpha = 0.50f))
                        ),
                        heroShape
                    )
                    .border(
                        1.dp,
                        Brush.verticalGradient(listOf(RX.Accent.copy(alpha = 0.45f), RX.Line)),
                        heroShape
                    )
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OrbitRing(p, info.level, total == 0)
                Spacer(Modifier.height(4.dp))
                Text(
                    "$intoShown / ${info.need} XP",
                    color = RX.Muted, fontSize = 12.sp
                )
                Spacer(Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HeroStat(T("TODAY"), "+$todayXp XP", RX.Accent, Modifier.weight(1f))
                    Box(Modifier.width(1.dp).height(30.dp).background(RX.Line))
                    HeroStat(
                        tr("STREAK", "SERİ"),
                        if (streak == 1) "1 ${T("day")}" else "$streak ${T("days")}",
                        Flame, Modifier.weight(1f), flame = true
                    )
                    Box(Modifier.width(1.dp).height(30.dp).background(RX.Line))
                    HeroStat(tr("INVESTED", "SÜRE"), ftime(investedSec), RX.Text, Modifier.weight(1f))
                }
                Spacer(Modifier.height(14.dp))
                Text(
                    if (total == 0) {
                        tr(
                            "Open an arena and log your first session.",
                            "Bir alana gir ve ilk çalışmanı kaydet."
                        )
                    } else {
                        "“${dailyQuote()}”"
                    },
                    color = RX.Muted, fontSize = 13.sp, fontStyle = FontStyle.Italic,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.height(14.dp))
            Box(modifier = Modifier.staggerIn(1)) { WeekStrip(weekCol) }

            if (lastArea != null) {
                Spacer(Modifier.height(14.dp))
                Box(modifier = Modifier.staggerIn(2)) {
                    ContinueCard(lastArea, store, tick) { onOpen(lastArea) }
                }
            }

            if (pending != null) {
                Spacer(Modifier.height(14.dp))
                Box(modifier = Modifier.staggerIn(3)) {
                    ReportCard(
                        TF("%s report is ready", monthName(pending.second)),
                        T("See how your month went"), true
                    ) { onReport(pending.first, pending.second) }
                }
            }

            Spacer(Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    T("YOUR ARENAS") + "  ·  ${chosen.size}",
                    color = RX.Accent, fontSize = 11.sp, letterSpacing = 3.sp, fontWeight = FontWeight.Bold
                )
                if (chosen.size > 1) {
                    Text(
                        if (editing) T("DONE") else T("EDIT"),
                        color = RX.Accent, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp,
                        modifier = Modifier.clickable { editing = !editing }.padding(vertical = 6.dp)
                    )
                }
            }
            Spacer(Modifier.height(10.dp))
            cells.chunked(2).forEachIndexed { r, row ->
                Row(modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)) {
                    row.forEachIndexed { c, name ->
                        if (c > 0) Spacer(Modifier.width(10.dp))
                        val idx = r * 2 + c + 3
                        if (name == null) {
                            AddTile(
                                Catalog.all.size - chosen.size,
                                Modifier.weight(1f).staggerIn(minOf(idx, 12))
                            ) { onAdd() }
                        } else {
                            skinFor(name).HomeCard(
                                name, store, tick, editing, chosen.size > 1,
                                Modifier.weight(1f).staggerIn(minOf(idx, 12))
                            ) {
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
            Spacer(Modifier.height(28.dp))
        }
    }
}
