package com.realxp.app.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.realxp.app.data.Areas
import com.realxp.app.data.Levels
import com.realxp.app.data.Store

@Composable
fun App(store: Store) {
    var screen by remember { mutableStateOf(if (store.chosen().isEmpty()) "welcome" else "home") }
    var chosen by remember { mutableStateOf(store.chosen()) }

    when (screen) {
        "welcome" -> WelcomeScreen(onStart = { screen = "pick" })
        "pick" -> PickScreen(onDone = { list ->
            store.saveChosen(list)
            chosen = list
            screen = "home"
        })
        else -> HomeScreen(store, chosen)
    }
}

@Composable
fun PickScreen(onDone: (List<String>) -> Unit) {
    var selected by remember { mutableStateOf(listOf<String>()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RX.Bg)
            .systemBarsPadding()
            .padding(24.dp)
    ) {
        Spacer(Modifier.height(24.dp))
        Text("What do you want to level up?", color = RX.Text, fontSize = 26.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text("Pick 1 to 3.", color = RX.Muted, fontSize = 16.sp)
        Spacer(Modifier.height(24.dp))

        Areas.all.forEach { name ->
            val on = name in selected
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .background(if (on) RX.Panel else RX.Bg, RoundedCornerShape(16.dp))
                    .border(1.dp, if (on) RX.Accent else RX.Line, RoundedCornerShape(16.dp))
                    .clickable {
                        selected = if (on) selected - name
                        else if (selected.size < 3) selected + name
                        else selected
                    }
                    .padding(20.dp)
            ) {
                Text(
                    name,
                    color = if (on) RX.Accent else RX.Text,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(Modifier.weight(1f))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .alpha(if (selected.isEmpty()) 0.4f else 1f)
                .border(1.dp, RX.Accent, RoundedCornerShape(14.dp))
                .clickable(enabled = selected.isNotEmpty()) { onDone(selected) }
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("CONTINUE", color = RX.Accent, fontWeight = FontWeight.Bold, letterSpacing = 3.sp)
        }
    }
}

@Composable
fun HomeScreen(store: Store, chosen: List<String>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RX.Bg)
            .systemBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Spacer(Modifier.height(16.dp))
        Text("REAL XP", color = RX.Text, fontSize = 28.sp, fontWeight = FontWeight.Bold, letterSpacing = 6.sp)
        Spacer(Modifier.height(4.dp))
        Text("Log what you actually did.", color = RX.Muted, fontSize = 15.sp)
        Spacer(Modifier.height(24.dp))
        chosen.forEach { area ->
            AreaCard(area, store)
        }
    }
}

@Composable
fun AreaCard(area: String, store: Store) {
    var xp by remember { mutableStateOf(store.xp(area)) }
    val info = Levels.of(xp)
    val progress by animateFloatAsState(
        targetValue = (info.into.toFloat() / info.need).coerceIn(0f, 1f),
        animationSpec = tween(800),
        label = "progress"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
            .background(RX.Panel, RoundedCornerShape(20.dp))
            .border(1.dp, RX.Line, RoundedCornerShape(20.dp))
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(area, color = RX.Text, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("LV ${info.level}", color = RX.Accent, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(14.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(RX.Line, RoundedCornerShape(4.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .height(8.dp)
                    .background(RX.Accent, RoundedCornerShape(4.dp))
            )
        }
        Spacer(Modifier.height(8.dp))
        Text("${info.into} / ${info.need} XP", color = RX.Muted, fontSize = 13.sp)
        Spacer(Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            LogButton("15 min  +20 XP", Modifier.weight(1f)) {
                store.addXp(area, 20)
                xp += 20
            }
            Spacer(Modifier.width(12.dp))
            LogButton("30 min  +45 XP", Modifier.weight(1f)) {
                store.addXp(area, 45)
                xp += 45
            }
        }
    }
}

@Composable
fun LogButton(label: String, modifier: Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .border(1.dp, RX.Accent, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(label, color = RX.Accent, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}
