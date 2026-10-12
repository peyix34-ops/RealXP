package com.realxp.app.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.realxp.app.data.Store
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sqrt

private val FBg = Color(0xFF070B0A)
private val FBg2 = Color(0xFF0C1412)
private val FLine = Color(0xFF1B3B33)
private val FGreen = Color(0xFF3DDC84)
private val FRed = Color(0xFFFF5F56)
private val FAmber = Color(0xFFFFB454)
private val FBlue: Color
    get() = if (RX.dark) Color(0xFF6C9BFF) else Color(0xFF2F6BFF)
private val FSky: Color
    get() = if (RX.dark) Color(0xFF0F1D3F) else Color(0xFFE3EEFF)

private fun fixBubble(tailLeft: Boolean) = RoundedCornerShape(
    topStart = 18.dp,
    topEnd = 18.dp,
    bottomEnd = if (tailLeft) 18.dp else 4.dp,
    bottomStart = if (tailLeft) 4.dp else 18.dp
)

@Composable
private fun FixGrid(modifier: Modifier) {
    Canvas(modifier = modifier) {
        val step = 22.dp.toPx()
        var x = 0f
        while (x < size.width) {
            drawLine(FGreen.copy(alpha = 0.05f), Offset(x, 0f), Offset(x, size.height), 1f)
            x += step
        }
        var y = 0f
        while (y < size.height) {
            drawLine(FGreen.copy(alpha = 0.05f), Offset(0f, y), Offset(size.width, y), 1f)
            y += step
        }
    }
}

@Composable
private fun FixTermIcon() {
    val v by rememberInfiniteTransition(label = "fixblink").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(520), RepeatMode.Reverse),
        label = "fixblinkV"
    )
    Box(
        modifier = Modifier
            .size(width = 46.dp, height = 32.dp)
            .background(FBg, RoundedCornerShape(5.dp))
            .border(1.dp, FGreen.copy(alpha = 0.7f), RoundedCornerShape(5.dp))
    ) {
        Row(modifier = Modifier.padding(start = 5.dp, top = 5.dp)) {
            Box(Modifier.padding(end = 3.dp).size(4.dp).background(FRed, CircleShape))
            Box(Modifier.padding(end = 3.dp).size(4.dp).background(FAmber, CircleShape))
            Box(Modifier.size(4.dp).background(FGreen, CircleShape))
        }
        Row(
            modifier = Modifier.align(Alignment.BottomStart).padding(start = 6.dp, bottom = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(">", color = FGreen, fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
            Text(
                "█",
                color = FGreen.copy(alpha = if (v > 0.5f) 1f else 0f),
                fontSize = 11.sp, fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Composable
fun CodingPickTile(name: String, on: Boolean, modifier: Modifier, onClick: () -> Unit) {
    val border by animateColorAsState(if (on) FGreen else FLine, tween(200), label = "cpb")
    val sc by animateFloatAsState(
        if (on) 1.05f else 1f,
        spring(dampingRatio = 0.5f, stiffness = 500f),
        label = "cpsc"
    )
    val shape = RoundedCornerShape(14.dp)
    Box(
        modifier = modifier
            .height(88.dp)
            .scale(sc)
            .clip(shape)
            .background(Brush.verticalGradient(listOf(FBg2, FBg)))
            .border(1.dp, border, shape)
            .clickable { onClick() }
    ) {
        FixGrid(Modifier.matchParentSize())
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            FixTermIcon()
            Spacer(Modifier.height(8.dp))
            Text(
                A(name), color = FGreen, fontSize = 13.sp,
                fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold
            )
        }
        if (on) {
            PickCheck(FGreen, FBg, Modifier.align(Alignment.TopEnd).padding(6.dp))
        }
    }
}

@Composable
private fun FixGlobe(boxSize: Dp, color: Color, modifier: Modifier = Modifier) {
    val t by rememberInfiniteTransition(label = "fixglobe").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(9000, easing = LinearEasing)),
        label = "fixglobeT"
    )
    Canvas(modifier = modifier.size(boxSize)) {
        val r = this.size.minDimension / 2f * 0.92f
        val c = Offset(this.size.width / 2f, this.size.height / 2f)
        drawCircle(color.copy(alpha = 0.10f), r, c)
        drawCircle(color, r, c, style = Stroke(1.6.dp.toPx()))
        for (i in 0 until 3) {
            val phase = (t + i / 3f) * 3.1415927f
            val w = r * abs(cos(phase))
            drawOval(
                color = color.copy(alpha = 0.55f),
                topLeft = Offset(c.x - w, c.y - r),
                size = Size(2f * w, 2f * r),
                style = Stroke(1.2.dp.toPx())
            )
        }
        for (k in listOf(-0.55f, 0f, 0.55f)) {
            val y = c.y + r * k
            val half = sqrt(r * r - (r * k) * (r * k))
            drawLine(color.copy(alpha = 0.45f), Offset(c.x - half, y), Offset(c.x + half, y), 1.2.dp.toPx())
        }
    }
}

@Composable
fun EnglishPickTile(name: String, on: Boolean, modifier: Modifier, onClick: () -> Unit) {
    val border by animateColorAsState(if (on) FBlue else RX.Line, tween(200), label = "epb")
    val sc by animateFloatAsState(
        if (on) 1.05f else 1f,
        spring(dampingRatio = 0.5f, stiffness = 500f),
        label = "epsc"
    )
    val shape = RoundedCornerShape(14.dp)
    Box(
        modifier = modifier
            .height(88.dp)
            .scale(sc)
            .clip(shape)
            .background(Brush.verticalGradient(listOf(FSky, RX.Panel)))
            .border(1.dp, border, shape)
            .clickable { onClick() }
    ) {
        FixGlobe(54.dp, FBlue, Modifier.align(Alignment.TopCenter).padding(top = 6.dp))
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 8.dp, end = 6.dp)
                .background(FBlue, fixBubble(true))
                .padding(horizontal = 5.dp, vertical = 1.dp)
        ) {
            Text("ABC", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
        }
        Text(
            A(name), color = FBlue, fontSize = 13.sp, fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 6.dp)
        )
        if (on) {
            PickCheck(FBlue, Color.White, Modifier.align(Alignment.TopStart).padding(6.dp))
        }
    }
}

