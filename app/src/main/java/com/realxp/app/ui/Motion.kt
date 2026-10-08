package com.realxp.app.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
fun FloatingGain(text: String, id: Int, modifier: Modifier = Modifier) {
    val prog = remember { Animatable(1f) }
    LaunchedEffect(id) {
        if (id > 0) {
            prog.snapTo(0f)
            prog.animateTo(1f, tween(1200, easing = FastOutSlowInEasing))
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
                .scale(0.85f + 0.25f * (1f - p))
        )
    }
}
