package com.realxp.app.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.realxp.app.data.Levels
import com.realxp.app.data.Ranks
import com.realxp.app.data.Store

fun arenaAccent(name: String): Color = when (name) {
    "English" -> Color(0xFF3D7BFF)
    "Languages" -> Color(0xFF2F9BFF)
    "Reading" -> Color(0xFF22C493)
    "History" -> Color(0xFFE08A3C)
    "Math" -> Color(0xFFE5539A)
    "Science" -> Color(0xFF24B8C4)
    "Writing" -> Color(0xFF9B7BFF)
    "Coding" -> Color(0xFF14B8D4)
    "AI" -> Color(0xFF7C5CFF)
    "Design" -> Color(0xFFFF6FA1)
    "Photography" -> Color(0xFFF2A93B)
    "Art" -> Color(0xFFFF6F61)
    "Music" -> Color(0xFF5B72FF)
    "Speaking" -> Color(0xFF2FBF9F)
    "Cooking" -> Color(0xFFFF8A3D)
    "Driving" -> Color(0xFF6C7A89)
    "Gardening" -> Color(0xFF4CB963)
    "Fitness" -> Color(0xFFFF5A4D)
    "Running" -> Color(0xFFFF7A1A)
    "Swimming" -> Color(0xFF2FB5F5)
    "Cycling" -> Color(0xFF4CCB5C)
    "Yoga" -> Color(0xFFB07CF0)
    "Health" -> Color(0xFFFF5C8A)
    "Finance" -> Color(0xFFE0A800)
    "Business" -> Color(0xFF4A6CF7)
    "Investing" -> Color(0xFF17B26A)
    "Meditation" -> Color(0xFF55BFA0)
    "Sleep" -> Color(0xFF6B7BF0)
    "Travel" -> Color(0xFF21A0D8)
    "Gaming" -> Color(0xFF8F5CF7)
    else -> Color(0xFF3D7BFF)
}

private fun flameOuter(): Path = Path().apply {
    moveTo(12f, 1f)
    cubicTo(13.5f, 5f, 19f, 7.5f, 19f, 14f)
    cubicTo(19f, 18.4f, 15.9f, 22f, 12f, 22f)
    cubicTo(8.1f, 22f, 5f, 18.4f, 5f, 14f)
    cubicTo(5f, 11f, 6.6f, 9f, 8f, 7.6f)
    cubicTo(8.2f, 9.4f, 9.2f, 10.4f, 10.2f, 10.6f)
    cubicTo(9.6f, 7.6f, 10.2f, 3.8f, 12f, 1f)
    close()
}

private fun flameInner(): Path = Path().apply {
    moveTo(12f, 11.5f)
    cubicTo(13f, 13.5f, 15.3f, 14.8f, 15.3f, 17.6f)
    cubicTo(15.3f, 19.7f, 13.8f, 21f, 12f, 21f)
    cubicTo(10.2f, 21f, 8.7f, 19.7f, 8.7f, 17.6f)
    cubicTo(8.7f, 15.5f, 10.6f, 14.2f, 12f, 11.5f)
    close()
}

@Composable
fun FlameIcon(boxSize: Dp, modifier: Modifier = Modifier) {
    val t by rememberInfiniteTransition(label = "flame").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "flameT"
    )
    val outer = remember { flameOuter() }
    val inner = remember { flameInner() }
    Canvas(modifier = modifier.size(boxSize)) {
        val dim = this.size.minDimension
        val s = dim / 24f
        val glowCenter = Offset(dim / 2f, dim * 0.62f)
        drawCircle(
            brush = Brush.radialGradient(
                listOf(Color(0xFFFF8A1A).copy(alpha = 0.30f + 0.18f * t), Color.Transparent),
                center = glowCenter,
                radius = dim * 0.8f
            ),
            radius = dim * 0.8f,
            center = glowCenter
        )
        withTransform({
            scale(s, s, Offset.Zero)
            scale(1f, 1f + 0.07f * t, Offset(12f, 22f))
        }) {
            drawPath(
                outer,
                Brush.verticalGradient(
                    listOf(Color(0xFFFFC21A), Color(0xFFFF7A1A), Color(0xFFF0382B)),
                    startY = 1f, endY = 22f
                )
            )
            drawPath(
                inner,
                Brush.verticalGradient(
                    listOf(Color(0xFFFFF0A0), Color(0xFFFFB02E)),
                    startY = 11f, endY = 21f
                )
            )
        }
    }
}

