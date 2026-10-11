package com.realxp.app.ui

import android.app.Activity
import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.realxp.app.data.DayStat
import com.realxp.app.data.LevelInfo
import com.realxp.app.data.Levels
import com.realxp.app.data.Ranks
import com.realxp.app.data.Store
import com.realxp.app.data.Xp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale

@Composable
fun animatedProgress(target: Float, ms: Int = 1000): Float {
    val a = remember { Animatable(0f) }
    LaunchedEffect(target) {
        a.animateTo(target, tween(ms, easing = FastOutSlowInEasing))
    }
    return a.value
}

@Composable
fun Bar(p: Float, h: Int = 6, color: Color = RX.Accent) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(h.dp)
            .background(RX.Line, RoundedCornerShape(h.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(p.coerceIn(0.01f, 1f))
                .height(h.dp)
                .background(color, RoundedCornerShape(h.dp))
        )
    }
}

@Composable
fun App(store: Store) {
    val ctx = LocalContext.current
    val ui = remember { ctx.getSharedPreferences("realxp_ui", Context.MODE_PRIVATE) }
    remember {
        RX.dark = ui.getBoolean("dark", false)
        val saved = ui.getString("lang", null)
        Lang.tr = if (saved != null) saved == "tr" else Locale.getDefault().language == "tr"
        true
    }
    val dark = RX.dark
    val view = LocalView.current
    SideEffect {
        val window = (view.context as? Activity)?.window
        if (window != null) {
            val c = WindowCompat.getInsetsController(window, view)
            c.isAppearanceLightStatusBars = !dark
            c.isAppearanceLightNavigationBars = !dark
        }
    }
    val toggleDark: () -> Unit = {
        RX.dark = !RX.dark
        ui.edit().putBoolean("dark", RX.dark).apply()
    }
    val setLang: (Boolean) -> Unit = { tr ->
        Lang.tr = tr
        ui.edit().putString("lang", if (tr) "tr" else "en").apply()
    }

    val scope = rememberCoroutineScope()
    val blackout = remember { Animatable(0f) }
    val blocker = remember { MutableInteractionSource() }
    val fadeThrough: (() -> Unit) -> Unit = { change ->
        scope.launch {
            blackout.animateTo(1f, tween(380, easing = FastOutSlowInEasing))
            change()
            delay(90)
            blackout.animateTo(0f, tween(560, easing = FastOutSlowInEasing))
        }
    }

    var screen by remember { mutableStateOf(if (store.chosen().isEmpty()) "welcome" else "home") }
    var chosen by remember { mutableStateOf(store.chosen()) }
    var openArea by remember { mutableStateOf<String?>(null) }
    var tick by remember { mutableStateOf(0) }
    var reportTarget by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    var reportBack by remember { mutableStateOf("home") }

    val area = openArea
    val route = if (area != null) "area:$area" else screen

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedContent(
            targetState = route,
            transitionSpec = { fadeIn(tween(220)) togetherWith fadeOut(tween(140)) },
            label = "route"
        ) { r ->
            when {
                r.startsWith("area:") -> {
                    BackHandler { openArea = null; tick++ }
                    AreaScreen(store, r.removePrefix("area:")) { openArea = null; tick++ }
                }
                r == "welcome" -> WelcomeScreen(onStart = { screen = "pick" })
                r == "pick" -> ArenaPicker(
                    Catalog.all,
                    T("Pick everything\nyou want to level up."),
                    T("CONTINUE"), null
                ) { list ->
                    fadeThrough {
                        store.saveChosen(list)
                        chosen = list
                        screen = "home"
                    }
                }
                r == "add" -> {
                    BackHandler { screen = "home" }
                    ArenaPicker(
                        Catalog.all.filter { it !in chosen },
                        T("Add more\narenas."), T("ADD"), { screen = "home" }
                    ) { list ->
                        fadeThrough {
                            val merged = chosen + list
                            store.saveChosen(merged)
                            chosen = merged
                            screen = "home"
                            tick++
                        }
                    }
                }
                r == "profile" -> {
                    BackHandler { screen = "home" }
                    ProfileScreen(
                        store,
                        onBack = { screen = "home" },
                        onReports = {
                            reportTarget = null
                            reportBack = "profile"
                            screen = "reports"
                        }
                    )
                }
                r == "reports" -> {
                    BackHandler { screen = reportBack; tick++ }
                    ReportsScreen(
                        store, reportTarget?.first, reportTarget?.second
                    ) { screen = reportBack; tick++ }
                }
                else -> HomeHub(
                    store, chosen, tick,
                    onOpen = { openArea = it },
                    onAdd = { screen = "add" },
                    onRemove = { name, erase ->
                        if (erase) store.eraseArea(name)
                        val left = chosen - name
                        store.saveChosen(left)
                        chosen = left
                        tick++
                    },
                    onToggleDark = toggleDark,
                    onSetLang = setLang,
                    onProfile = { screen = "profile" },
                    onReport = { y, m ->
                        reportTarget = Pair(y, m)
                        reportBack = "home"
                        screen = "reports"
                    }
                )
            }
        }
        if (blackout.value > 0.001f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = blackout.value))
                    .clickable(interactionSource = blocker, indication = null) {}
            )
        }
    }
}