object CodingFixed : ArenaSkin {
    @Composable
    override fun PickerTile(name: String, on: Boolean, modifier: Modifier, onClick: () -> Unit) =
        CodingPickTile(name, on, modifier, onClick)

    @Composable
    override fun HomeCard(
        name: String,
        store: Store,
        tick: Int,
        editing: Boolean,
        canRemove: Boolean,
        modifier: Modifier,
        onClick: () -> Unit
    ) = CodingSkin.HomeCard(name, store, tick, editing, canRemove, modifier, onClick)

    @Composable
    override fun Screen(s: AreaState, onBack: () -> Unit) = CodingSkin.Screen(s, onBack)

    @Composable
    override fun LevelUp(area: String, level: Int, gained: Int, onDismiss: () -> Unit) =
        CodingSkin.LevelUp(area, level, gained, onDismiss)
}

object EnglishFixed : ArenaSkin {
    @Composable
    override fun PickerTile(name: String, on: Boolean, modifier: Modifier, onClick: () -> Unit) =
        EnglishPickTile(name, on, modifier, onClick)

    @Composable
    override fun HomeCard(
        name: String,
        store: Store,
        tick: Int,
        editing: Boolean,
        canRemove: Boolean,
        modifier: Modifier,
        onClick: () -> Unit
    ) = EnglishSkin.HomeCard(name, store, tick, editing, canRemove, modifier, onClick)

    @Composable
    override fun Screen(s: AreaState, onBack: () -> Unit) = EnglishSkin.Screen(s, onBack)

    @Composable
    override fun LevelUp(area: String, level: Int, gained: Int, onDismiss: () -> Unit) =
        EnglishSkin.LevelUp(area, level, gained, onDismiss)
}
