package com.realxp.app.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
fun WelcomeScreen(onStart: () -> Unit) {
    val ring = remember { Animatable(0f) }
    val title = remember { Animatable(0f) }
    val sub = remember { Animatable(0f) }
    val btn = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        launch { ring.animateTo(0.72f, tween(1800, easing = FastOutSlowInEasing)) }
        launch { title.animateTo(1f, tween(900, delayMillis = 400)) }
        launch { sub.animateTo(1f, tween(900, delayMillis = 1100)) }
        launch { btn.animateTo(1f, tween(700, delayMillis = 1900)) }
    }

    val pulse = rememberInfiniteTransition(label = "pulse").animateFloat(
        initialValue = 0.55f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1400), RepeatMode.Reverse),
        label = "pulseValue"
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
        Box(contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.size(220.dp)) {
                val stroke = 14.dp.toPx()
                val topLeft = Offset(stroke / 2, stroke / 2)
                val arcSize = Size(size.width - stroke, size.height - stroke)
                drawArc(
                    color = RX.Line,
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(stroke, cap = StrokeCap.Round)
                )
                drawArc(
                    color = RX.Accent.copy(alpha = pulse.value),
                    startAngle = -90f,
                    sweepAngle = 360f * ring.value,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(stroke, cap = StrokeCap.Round)
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("LEVEL", color = RX.Muted, fontSize = 13.sp, letterSpacing = 4.sp)
                Text("1", color = RX.Text, fontSize = 64.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(Modifier.height(40.dp))

        Text(
            "REAL XP",
            color = RX.Text,
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 6.sp,
            modifier = Modifier.alpha(title.value)
        )
        Spacer(Modifier.height(12.dp))
        Text(
            "Life doesn't hand out levels.\nEarn yours.",
            color = RX.Muted,
            fontSize = 17.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.alpha(sub.value)
        )

        Spacer(Modifier.height(48.dp))

        Box(
            modifier = Modifier
                .alpha(btn.value)
                .border(1.dp, RX.Accent, RoundedCornerShape(14.dp))
                .clickable { onStart() }
                .padding(horizontal = 44.dp, vertical = 14.dp)
        ) {
            Text("BEGIN", color = RX.Accent, fontWeight = FontWeight.Bold, letterSpacing = 3.sp)
        }
    }
}