@Composable
fun QuickLog(
    minutes: Int,
    label: String,
    enabled: Boolean,
    accent: Color,
    modifier: Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .alpha(if (enabled) 1f else 0.5f)
            .then(if (enabled) Modifier.bounceClick { onClick() } else Modifier)
            .background(
                Brush.verticalGradient(listOf(accent.copy(alpha = 0.12f), RX.Panel)),
                RoundedCornerShape(16.dp)
            )
            .border(
                1.dp,
                accent.copy(alpha = if (enabled) 0.32f else 0.12f),
                RoundedCornerShape(16.dp)
            )
            .padding(top = 12.dp, bottom = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("$minutes${T("m")}", color = RX.Text, fontSize = 17.sp, fontWeight = FontWeight.Bold, maxLines = 1)
        Spacer(Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .background(accent.copy(alpha = 0.16f), RoundedCornerShape(50))
                .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
            Text(label, color = accent, fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1)
        }
    }
}

@Composable
fun MiniStat(label: String, value: String, modifier: Modifier) {
    Column(modifier = modifier) {
        Text(label, color = RX.Muted, fontSize = 10.sp, letterSpacing = 1.5.sp)
        Spacer(Modifier.height(3.dp))
        Text(value, color = RX.Text, fontSize = 16.sp, fontWeight = FontWeight.Bold, maxLines = 1)
    }
}

@Composable
fun WeekChart(days: List<DayStat>, tone: Color, labels: List<String>? = null) {
    val maxM = maxOf(60, days.maxOf { it.minutes })
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        days.forEachIndexed { i, d ->
            val frac = animatedProgress(d.minutes.toFloat() / maxM, 800)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    when {
                        d.minutes <= 0 -> ""
                        d.minutes >= 60 -> "${d.minutes / 60}${T("h")}"
                        else -> "${d.minutes}"
                    },
                    color = RX.Muted, fontSize = 9.sp,
                    modifier = Modifier.height(12.dp)
                )
                Box(
                    modifier = Modifier
                        .width(22.dp)
                        .height((56f * frac + 3f).dp)
                        .background(
                            when {
                                d.minutes == 0 -> RX.Line
                                d.isToday -> tone
                                else -> tone.copy(alpha = 0.5f)
                            },
                            RoundedCornerShape(7.dp)
                        )
                )
                Spacer(Modifier.height(5.dp))
                Text(
                    labels?.getOrNull(i) ?: d.label,
                    color = if (d.isToday) tone else RX.Muted,
                    fontSize = 11.sp,
                    fontWeight = if (d.isToday) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
fun TabBar(tab: Int, onSelect: (Int) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth()) {
        listOf("Overview", "History").forEachIndexed { i, label ->
            val on = tab == i
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onSelect(i) },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    T(label),
                    color = if (on) RX.Accent else RX.Muted,
                    fontSize = 14.sp,
                    fontWeight = if (on) FontWeight.Bold else FontWeight.Medium,
                    modifier = Modifier.padding(vertical = 10.dp)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(if (on) RX.Accent else RX.Line)
                )
            }
        }
    }
}

