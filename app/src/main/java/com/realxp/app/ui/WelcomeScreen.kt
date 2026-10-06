package com.realxp.app.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
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
    val a2 by rememberReveal(600)
    val a3 by rememberReveal(1200)
    val a4 by rememberReveal(1800)

    Box(modifier = Modifier.fillMaxSize().background(RX.Page)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = RX.Accent.copy(alpha = 0.08f),
                radius = size.width * 0.8f,
                center = Offset(size.width, size.height * 0.05f)
            )
            drawCircle(
                color = RX.Blue.copy(alpha = 0.06f),
                radius = size.width * 0.9f,
                center = Offset(0f, size.height * 1.02f)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.weight(1f))
            Text(
                "R",
                style = TextStyle(
                    brush = RX.Button,
                    fontSize = 120.sp,
                    fontWeight = FontWeight.Black,
                    fontStyle = FontStyle.Italic
                ),
                modifier = Modifier.reveal(a1)
            )
            Spacer(Modifier.height(12.dp))
            Text(
                "WELCOME TO",
                color = RX.Muted,
                fontSize = 13.sp,
                letterSpacing = 6.sp,
                modifier = Modifier.reveal(a2)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                buildAnnotatedString {
                    withStyle(SpanStyle(color = RX.Text)) { append("REAL ") }
                    withStyle(SpanStyle(color = RX.Accent)) { append("XP") }
                },
                fontSize = 52.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.reveal(a2, 32)
            )
            Spacer(Modifier.height(24.dp))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.reveal(a3)
            ) {
                Text(
                    "Life doesn't hand out levels.",
                    color = RX.Text.copy(alpha = 0.75f),
                    fontSize = 17.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Earn yours.",
                    color = RX.Accent,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
            Spacer(Modifier.weight(1.4f))
            Box(
                modifier = Modifier
                    .reveal(a4, 16)
                    .fillMaxWidth()
                    .background(RX.Button, RoundedCornerShape(50))
                    .clickable { onStart() }
                    .padding(vertical = 17.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "GET STARTED  →",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 3.sp
                )
            }
            Spacer(Modifier.height(48.dp))
        }
    }
}