@Composable
fun ProgressIcon(area: String, progress: Float, boxSize: Int) {
    val accent = arenaAccent(area)
    Box(modifier = Modifier.size(boxSize.dp), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = 3.dp.toPx()
            val tl = Offset(stroke / 2, stroke / 2)
            val sz = Size(size.width - stroke, size.height - stroke)
            drawArc(
                color = accent.copy(alpha = 0.18f),
                startAngle = -90f, sweepAngle = 360f, useCenter = false,
                topLeft = tl, size = sz, style = Stroke(stroke)
            )
            if (progress > 0.01f) {
                drawArc(
                    color = accent,
                    startAngle = -90f, sweepAngle = 360f * progress, useCenter = false,
                    topLeft = tl, size = sz, style = Stroke(stroke, cap = StrokeCap.Round)
                )
            }
        }
        Box(
            modifier = Modifier
                .size((boxSize * 0.68f).dp)
                .background(accent.copy(alpha = 0.12f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            ArenaIcon(area, (boxSize * 0.38f).toInt(), accent)
        }
    }
}

@Composable
private fun DefaultDecor(area: String, modifier: Modifier) {
    val accent = arenaAccent(area)
    val drift by rememberInfiniteTransition(label = "decor").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(6000, easing = LinearEasing), RepeatMode.Reverse),
        label = "decorT"
    )
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        drawCircle(
            color = accent.copy(alpha = 0.10f),
            radius = h * (0.9f + 0.1f * drift),
            center = Offset(w * 0.95f, -h * 0.1f)
        )
        drawCircle(
            color = accent.copy(alpha = 0.06f),
            radius = h * (0.6f - 0.05f * drift),
            center = Offset(w, h * 0.9f)
        )
    }
}

@Composable
fun ArenaDecor(area: String, modifier: Modifier = Modifier) {
    when (area) {
        else -> DefaultDecor(area, modifier)
    }
}

@Composable
fun SectionCard(title: String, trailing: String? = null, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(RX.Panel, RoundedCornerShape(18.dp))
            .border(1.dp, RX.Line, RoundedCornerShape(18.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, color = RX.Muted, fontSize = 11.sp, letterSpacing = 3.sp, fontWeight = FontWeight.Bold)
            if (trailing != null) {
                Text(trailing, color = RX.Text, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(Modifier.height(12.dp))
        content()
    }
}

@Composable
fun ArenaCard2(
    area: String,
    store: Store,
    tick: Int,
    editing: Boolean,
    canRemove: Boolean,
    modifier: Modifier,
    onClick: () -> Unit
) {
    val xp = remember(tick) { store.xp(area) }
    val info = Levels.of(xp)
    val accent = arenaAccent(area)
    val tone = RankColors.tone(info.level)
    val p = animatedProgress(info.into.toFloat() / info.need, 900)
    val shape = RoundedCornerShape(18.dp)
    Column(
        modifier = modifier
            .height(126.dp)
            .background(Brush.verticalGradient(listOf(accent.copy(alpha = 0.12f), RX.Panel)), shape)
            .border(
                1.dp,
                if (info.level >= 10) tone.copy(alpha = 0.55f) else accent.copy(alpha = 0.25f),
                shape
            )
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            ProgressIcon(area, p, 46)
            if (editing && canRemove) {
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .background(Danger.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.Close, null, tint = Danger, modifier = Modifier.size(16.dp))
                }
            } else {
                Box(
                    modifier = Modifier
                        .background(tone.copy(alpha = 0.14f), RoundedCornerShape(50))
                        .padding(horizontal = 9.dp, vertical = 3.dp)
                ) {
                    Text("${T("LV")} ${info.level}", color = tone, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
        Spacer(Modifier.weight(1f))
        RankText(
            A(area), info.level, 15.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1, overflow = TextOverflow.Ellipsis
        )
        Spacer(Modifier.height(2.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                T(Ranks.title(info.level)),
                color = if (info.level >= 10) tone else RX.Muted,
                fontSize = 11.sp
            )
            Text("${info.into}/${info.need}", color = RX.Muted, fontSize = 10.sp)
        }
    }
}
