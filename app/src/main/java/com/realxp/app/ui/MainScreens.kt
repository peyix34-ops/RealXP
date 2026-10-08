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
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.scale
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
import com.realxp.app.data.Areas
import com.realxp.app.data.DayStat
import com.realxp.app.data.Levels
import com.realxp.app.data.Ranks
import com.realxp.app.data.Store
import com.realxp.app.data.Xp
import com.realxp.app.data.formatTime
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

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

private fun depthOf(route: String): Int = when (route) {
    "welcome" -> 0
    "pick" -> 1
    "home" -> 2
    else -> 3
}

@Composable
fun App(store: Store) {
    val ctx = LocalContext.current
    val ui = remember { ctx.getSharedPreferences("realxp_ui", Context.MODE_PRIVATE) }
    remember {
        RX.dark = ui.getBoolean("dark", false)
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

    var screen by remember { mutableStateOf(if (store.chosen().isEmpty()) "welcome" else "home") }
    var chosen by remember { mutableStateOf(store.chosen()) }
    var openArea by remember { mutableStateOf<String?>(null) }
    var tick by remember { mutableStateOf(0) }

    val area = openArea
    val route = if (area != null) "area:$area" else screen

    AnimatedContent(
        targetState = route,
        transitionSpec = {
            val forward = depthOf(targetState) >= depthOf(initialState)
            val enter = slideInHorizontally(tween(340, easing = FastOutSlowInEasing)) { w ->
                if (forward) w / 5 else -w / 5
            } + fadeIn(tween(340))
            val exit = slideOutHorizontally(tween(340, easing = FastOutSlowInEasing)) { w ->
                if (forward) -w / 5 else w / 5
            } + fadeOut(tween(220))
            enter togetherWith exit
        },
        label = "route"
    ) { r ->
        when {
            r.startsWith("area:") -> {
                BackHandler { openArea = null; tick++ }
                AreaScreen(store, r.removePrefix("area:")) { openArea = null; tick++ }
            }
            r == "welcome" -> WelcomeScreen(onStart = { screen = "pick" })
            r == "pick" -> ArenaPicker(
                Areas.all, "Pick everything\nyou want to level up.", "CONTINUE", null
            ) { list ->
                store.saveChosen(list)
                chosen = list
                screen = "home"
            }
            r == "add" -> {
                BackHandler { screen = "home" }
                ArenaPicker(
                    Areas.all.filter { it !in chosen },
                    "Add more\narenas.", "ADD", { screen = "home" }
                ) { list ->
                    val merged = chosen + list
                    store.saveChosen(merged)
                    chosen = merged
                    screen = "home"
                    tick++
                }
            }
            r == "profile" -> {
                BackHandler { screen = "home" }
                ProfileScreen(store) { screen = "home" }
            }
            else -> HomeHub(
                store, chosen, tick,
                onOpen = { openArea = it },
                onAdd = { screen = "add" },
                onRemove = { name ->
                    val left = chosen - name
                    store.saveChosen(left)
                    chosen = left
                    tick++
                },
                onToggleDark = toggleDark,
                onProfile = { screen = "profile" }
            )
        }
    }
}

@Composable
fun QuickLog(minutes: Int, gainText: String, modifier: Modifier, onClick: () -> Unit) {
    Column(
        modifier = modifier
            .bounceClick { onClick() }
            .background(RX.Panel, RoundedCornerShape(12.dp))
            .border(1.dp, RX.Line, RoundedCornerShape(12.dp))
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("${minutes}m", color = RX.Text, fontSize = 16.sp, fontWeight = FontWeight.Bold, maxLines = 1)
        Spacer(Modifier.height(2.dp))
        Text(gainText, color = RX.Accent, fontSize = 11.sp, maxLines = 1)
    }
}

@Composable
fun StatBox(label: String, value: String, modifier: Modifier) {
    Column(
        modifier = modifier
            .background(RX.Panel, RoundedCornerShape(12.dp))
            .border(1.dp, RX.Line, RoundedCornerShape(12.dp))
            .padding(10.dp)
    ) {
        Text(label, color = RX.Muted, fontSize = 10.sp, letterSpacing = 1.5.sp)
        Spacer(Modifier.height(4.dp))
        Text(value, color = RX.Text, fontSize = 16.sp, fontWeight = FontWeight.Bold, maxLines = 1)
    }
}

@Composable
fun WeekChart(days: List<DayStat>, tone: Color) {
    val maxM = maxOf(60, days.maxOf { it.minutes })
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        days.forEach { d ->
            val frac = animatedProgress(d.minutes.toFloat() / maxM, 800)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    if (d.minutes > 0) "${d.minutes}" else "",
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
                            RoundedCornerShape(6.dp)
                        )
                )
                Spacer(Modifier.height(5.dp))
                Text(
                    d.label,
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
                    label,
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
    }
    BackHandler { onDismiss() }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(RX.Bg.copy(alpha = 0.95f * fade.value))
            .clickable { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(300.dp)) {
            val maxR = size.minDimension / 2
            for (i in 0..2) {
                val t = (wave.value - i * 0.2f).coerceIn(0f, 1f)
                drawCircle(
                    color = tone.copy(alpha = (1f - t) * 0.6f),
                    radius = maxR * t,
                    style = Stroke(2.dp.toPx())
                )
            }
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.scale(pop.value).alpha(fade.value)
        ) {
            Text(
                if (gained > 1) "+$gained LEVELS" else "LEVEL UP",
                color = tone, fontSize = 14.sp, letterSpacing = 7.sp, fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            RankText("$level", level, 88.sp, fontWeight = FontWeight.ExtraBold)
            RankText(
                Ranks.title(level).uppercase(), level, 17.sp,
                fontWeight = FontWeight.Bold, letterSpacing = 4.sp
            )
            Spacer(Modifier.height(6.dp))
            Text(area, color = RX.Muted, fontSize = 14.sp)
            Spacer(Modifier.height(28.dp))
            Text("Tap to continue", color = RX.Muted, fontSize = 12.sp)
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

    val info = Levels.of(xp)
    val tone = RankColors.tone(info.level)
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
                if (res.leftMin > 0) "A day has 24 hours. ${res.leftMin}m left today."
                else "A day only has 24 hours.",
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
            show("+${res.gain} XP  ·  ${m}m", true)
        }
    }

    val request: (Int) -> Unit = { m ->
        if (Random.nextInt(5) == 0) pendingMin = m else doLog(m)
    }

    val pm = pendingMin
    if (pm != null) {
        AlertDialog(
            onDismissRequest = {},
            containerColor = RX.Panel,
            title = { Text("Be honest.", color = RX.Text) },
            text = {
                Text(
                    "You actually did this, right? Or are you just fooling yourself? \uD83D\uDE42",
                    color = RX.Muted
                )
            },
            confirmButton = {
                TextButton(onClick = { pendingMin = null; doLog(pm) }) {
                    Text("Yes, I did", color = RX.Accent)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    pendingMin = null
                    show("Respect for the honesty.", false)
                }) {
                    Text("Not really", color = RX.Muted)
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
                "←  Back",
                color = RX.Muted, fontSize = 14.sp,
                modifier = Modifier.clickable { onBack() }.padding(vertical = 8.dp)
            )
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ArenaBadge(area, 46)
                    Spacer(Modifier.width(12.dp))
                    Column {
                        RankText(area, info.level, 24.sp, fontWeight = FontWeight.Bold)
                        RankText(
                            Ranks.title(info.level).uppercase(), info.level, 11.sp,
                            letterSpacing = 2.sp, fontWeight = FontWeight.Medium
                        )
                    }
                }
                LevelPill(info.level)
            }
            Spacer(Modifier.height(14.dp))
            Bar(barP, 5, tone)
            Spacer(Modifier.height(5.dp))
            Text("${info.into} / ${info.need} XP", color = RX.Muted, fontSize = 12.sp)

            Spacer(Modifier.height(16.dp))
            TabBar(tab) { tab = it }
            Spacer(Modifier.height(20.dp))

            AnimatedContent(
                targetState = tab,
                transitionSpec = { fadeIn(tween(240)) togetherWith fadeOut(tween(120)) },
                label = "tab"
            ) { t ->
                Column(modifier = Modifier.fillMaxWidth()) {
                    if (t == 0) {
                        Text("LOG YOUR WORK", color = RX.Muted, fontSize = 11.sp, letterSpacing = 3.sp)
                        Spacer(Modifier.height(10.dp))
                        Row(modifier = Modifier.fillMaxWidth()) {
                            listOf(15, 30, 60, 90).forEachIndexed { i, m ->
                                if (i > 0) Spacer(Modifier.width(8.dp))
                                QuickLog(
                                    m,
                                    if (fits(m)) "+${gainFor(m)} XP" else "—",
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
                                label = { Text("Other (min)") },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = RX.Text,
                                    unfocusedTextColor = RX.Text,
                                    focusedBorderColor = RX.Accent,
                                    unfocusedBorderColor = RX.Line,
                                    focusedContainerColor = RX.Panel,
                                    unfocusedContainerColor = RX.Panel,
                                    cursorColor = RX.Accent,
                                    focusedLabelColor = RX.Accent,
                                    unfocusedLabelColor = RX.Muted
                                )
                            )
                            Spacer(Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .bounceClick {
                                        val m = input.toIntOrNull()
                                        if (m == null || m < 1) {
                                            error = "Type how many minutes you worked."
                                        } else if (!fits(m)) {
                                            error = "A day only has 24 hours."
                                        } else {
                                            request(m)
                                        }
                                    }
                                    .width(84.dp)
                                    .height(56.dp)
                                    .background(RX.Button, RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("LOG", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                            }
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(
                            when {
                                error.isNotEmpty() -> error
                                typed != null && typed >= 1 && fits(typed) -> "+${gainFor(typed)} XP for ${typed}m"
                                typed != null && typed >= 1 -> "A day only has 24 hours."
                                else -> "Type any amount, for example 5."
                            },
                            color = if (error.isNotEmpty()) Danger else RX.Muted,
                            fontSize = 12.sp
                        )

                        Spacer(Modifier.height(22.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("TODAY'S XP", color = RX.Muted, fontSize = 11.sp, letterSpacing = 3.sp)
                            Text("+$xpTodayShown XP", color = tone, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(Modifier.height(16.dp))
                        Row(modifier = Modifier.fillMaxWidth()) {
                            StatBox("TODAY", formatTime(todaySec), Modifier.weight(1f))
                            Spacer(Modifier.width(8.dp))
                            StatBox("TOTAL", formatTime(totalSec), Modifier.weight(1f))
                            Spacer(Modifier.width(8.dp))
                            StatBox("LOGS", "$count", Modifier.weight(1f))
                        }
                        Spacer(Modifier.height(22.dp))
                        Text("LAST 7 DAYS", color = RX.Muted, fontSize = 11.sp, letterSpacing = 3.sp)
                        Spacer(Modifier.height(10.dp))
                        WeekChart(week, tone)
                        Spacer(Modifier.height(22.dp))
                        Text("LAST 16 WEEKS", color = RX.Muted, fontSize = 11.sp, letterSpacing = 3.sp)
                        Spacer(Modifier.height(12.dp))
                        Heatmap(heat)
                        Spacer(Modifier.height(8.dp))
                        HeatLegend()
                    } else {
                        Text(
                            "YOUR LOGS  ·  ${hist.size}",
                            color = RX.Muted, fontSize = 11.sp, letterSpacing = 3.sp
                        )
                        Spacer(Modifier.height(12.dp))
                        if (hist.isEmpty()) {
                            Text(
                                "No logs yet. Log your first session in Overview.",
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
                    .border(1.dp, RX.Accent, RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(shown, color = RX.Accent, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                if (undoable) {
                    Spacer(Modifier.width(14.dp))
                    Text(
                        "UNDO",
                        color = RX.Text, fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp,
                        modifier = Modifier
                            .clickable {
                                store.undoLast()
                                xp = store.xp(area)
                                rev++
                                show("Undone.", false)
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
