package com.realxp.app.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.realxp.app.data.Areas
import com.realxp.app.data.DayStat
import com.realxp.app.data.Levels
import com.realxp.app.data.Ranks
import com.realxp.app.data.Sess
import com.realxp.app.data.Store
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
fun Bar(p: Float, h: Int = 6) {
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
                .background(RX.Accent, RoundedCornerShape(h.dp))
        )
    }
}

@Composable
fun App(store: Store) {
    var screen by remember { mutableStateOf(if (store.chosen().isEmpty()) "welcome" else "home") }
    var chosen by remember { mutableStateOf(store.chosen()) }
    var openArea by remember { mutableStateOf<String?>(null) }
    var tick by remember { mutableStateOf(0) }

    val area = openArea
    if (area != null) {
        BackHandler { openArea = null; tick++ }
        AreaScreen(store, area) { openArea = null; tick++ }
    } else {
        when (screen) {
            "welcome" -> WelcomeScreen(onStart = { screen = "pick" })
            "pick" -> ArenaPicker(
                Areas.all, "Pick everything\nyou want to level up.", "CONTINUE", null
            ) { list ->
                store.saveChosen(list)
                chosen = list
                screen = "home"
            }
            "add" -> {
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
            else -> LifeHome(
                store, chosen, tick,
                onOpen = { openArea = it },
                onAdd = { screen = "add" },
                onRemove = { name ->
                    val left = chosen - name
                    store.saveChosen(left)
                    chosen = left
                    tick++
                }
            )
        }
    }
}

fun clock(sec: Int): String = "%02d:%02d".format(sec / 60, sec % 60)

@Composable
fun SessionRing(progress: Float, top: String, main: String, bottom: String, live: Boolean, topColor: Color) {
    val pulse by rememberInfiniteTransition(label = "pulse").animateFloat(
        initialValue = 0.1f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(tween(1200), RepeatMode.Reverse),
        label = "pulseValue"
    )
    Box(contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(260.dp)) {
            val stroke = 12.dp.toPx()
            val topLeft = Offset(stroke / 2, stroke / 2)
            val arcSize = Size(size.width - stroke, size.height - stroke)
            drawArc(
                color = RX.Line, startAngle = -90f, sweepAngle = 360f, useCenter = false,
                topLeft = topLeft, size = arcSize,
                style = Stroke(stroke, cap = StrokeCap.Round)
            )
            if (progress > 0.002f) {
                drawArc(
                    brush = Brush.linearGradient(listOf(RX.Blue, RX.Accent)),
                    startAngle = -90f, sweepAngle = 360f * progress, useCenter = false,
                    topLeft = topLeft, size = arcSize,
                    style = Stroke(stroke, cap = StrokeCap.Round)
                )
            }
            if (live) {
                drawCircle(
                    color = RX.Accent.copy(alpha = pulse * 0.5f),
                    radius = (size.minDimension - stroke) / 2 - 18.dp.toPx(),
                    style = Stroke(1.5.dp.toPx())
                )
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(top, color = topColor, fontSize = 13.sp, letterSpacing = 4.sp, fontWeight = FontWeight.Bold)
            Text(main, color = RX.Text, fontSize = 60.sp, fontWeight = FontWeight.Bold)
            Text(bottom, color = RX.Muted, fontSize = 13.sp)
        }
    }
}

@Composable
fun GoalChip(minutes: Int, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .then(
                if (selected) Modifier.background(RX.Accent.copy(alpha = 0.14f), RoundedCornerShape(50))
                else Modifier
            )
            .border(1.dp, if (selected) RX.Accent else RX.Line, RoundedCornerShape(50))
            .clickable { onClick() }
            .padding(horizontal = 18.dp, vertical = 10.dp)
    ) {
        Text(
            "$minutes min",
            color = if (selected) RX.Accent else RX.Muted,
            fontSize = 14.sp, fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun StatBox(label: String, value: String, modifier: Modifier) {
    Column(
        modifier = modifier
            .background(RX.Panel, RoundedCornerShape(16.dp))
            .border(1.dp, RX.Line, RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Text(label, color = RX.Muted, fontSize = 11.sp, letterSpacing = 2.sp)
        Spacer(Modifier.height(6.dp))
        Text(value, color = RX.Text, fontSize = 20.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun WeekChart(days: List<DayStat>) {
    val maxM = maxOf(60, days.maxOf { it.minutes })
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(132.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        days.forEach { d ->
            val frac = animatedProgress(d.minutes.toFloat() / maxM, 800)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    if (d.minutes > 0) "${d.minutes}" else "",
                    color = RX.Muted, fontSize = 10.sp,
                    modifier = Modifier.height(14.dp)
                )
                Box(
                    modifier = Modifier
                        .width(28.dp)
                        .height((80f * frac + 4f).dp)
                        .background(
                            when {
                                d.minutes == 0 -> RX.Line
                                d.isToday -> RX.Accent
                                else -> RX.Blue.copy(alpha = 0.6f)
                            },
                            RoundedCornerShape(7.dp)
                        )
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    d.label,
                    color = if (d.isToday) RX.Accent else RX.Muted,
                    fontSize = 12.sp,
                    fontWeight = if (d.isToday) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
fun LevelUpOverlay(area: String, level: Int, onDismiss: () -> Unit) {
    val pop = remember { Animatable(0.3f) }
    val wave = remember { Animatable(0f) }
    val fade = remember { Animatable(0f) }
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
            .background(Color(0xFF050A1E).copy(alpha = 0.94f * fade.value))
            .clickable { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(340.dp)) {
            val maxR = size.minDimension / 2
            for (i in 0..2) {
                val t = (wave.value - i * 0.2f).coerceIn(0f, 1f)
                drawCircle(
                    color = RX.Accent.copy(alpha = (1f - t) * 0.6f),
                    radius = maxR * t,
                    style = Stroke(3.dp.toPx())
                )
            }
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.scale(pop.value).alpha(fade.value)
        ) {
            Text("LEVEL UP", color = RX.Accent, fontSize = 16.sp, letterSpacing = 8.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            Text("$level", color = RX.Text, fontSize = 104.sp, fontWeight = FontWeight.ExtraBold)
            Text(
                Ranks.title(level).uppercase(),
                color = RX.Text, fontSize = 20.sp, letterSpacing = 5.sp, fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(6.dp))
            Text(area, color = RX.Muted, fontSize = 15.sp)
            Spacer(Modifier.height(32.dp))
            Text("Tap to continue", color = RX.Muted, fontSize = 12.sp)
        }
    }
}

@Composable
fun AreaScreen(store: Store, area: String, onBack: () -> Unit) {
    var xp by remember { mutableStateOf(store.xp(area)) }
    var running by remember { mutableStateOf(store.running()) }
    var now by remember { mutableStateOf(System.currentTimeMillis()) }
    var goal by remember { mutableStateOf(30) }
    var rev by remember { mutableStateOf(0) }
    var banner by remember { mutableStateOf<String?>(null) }
    var shown by remember { mutableStateOf("") }
    var askHonest by remember { mutableStateOf(false) }
    var askCancel by remember { mutableStateOf(false) }
    var levelUp by remember { mutableStateOf<Int?>(null) }

    val info = Levels.of(xp)
    val barP = animatedProgress(info.into.toFloat() / info.need, 900)

    val r = running
    val mine = r != null && r.area == area
    val elapsed = if (r != null && r.area == area)
        ((now - r.startMs) / 1000).toInt().coerceIn(0, Sess.MAX_SEC) else 0
    val goalMin = if (r != null && r.area == area) r.goalMin else goal
    val ringP by animateFloatAsState(
        if (mine) (elapsed / (goalMin * 60f)).coerceIn(0f, 1f) else 0f,
        tween(300), label = "ringP"
    )

    val todaySec = remember(rev) { store.secondsToday(area) }
    val totalSec = remember(rev) { store.totalSeconds(area) }
    val count = remember(rev) { store.sessionCount(area) }
    val week = remember(rev) { store.week(area) }

    LaunchedEffect(running) {
        while (running != null) {
            now = System.currentTimeMillis()
            delay(250)
        }
    }

    LaunchedEffect(banner) {
        val b = banner
        if (b != null) {
            shown = b
            delay(2000)
            banner = null
        }
    }

    val commitFinish: () -> Unit = {
        val before = Levels.of(xp).level
        val res = store.finishSession()
        running = null
        xp = store.xp(area)
        rev++
        if (res != null && res.counted) {
            val after = Levels.of(xp).level
            if (after > before) levelUp = after
            banner = if (res.gain > 0) "+${res.gain} XP  ·  ${formatTime(res.seconds)}"
            else "${formatTime(res.seconds)} logged  ·  daily XP cap reached"
        }
    }

    val finish: () -> Unit = {
        if (elapsed < Sess.MIN_SEC) {
            store.cancelSession()
            running = null
            banner = "Sessions under 5 min don't count."
        } else if (Random.nextInt(5) == 0) {
            askHonest = true
        } else {
            commitFinish()
        }
    }

    if (askHonest) {
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
                TextButton(onClick = { askHonest = false; commitFinish() }) {
                    Text("Yes, I did", color = RX.Accent)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    askHonest = false
                    store.cancelSession()
                    running = null
                    banner = "Respect for the honesty."
                }) {
                    Text("Not really", color = RX.Muted)
                }
            }
        )
    }

    if (askCancel) {
        AlertDialog(
            onDismissRequest = { askCancel = false },
            containerColor = RX.Panel,
            title = { Text("Discard this session?", color = RX.Text) },
            text = { Text("The time won't count and no XP is earned.", color = RX.Muted) },
            confirmButton = {
                TextButton(onClick = {
                    askCancel = false
                    store.cancelSession()
                    running = null
                }) { Text("Discard", color = Danger) }
            },
            dismissButton = {
                TextButton(onClick = { askCancel = false }) {
                    Text("Keep going", color = RX.Accent)
                }
            }
        )
    }

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
                .padding(horizontal = 24.dp)
        ) {
            Spacer(Modifier.height(16.dp))
            Text(
                "←  Back",
                color = RX.Muted, fontSize = 15.sp,
                modifier = Modifier.clickable { onBack() }.padding(vertical = 8.dp)
            )
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ArenaTile(area, 48)
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text(area, color = RX.Text, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                        Text(
                            Ranks.title(info.level).uppercase(),
                            color = RX.Muted, fontSize = 12.sp, letterSpacing = 3.sp
                        )
                    }
                }
                Text("LV ${info.level}", color = RX.Accent, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(16.dp))
            Bar(barP, 8)
            Spacer(Modifier.height(8.dp))
            Text("${info.into} / ${info.need} XP", color = RX.Muted, fontSize = 13.sp)

            Spacer(Modifier.height(28.dp))
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                if (mine) {
                    val reached = elapsed >= goalMin * 60
                    SessionRing(
                        ringP,
                        if (reached) "GOAL REACHED" else "FOCUS",
                        clock(elapsed),
                        "of ${clock(goalMin * 60)}",
                        true,
                        if (reached) RX.Accent else RX.Muted
                    )
                } else {
                    SessionRing(0f, "GOAL", "$goal", "minutes", false, RX.Muted)
                }
            }
            Spacer(Modifier.height(24.dp))

            if (mine) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(RX.Button, RoundedCornerShape(50))
                        .clickable { finish() }
                        .padding(vertical = 18.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("FINISH", color = Color.White, fontWeight = FontWeight.Bold, letterSpacing = 4.sp)
                }
                Text(
                    "Discard session",
                    color = RX.Muted, fontSize = 14.sp,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .clickable { askCancel = true }
                        .padding(14.dp)
                )
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf(15, 30, 60, 90).forEach { g ->
                        GoalChip(g, g == goal) { goal = g }
                    }
                }
                Spacer(Modifier.height(18.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(RX.Button, RoundedCornerShape(50))
                        .clickable {
                            if (r != null) {
                                banner = "Finish your ${r.area} session first."
                            } else {
                                store.startSession(area, goal)
                                running = store.running()
                                now = System.currentTimeMillis()
                            }
                        }
                        .padding(vertical = 18.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("START SESSION", color = Color.White, fontWeight = FontWeight.Bold, letterSpacing = 4.sp)
                }
                Spacer(Modifier.height(10.dp))
                Text(
                    "Up to 180 XP per day. Sessions under 5 min don't count.",
                    color = RX.Muted, fontSize = 12.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            Spacer(Modifier.height(32.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                StatBox("TODAY", formatTime(todaySec), Modifier.weight(1f))
                Spacer(Modifier.width(10.dp))
                StatBox("TOTAL", formatTime(totalSec), Modifier.weight(1f))
                Spacer(Modifier.width(10.dp))
                StatBox("SESSIONS", "$count", Modifier.weight(1f))
            }
            Spacer(Modifier.height(32.dp))
            Text("LAST 7 DAYS", color = RX.Muted, fontSize = 13.sp, letterSpacing = 3.sp)
            Spacer(Modifier.height(14.dp))
            WeekChart(week)
            Spacer(Modifier.height(40.dp))
        }

        AnimatedVisibility(
            visible = banner != null,
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 12.dp),
            enter = fadeIn() + slideInVertically { -it },
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .background(RX.Panel, RoundedCornerShape(14.dp))
                    .border(1.dp, RX.Accent, RoundedCornerShape(14.dp))
                    .padding(horizontal = 22.dp, vertical = 12.dp)
            ) {
                Text(shown, color = RX.Accent, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }

        val lu = levelUp
        if (lu != null) {
            LevelUpOverlay(area, lu) { levelUp = null }
        }
    }
}
