package com.realxp.app.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.realxp.app.data.Levels
import com.realxp.app.data.Quests
import com.realxp.app.data.Store
import kotlinx.coroutines.delay
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
            "pick" -> ArenaPicker { list ->
                store.saveChosen(list)
                chosen = list
                screen = "home"
            }
            else -> LifeHome(store, chosen, tick) { openArea = it }
        }
    }
}

@Composable
fun QuestRow(target: Int, reward: Int, today: Int) {
    val done = today >= target
    val border by animateColorAsState(if (done) RX.Accent else RX.Line, tween(400), label = "qb")
    val bg by animateColorAsState(
        if (done) RX.Accent.copy(alpha = 0.12f) else RX.Panel, tween(400), label = "qbg"
    )
    val p = animatedProgress((today.toFloat() / target).coerceIn(0f, 1f), 700)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
            .background(bg, RoundedCornerShape(16.dp))
            .border(1.dp, border, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                if (done) "✓  Work $target min" else "Work $target min",
                color = if (done) RX.Accent else RX.Text,
                fontSize = 16.sp, fontWeight = FontWeight.Bold
            )
            Text(
                "+$reward XP",
                color = if (done) RX.Accent else RX.Muted,
                fontSize = 14.sp, fontWeight = FontWeight.Bold
            )
        }
        Spacer(Modifier.height(10.dp))
        Bar(p, 4)
        Spacer(Modifier.height(6.dp))
        Text("${minOf(today, target)} / $target min", color = RX.Muted, fontSize = 12.sp)
    }
}

@Composable
fun AreaScreen(store: Store, area: String, onBack: () -> Unit) {
    var xp by remember { mutableStateOf(store.xp(area)) }
    var today by remember { mutableStateOf(store.minutesToday(area)) }
    var input by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    var pending by remember { mutableStateOf<Int?>(null) }
    var banner by remember { mutableStateOf<String?>(null) }
    var shown by remember { mutableStateOf("") }

    val info = Levels.of(xp)
    val p = animatedProgress(info.into.toFloat() / info.need, 900)

    LaunchedEffect(banner) {
        val b = banner
        if (b != null) {
            shown = b
            delay(1800)
            banner = null
        }
    }

    val commit: (Int) -> Unit = { minutes ->
        val before = Levels.of(xp).level
        val gain = store.logMinutes(area, minutes)
        xp = store.xp(area)
        today = store.minutesToday(area)
        input = ""
        val after = Levels.of(xp).level
        banner = if (after > before) "LEVEL UP  ·  $area LV $after"
        else if (gain > 0) "+$gain XP"
        else "Logged"
    }

    val pm = pending
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
                TextButton(onClick = { pending = null; commit(pm) }) {
                    Text("Yes, I did", color = RX.Accent)
                }
            },
            dismissButton = {
                TextButton(onClick = { pending = null; banner = "Respect for the honesty." }) {
                    Text("Not really", color = RX.Muted)
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
                    ArenaTile(area, 44)
                    Spacer(Modifier.width(14.dp))
                    Text(area, color = RX.Text, fontSize = 30.sp, fontWeight = FontWeight.Bold)
                }
                Text("LV ${info.level}", color = RX.Accent, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(14.dp))
            Bar(p, 8)
            Spacer(Modifier.height(8.dp))
            Text("${info.into} / ${info.need} XP", color = RX.Muted, fontSize = 13.sp)

            Spacer(Modifier.height(32.dp))
            Text("HOW LONG DID YOU WORK?", color = RX.Muted, fontSize = 13.sp, letterSpacing = 3.sp)
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = input,
                onValueChange = { v -> input = v.filter { it.isDigit() }.take(3) },
                label = { Text("Minutes") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = RX.Text,
                    unfocusedTextColor = RX.Text,
                    focusedBorderColor = RX.Accent,
                    unfocusedBorderColor = RX.Line,
                    cursorColor = RX.Accent,
                    focusedLabelColor = RX.Accent,
                    unfocusedLabelColor = RX.Muted
                )
            )
            if (error.isNotEmpty()) {
                Spacer(Modifier.height(6.dp))
                Text(error, color = RX.Muted, fontSize = 13.sp)
            }
            Spacer(Modifier.height(14.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(RX.Button, RoundedCornerShape(50))
                    .clickable {
                        val m = input.toIntOrNull()
                        if (m == null || m < 5 || m > 240) {
                            error = "Enter between 5 and 240 minutes."
                        } else {
                            error = ""
                            if (Random.nextInt(5) == 0) pending = m else commit(m)
                        }
                    }
                    .padding(vertical = 18.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "LOG SESSION",
                    color = Color.White, fontWeight = FontWeight.Bold, letterSpacing = 3.sp
                )
            }

            Spacer(Modifier.height(32.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("TODAY'S QUESTS", color = RX.Muted, fontSize = 13.sp, letterSpacing = 3.sp)
                Text("$today min today", color = RX.Muted, fontSize = 13.sp)
            }
            Spacer(Modifier.height(12.dp))
            Quests.steps.forEach { (m, x) -> QuestRow(m, x, today) }
            Spacer(Modifier.height(32.dp))
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
    }
}