@Composable
fun LevelUpOverlay(area: String, level: Int, gained: Int, onDismiss: () -> Unit) {
    val pop = remember { Animatable(0.3f) }
    val wave = remember { Animatable(0f) }
    val fade = remember { Animatable(0f) }
    val burst = remember { Animatable(0f) }
    val tone = RankColors.tone(level)
    LaunchedEffect(Unit) {
        launch { fade.animateTo(1f, tween(300)) }
        launch {
            pop.animateTo(
                1f,
                spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
            )
        }
        launch { wave.animateTo(1f, tween(1500, easing = FastOutSlowInEasing)) }
        launch { burst.animateTo(1f, tween(1700, easing = FastOutSlowInEasing)) }
    }
    BackHandler { onDismiss() }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(RX.Bg.copy(alpha = 0.95f * fade.value))
            .clickable { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(340.dp)) {
            val maxR = size.minDimension / 2
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(tone.copy(alpha = 0.30f * fade.value), Color.Transparent),
                    radius = maxR
                ),
                radius = maxR
            )
            for (i in 0..2) {
                val t = (wave.value - i * 0.2f).coerceIn(0f, 1f)
                drawCircle(
                    color = tone.copy(alpha = (1f - t) * 0.6f),
                    radius = maxR * t,
                    style = Stroke(2.dp.toPx())
                )
            }
        }
        BurstParticles(burst.value, tone, Modifier.size(360.dp))
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.scale(pop.value).alpha(fade.value)
        ) {
            Text(
                if (gained > 1) TF("+%d LEVELS", gained) else T("LEVEL UP"),
                color = tone, fontSize = 14.sp, letterSpacing = 7.sp, fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            RankText("$level", level, 88.sp, fontWeight = FontWeight.ExtraBold)
            RankText(
                up(T(Ranks.title(level))), level, 17.sp,
                fontWeight = FontWeight.Bold, letterSpacing = 4.sp
            )
            Spacer(Modifier.height(6.dp))
            Text(A(area), color = RX.Muted, fontSize = 14.sp)
            Spacer(Modifier.height(28.dp))
            Text(T("Tap to continue"), color = RX.Muted, fontSize = 12.sp)
        }
    }
}

