package com.realxp.app.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
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
    val pulse by rememberInfiniteTransition(label = "pulse").animateFloat(
        initialValue = 0.25f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(tween(2200), RepeatMode.Reverse),
        label = "pulseValue"
    )

    Box(modifier = Modifier.fillMaxSize().background(RX.Page)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val c = Offset(0f, size.height * 0.1f)
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(Color(0x551E6BFF), Color.Transparent),
                    center = c,
                    radius = size.width * 0.95f
                ),
                radius = size.width * 0.95f,
                center = c
            )
        }

        Canvas(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 110.dp)
                .fillMaxWidth()
                .height(260.dp)
        ) {
            val w = size.width
            val h = size.height
            val sun = Offset(w * 0.62f, h * 0.58f)
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(Color(0xFF18C3F8).copy(alpha = pulse), Color.Transparent),
                    center = sun,
                    radius = w * 0.42f
                ),
                radius = w * 0.42f,
                center = sun
            )
            drawCircle(color = Color(0xFF1E8CFF), radius = w * 0.085f, center = sun)

            val far = Path().apply {
                moveTo(0f, h * 0.66f)
                lineTo(w * 0.16f, h * 0.40f)
                lineTo(w * 0.32f, h * 0.58f)
                lineTo(w * 0.48f, h * 0.44f)
                lineTo(w * 0.70f, h * 0.66f)
                lineTo(w * 0.88f, h * 0.42f)
                lineTo(w, h * 0.60f)
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }
            drawPath(far, Brush.verticalGradient(listOf(Color(0xFF123273), Color(0xFF070F28))))

            val near = Path().apply {
                moveTo(0f, h * 0.80f)
                lineTo(w * 0.22f, h * 0.60f)
                lineTo(w * 0.40f, h * 0.78f)
                lineTo(w * 0.60f, h * 0.72f)
                lineTo(w * 0.82f, h * 0.84f)
                lineTo(w, h * 0.74f)
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }
            drawPath(near, Brush.verticalGradient(listOf(Color(0xFF0A1A44), Color(0xFF060B1D))))
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
                    fontSize = 110.sp,
                    fontWeight = FontWeight.Black,
                    fontStyle = FontStyle.Italic
                ),
                modifier = Modifier.reveal(a1)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "WELCOME TO",
                color = RX.Muted,
                fontSize = 14.sp,
                letterSpacing = 6.sp,
                modifier = Modifier.reveal(a2)
            )
            Spacer(Modifier.height(10.dp))
            Text(
                buildAnnotatedString {
                    withStyle(SpanStyle(color = RX.Text)) { append("REAL ") }
                    withStyle(SpanStyle(color = RX.Accent)) { append("XP") }
                },
                fontSize = 54.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.reveal(a2, 32)
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
                    fontSize = 24.sp,
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
                    .padding(vertical = 18.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "GET STARTED  →",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 3.sp
                )
            }
            Spacer(Modifier.height(48.dp))
        }
    }
}
