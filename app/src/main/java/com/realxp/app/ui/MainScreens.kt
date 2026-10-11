package com.realxp.app.ui

import android.app.Activity
import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.realxp.app.data.DayStat
import com.realxp.app.data.LevelInfo
import com.realxp.app.data.Ranks
import com.realxp.app.data.Store
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
    val bars = RX.dark || Chrome.darkSurface
    val view = LocalView.current
    SideEffect {
        val window = (view.context as? Activity)?.window
        if (window != null) {
            val c = WindowCompat.getInsetsController(window, view)
            c.isAppearanceLightStatusBars = !bars
            c.isAppearanceLightNavigationBars = !bars
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
                r == "pick" -> ArenaPicker2(
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
                    ArenaPicker2(
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
                else -> HomeHub2(
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
fun AreaScreen(store: Store, area: String, onBack: () -> Unit) {
    val s = rememberAreaState(store, area)
    skinFor(area).Screen(s, onBack)
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
