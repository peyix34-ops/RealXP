package com.realxp.app.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

fun Modifier.bounceClick(onClick: () -> Unit): Modifier = composed {
    val src = remember { MutableInteractionSource() }
    val pressed by src.collectIsPressedAsState()
    val s by animateFloatAsState(
        targetValue = if (pressed) 0.94f else 1f,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = 600f),
        label = "press"
    )
    this.scale(s).clickable(interactionSource = src, indication = null) { onClick() }
}

fun Modifier.staggerIn(index: Int, stepMs: Int = 60): Modifier = composed {
    val a = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        a.animateTo(1f, tween(450, delayMillis = index * stepMs, easing = FastOutSlowInEasing))
    }
    this
        .alpha(a.value)
        .offset { IntOffset(0, ((1f - a.value) * 24.dp.toPx()).toInt()) }
}

@Composable
fun countUp(target: Int, ms: Int = 900): Int {
    val a = remember { Animatable(0f) }
    LaunchedEffect(target) {
        a.animateTo(target.toFloat(), tween(ms, easing = FastOutSlowInEasing))
    }
    return a.value.toInt()
}

@Composable
fun animatedCount(target: Int, ms: Int = 700): Int {
    val v by animateIntAsState(target, tween(ms, easing = FastOutSlowInEasing), label = "count")
    return v
}

@Composable
fun animatedXpBar(level: Int, target: Float): Float {
    val a = remember { Animatable(0f) }
    var prev by remember { mutableStateOf(level) }
    LaunchedEffect(level, target) {
        if (level > prev) {
            a.animateTo(1f, tween(450))
            a.snapTo(0f)
        }
        prev = level
        a.animateTo(target, tween(700, easing = FastOutSlowInEasing))
    }
    return a.value
}

@Composable
fun XpBar(p: Float, h: Int, color: Color, trigger: Int) {
    val shine = remember { Animatable(1f) }
    LaunchedEffect(trigger) {
        if (trigger > 0) {
            shine.snapTo(0f)
            shine.animateTo(1f, tween(900, easing = FastOutSlowInEasing))
        }
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(h.dp)
            .clip(RoundedCornerShape(h.dp))
            .background(RX.Line)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(p.coerceIn(0.01f, 1f))
                .height(h.dp)
                .background(color)
        )
        Canvas(modifier = Modifier.matchParentSize()) {
            val s = shine.value
            if (s < 1f) {
                val w = size.width
                val x = -w * 0.3f + w * 1.6f * s
                drawRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color.Transparent, Color.White.copy(alpha = 0.6f), Color.Transparent),
                        startX = x,
                        endX = x + w * 0.3f
                    )
                )
            }
        }
    }
}

@Composable
fun FloatingGain(text: String, id: Int, modifier: Modifier = Modifier) {
    val prog = remember { Animatable(1f) }
    val pop = remember { Animatable(1f) }
    LaunchedEffect(id) {
        if (id > 0) {
            prog.snapTo(0f)
            pop.snapTo(0f)
            launch2(prog, pop)
        }
    }
    val p = prog.value
    if (id > 0 && p < 1f) {
        Text(
            text,
            color = RX.Accent,
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = modifier
                .offset { IntOffset(0, (-80.dp.toPx() * p).toInt()) }
                .alpha(if (p < 0.6f) 1f else 1f - (p - 0.6f) / 0.4f)
                .scale(0.5f + 0.6f * pop.value)
        )
    }
}

private suspend fun launch2(prog: Animatable<Float, *>, pop: Animatable<Float, *>) {
    kotlinx.coroutines.coroutineScope {
        launch { pop.animateTo(1f, spring(dampingRatio = 0.4f, stiffness = 500f)) }
        launch { prog.animateTo(1f, tween(1200, easing = FastOutSlowInEasing)) }
    }
}

@Composable
fun BurstParticles(progress: Float, tone: Color, modifier: Modifier = Modifier) {
    val parts = remember {
        List(36) { i ->
            val ang = (i / 36f) * 6.2831855f + Random.nextFloat() * 0.3f
            Triple(ang, 0.55f + Random.nextFloat() * 0.45f, 2f + Random.nextFloat() * 4f)
        }
    }
    Canvas(modifier = modifier) {
        val maxR = size.minDimension / 2
        val cx = size.width / 2
        val cy = size.height / 2
        val t = FastOutSlowInEasing.transform(progress.coerceIn(0f, 1f))
        val alpha = (1f - progress).coerceIn(0f, 1f)
        parts.forEachIndexed { i, (a, sp, r) ->
            val d = maxR * sp * t
            drawCircle(
                color = (if (i % 3 == 0) Color.White else tone).copy(alpha = alpha),
                radius = r.dp.toPx() * (1f - 0.5f * progress),
                center = Offset(cx + cos(a) * d, cy + sin(a) * d)
            )
        }
    }
}
