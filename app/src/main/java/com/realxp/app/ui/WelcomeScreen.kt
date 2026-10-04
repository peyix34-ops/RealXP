package com.realxp.app.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun rememberReveal(delayMs: Int): State<Float> {
    val a = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        a.animateTo(1f, tween(900, delayMillis = delayMs, easing = FastOutSlowInEasing))
    }
    return a.asState()
}

fun Modifier.reveal(t: Float, rise: Int = 24): Modifier =
    this.alpha(t).offset { IntOffset(0, ((1f - t) * rise.dp.toPx()).toInt()) }

@Composable
fun WelcomeScreen(onStart: () -> Unit) {
    val a1 by rememberReveal(200)
    val a2 by rememberReveal(800)
    val a3 by rememberReveal(1500)
    val a4 by rememberReveal(2200)
    val glow by rememberInfiniteTransition(label = "glow").animateFloat(
        initialValue = 0.35f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1600), RepeatMode.Reverse),
        label = "glowValue"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RX.Bg)
            .systemBarsPadding()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "WELCOME TO",
            color = RX.Muted,
            fontSize = 14.sp,
            letterSpacing = 6.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.reveal(a1)
        )
        Spacer(Modifier.height(14.dp))
        Text(
            "REAL XP",
            color = RX.Text,
            fontSize = 52.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 8.sp,
            modifier = Modifier.reveal(a2, 32)
        )
        Spacer(Modifier.height(22.dp))
        Box(
            modifier = Modifier
                .width((140f * a2).dp)
                .height(2.dp)
                .background(RX.Accent.copy(alpha = glow))
        )
        Spacer(Modifier.height(28.dp))
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.reveal(a3)
        ) {
            Text(
                "Life doesn't hand out levels.",
                color = RX.Muted,
                fontSize = 18.sp,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "Earn yours.",
                color = RX.Accent,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
        Spacer(Modifier.height(64.dp))
        Box(
            modifier = Modifier
                .reveal(a4, 16)
                .border(1.dp, RX.Accent, RoundedCornerShape(14.dp))
                .clickable { onStart() }
                .padding(horizontal = 52.dp, vertical = 16.dp)
        ) {
            Text("BEGIN", color = RX.Accent, fontWeight = FontWeight.Bold, letterSpacing = 4.sp)
        }
    }
}