@Composable
fun AreaHero(area: String, info: LevelInfo, p: Float, popId: Int) {
    val accent = arenaAccent(area)
    val shape = RoundedCornerShape(24.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(Brush.verticalGradient(listOf(accent.copy(alpha = 0.18f), RX.Panel)))
            .border(1.dp, accent.copy(alpha = 0.30f), shape)
    ) {
        ArenaDecor(area, Modifier.matchParentSize())
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ProgressIcon(area, p, 68)
                Spacer(Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    RankText(A(area), info.level, 24.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(2.dp))
                    RankText(
                        up(T(Ranks.title(info.level))), info.level, 11.sp,
                        letterSpacing = 2.sp, fontWeight = FontWeight.Medium
                    )
                }
                LevelPill(info.level)
            }
            Spacer(Modifier.height(16.dp))
            XpBar(p, 6, accent, popId)
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("${info.into} / ${info.need} XP", color = RX.Muted, fontSize = 12.sp)
                Text(
                    "${info.need - info.into} XP  →  ${T("LV")} ${info.level + 1}",
                    color = accent, fontSize = 12.sp, fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun AreaScreen(store: Store, area: String, onBack: () -> Unit) {
    val haptic = LocalHapticFeedback.current
    var xp by remember { mutableStateOf(store.xp(area)) }
    var rev by remember { mutableStateOf(0) }
    var tab by remember { mutableStateOf(0) }
    var input by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    var pendingMin by remember { mutableStateOf<Int?>(null) }
    var bannerText by remember { mutableStateOf<String?>(null) }
    var shown by remember { mutableStateOf("") }
    var undoable by remember { mutableStateOf(false) }
    var bannerId by remember { mutableStateOf(0) }
    var levelUp by remember { mutableStateOf<Int?>(null) }
    var gained by remember { mutableStateOf(1) }
    var popText by remember { mutableStateOf("") }
    var popId by remember { mutableStateOf(0) }

    val accent = arenaAccent(area)
    val info = Levels.of(xp)
    val barP = animatedXpBar(info.level, info.into.toFloat() / info.need)

    val todaySec = remember(rev) { store.secondsToday(area) }
    val totalSec = remember(rev) { store.totalSeconds(area) }
    val count = remember(rev) { store.sessionCount(area) }
    val week = remember(rev) { store.week(area) }
    val heat = remember(rev) { store.heat(16, area) }
    val hist = remember(rev) { store.history(area, 100) }
    val todayMin = todaySec / 60.0
    val xpToday = Xp.forMinutes(todayMin)
    val xpTodayShown = animatedCount(xpToday)
    val weekTotal = week.sumOf { it.minutes }
    val activeDays = heat.sumOf { col -> col.count { (it ?: 0) > 0 } }

    val todayIdx = (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) + 5) % 7
    val weekLabels = (6 downTo 0).map { dayLetter(((todayIdx - it) % 7 + 7) % 7) }

    val gainFor: (Int) -> Int = { m -> Xp.forMinutes(todayMin + m) - Xp.forMinutes(todayMin) }
    val fits: (Int) -> Boolean = { m -> todayMin + m <= Xp.DAY_MINUTES }

    LaunchedEffect(bannerId) {
        if (bannerId > 0) {
            delay(if (undoable) 4500L else 2500L)
            bannerText = null
        }
    }

    val show: (String, Boolean) -> Unit = { text, undo ->
        shown = text
        bannerText = text
        undoable = undo
        bannerId++
    }

    val doLog: (Int) -> Unit = { m ->
        val before = Levels.of(xp).level
        val res = store.logMinutes(area, m)
        if (!res.ok) {
            show(
                if (res.leftMin > 0) TF("A day has 24 hours. %d min left today.", res.leftMin)
                else T("A day only has 24 hours."),
                false
            )
        } else {
            xp = store.xp(area)
            rev++
            input = ""
            error = ""
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            if (res.gain > 0) {
                popText = "+${res.gain} XP"
                popId++
            }
            val after = Levels.of(xp).level
            if (after > before) {
                gained = after - before
                levelUp = after
            }
            show(TF("+%d XP  ·  %d min", res.gain, m), true)
        }
    }

    val request: (Int) -> Unit = { m ->
        if (store.honestyDue()) {
            store.markHonesty()
            pendingMin = m
        } else {
            doLog(m)
        }
    }

    val pm = pendingMin
    if (pm != null) {
        AlertDialog(
            onDismissRequest = {},
            containerColor = RX.Panel,
            title = { Text(T("Be honest."), color = RX.Text) },
            text = {
                Text(
                    T("You actually did this, right? Or are you just fooling yourself? \uD83D\uDE42"),
                    color = RX.Muted
                )
            },
            confirmButton = {
                TextButton(onClick = { pendingMin = null; doLog(pm) }) {
                    Text(T("Yes, I did"), color = RX.Accent)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    pendingMin = null
                    show(T("Respect for the honesty."), false)
                }) {
                    Text(T("Not really"), color = RX.Muted)
                }
            }
        )
    }

    val typed = input.toIntOrNull()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(RX.Page)
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(10.dp))
            Text(
                T("←  Back"),
                color = RX.Muted, fontSize = 14.sp,
                modifier = Modifier.clickable { onBack() }.padding(vertical = 8.dp)
            )
            Spacer(Modifier.height(8.dp))
            AreaHero(area, info, barP, popId)

            Spacer(Modifier.height(14.dp))
            TabBar(tab) { tab = it }
            Spacer(Modifier.height(20.dp))

            AnimatedContent(
                targetState = tab,
                transitionSpec = { fadeIn(tween(240)) togetherWith fadeOut(tween(120)) },
                label = "tab"
            ) { t ->
                Column(modifier = Modifier.fillMaxWidth()) {
                    if (t == 0) {
                        Text(T("LOG YOUR WORK"), color = RX.Muted, fontSize = 11.sp, letterSpacing = 3.sp)
                        Spacer(Modifier.height(10.dp))
                        Row(modifier = Modifier.fillMaxWidth()) {
                            listOf(15, 30, 60, 90).forEachIndexed { i, m ->
                                if (i > 0) Spacer(Modifier.width(8.dp))
                                val ok = fits(m)
                                QuickLog(
                                    m,
                                    if (ok) "+${gainFor(m)} XP" else "24${T("h")}",
                                    ok, accent,
                                    Modifier.weight(1f)
                                ) { request(m) }
                            }
                        }
                        Spacer(Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = input,
                                onValueChange = { v ->
                                    input = v.filter { it.isDigit() }.take(4)
                                    error = ""
                                },
                                label = { Text(T("Other (min)")) },
                                singleLine = true,
                                shape = RoundedCornerShape(14.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = RX.Text,
                                    unfocusedTextColor = RX.Text,
                                    focusedBorderColor = accent,
                                    unfocusedBorderColor = RX.Line,
                                    focusedContainerColor = RX.Panel,
                                    unfocusedContainerColor = RX.Panel,
                                    cursorColor = accent,
                                    focusedLabelColor = accent,
                                    unfocusedLabelColor = RX.Muted
                                )
                            )
                            Spacer(Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .bounceClick {
                                        val m = input.toIntOrNull()
                                        if (m == null || m < 1) {
                                            error = T("Type how many minutes you worked.")
                                        } else if (!fits(m)) {
                                            error = T("A day only has 24 hours.")
                                        } else {
                                            request(m)
                                        }
                                    }
                                    .width(84.dp)
                                    .height(56.dp)
                                    .background(
                                        Brush.horizontalGradient(listOf(accent, accent.copy(alpha = 0.78f))),
                                        RoundedCornerShape(14.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(T("LOG"), color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                            }
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(
                            when {
                                error.isNotEmpty() -> error
                                typed != null && typed >= 1 && fits(typed) ->
                                    TF("+%d XP  ·  %d min", gainFor(typed), typed)
                                typed != null && typed >= 1 -> T("A day only has 24 hours.")
                                else -> T("Type any amount, for example 5.")
                            },
                            color = if (error.isNotEmpty()) Danger else RX.Muted,
                            fontSize = 12.sp
                        )

                        Spacer(Modifier.height(20.dp))
                        SectionCard(T("TODAY'S XP")) {
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    "+$xpTodayShown",
                                    color = accent, fontSize = 38.sp, fontWeight = FontWeight.ExtraBold
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    "XP", color = RX.Muted, fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 7.dp)
                                )
                            }
                            Spacer(Modifier.height(14.dp))
                            Box(Modifier.fillMaxWidth().height(1.dp).background(RX.Line))
                            Spacer(Modifier.height(14.dp))
                            Row(modifier = Modifier.fillMaxWidth()) {
                                MiniStat(T("TODAY"), ftime(todaySec), Modifier.weight(1f))
                                MiniStat(T("TOTAL"), ftime(totalSec), Modifier.weight(1f))
                                MiniStat(T("LOGS"), "$count", Modifier.weight(1f))
                            }
                        }

                        Spacer(Modifier.height(14.dp))
                        SectionCard(T("LAST 7 DAYS"), ftime(weekTotal * 60)) {
                            WeekChart(week, accent, weekLabels)
                        }

                        Spacer(Modifier.height(14.dp))
                        SectionCard(T("LAST 16 WEEKS"), daysText(activeDays)) {
                            Heatmap(heat)
                            Spacer(Modifier.height(8.dp))
                            HeatLegend()
                        }
                    } else {
                        Text(
                            TF("YOUR LOGS  ·  %d", hist.size),
                            color = RX.Muted, fontSize = 11.sp, letterSpacing = 3.sp
                        )
                        Spacer(Modifier.height(12.dp))
                        if (hist.isEmpty()) {
                            Text(
                                T("No logs yet. Log your first session in Overview."),
                                color = RX.Muted, fontSize = 14.sp
                            )
                        }
                        hist.forEach { e -> HistoryRow(e) }
                    }
                }
            }
            Spacer(Modifier.height(32.dp))
        }

        FloatingGain(
            popText, popId,
            Modifier.align(Alignment.TopCenter).padding(top = 150.dp)
        )

        AnimatedVisibility(
            visible = bannerText != null,
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 10.dp),
            enter = fadeIn() + slideInVertically { -it },
            exit = fadeOut()
        ) {
            Row(
                modifier = Modifier
                    .background(RX.Panel, RoundedCornerShape(12.dp))
                    .border(1.dp, accent, RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(shown, color = accent, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                if (undoable) {
                    Spacer(Modifier.width(14.dp))
                    Text(
                        up(T("Undo")),
                        color = RX.Text, fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp,
                        modifier = Modifier
                            .clickable {
                                store.undoLast()
                                xp = store.xp(area)
                                rev++
                                show(T("Undone."), false)
                            }
                            .padding(4.dp)
                    )
                }
            }
        }

        val lu = levelUp
        if (lu != null) {
            LevelUpOverlay(area, lu, gained) { levelUp = null }
        }
    }
}
