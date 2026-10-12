package com.realxp.app.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.realxp.app.data.DayStat
import com.realxp.app.data.Levels
import com.realxp.app.data.Ranks
import com.realxp.app.data.Store
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin

private val SBgTop: Color
    get() = if (RX.dark) Color(0xFF0B2630) else Color(0xFFE5FAF4)
private val SBg: Color
    get() = if (RX.dark) Color(0xFF061419) else Color(0xFFF6FFFC)
private val SPanel: Color
    get() = if (RX.dark) Color(0xFF0D2A33) else Color(0xFFFFFFFF)
private val SLine: Color
    get() = if (RX.dark) Color(0xFF1E4B57) else Color(0xFFC6E9E0)
private val SInk: Color
    get() = if (RX.dark) Color(0xFFE3F6F2) else Color(0xFF0D2B3A)
private val SMuted: Color
    get() = if (RX.dark) Color(0xFF7FB0B5) else Color(0xFF5E8A92)
private val STeal: Color
    get() = if (RX.dark) Color(0xFF2FE0CC) else Color(0xFF0FB5A3)
private val SViolet: Color
    get() = if (RX.dark) Color(0xFF9B7BFF) else Color(0xFF7A4DFF)
private val SLime: Color
    get() = if (RX.dark) Color(0xFF38E8A8) else Color(0xFF1FC98B)
private val SPink = Color(0xFFFF6FA8)

private fun tr(en: String, trk: String): String = if (Lang.tr) trk else en

private val ELEMENTS = listOf(
    Triple("H", "Hydrogen", "Hidrojen"), Triple("He", "Helium", "Helyum"),
    Triple("Li", "Lithium", "Lityum"), Triple("Be", "Beryllium", "Berilyum"),
    Triple("B", "Boron", "Bor"), Triple("C", "Carbon", "Karbon"),
    Triple("N", "Nitrogen", "Azot"), Triple("O", "Oxygen", "Oksijen"),
    Triple("F", "Fluorine", "Flor"), Triple("Ne", "Neon", "Neon"),
    Triple("Na", "Sodium", "Sodyum"), Triple("Mg", "Magnesium", "Magnezyum"),
    Triple("Al", "Aluminium", "Alüminyum"), Triple("Si", "Silicon", "Silisyum"),
    Triple("P", "Phosphorus", "Fosfor"), Triple("S", "Sulfur", "Kükürt"),
    Triple("Cl", "Chlorine", "Klor"), Triple("Ar", "Argon", "Argon"),
    Triple("K", "Potassium", "Potasyum"), Triple("Ca", "Calcium", "Kalsiyum"),
    Triple("Sc", "Scandium", "Skandiyum"), Triple("Ti", "Titanium", "Titanyum"),
    Triple("V", "Vanadium", "Vanadyum"), Triple("Cr", "Chromium", "Krom"),
    Triple("Mn", "Manganese", "Mangan"), Triple("Fe", "Iron", "Demir"),
    Triple("Co", "Cobalt", "Kobalt"), Triple("Ni", "Nickel", "Nikel"),
    Triple("Cu", "Copper", "Bakır"), Triple("Zn", "Zinc", "Çinko"),
    Triple("Ga", "Gallium", "Galyum"), Triple("Ge", "Germanium", "Germanyum"),
    Triple("As", "Arsenic", "Arsenik"), Triple("Se", "Selenium", "Selenyum"),
    Triple("Br", "Bromine", "Brom"), Triple("Kr", "Krypton", "Kripton"),
    Triple("Rb", "Rubidium", "Rubidyum"), Triple("Sr", "Strontium", "Stronsiyum"),
    Triple("Y", "Yttrium", "İtriyum"), Triple("Zr", "Zirconium", "Zirkonyum"),
    Triple("Nb", "Niobium", "Niyobyum"), Triple("Mo", "Molybdenum", "Molibden"),
    Triple("Tc", "Technetium", "Teknesyum"), Triple("Ru", "Ruthenium", "Rutenyum"),
    Triple("Rh", "Rhodium", "Rodyum"), Triple("Pd", "Palladium", "Paladyum"),
    Triple("Ag", "Silver", "Gümüş"), Triple("Cd", "Cadmium", "Kadmiyum"),
    Triple("In", "Indium", "İndiyum"), Triple("Sn", "Tin", "Kalay")
)

private fun element(level: Int): Triple<String, String, String> =
    if (level > 50) Triple("Au", "Gold", "Altın") else ELEMENTS[(level - 1).coerceIn(0, 49)]

private fun atomic(level: Int): Int = if (level > 50) 79 else level.coerceAtLeast(1)

private fun elName(level: Int): String {
    val e = element(level)
    return if (Lang.tr) e.third else e.second
}

@Composable
private fun Sc(
    text: String,
    color: Color,
    sz: Int,
    bold: Boolean = false,
    modifier: Modifier = Modifier,
    spacing: TextUnit = TextUnit.Unspecified,
    mono: Boolean = false
) {
    Text(
        text,
        color = color,
        fontSize = sz.sp,
        fontFamily = if (mono) FontFamily.Monospace else FontFamily.Default,
        fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
        letterSpacing = spacing,
        modifier = modifier
    )
}

private fun tubePath(x0: Float, tw: Float, h: Float, close: Boolean): Path = Path().apply {
    val r = tw / 2f
    moveTo(x0, 0f)
    lineTo(x0, h - r)
    arcTo(Rect(x0, h - 2f * r, x0 + tw, h), 180f, -180f, false)
    lineTo(x0 + tw, 0f)
    if (close) close()
}

@Composable
private fun ElementTile(level: Int, boxSize: Dp, showName: Boolean, modifier: Modifier = Modifier) {
    val e = element(level)
    val shape = RoundedCornerShape(boxSize * 0.14f)
    val u = boxSize.value
    Box(
        modifier = modifier
            .size(boxSize)
            .background(
                Brush.verticalGradient(listOf(STeal.copy(alpha = 0.25f), SViolet.copy(alpha = 0.20f))),
                shape
            )
            .border(1.5.dp, STeal, shape)
    ) {
        Sc(
            "${atomic(level)}", SMuted, (u * 0.15f).toInt().coerceAtLeast(7), true,
            Modifier.align(Alignment.TopStart).padding(boxSize * 0.08f)
        )
        Sc(e.first, SInk, (u * 0.42f).toInt(), true, Modifier.align(Alignment.Center))
        if (showName) {
            Sc(
                elName(level), STeal, (u * 0.11f).toInt().coerceAtLeast(7), true,
                Modifier.align(Alignment.BottomCenter).padding(bottom = boxSize * 0.06f)
            )
        }
    }
}

@Composable
private fun TestTube(p: Float, boost: Float, modifier: Modifier) {
    val t by rememberInfiniteTransition(label = "tube").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2600, easing = LinearEasing)),
        label = "tubeT"
    )
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val tw = w * 0.56f
        val x0 = (w - tw) / 2f
        val body = tubePath(x0, tw, h, true)
        val outline = tubePath(x0, tw, h, false)
        if (boost > 0.01f) {
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(SLime.copy(alpha = 0.35f * boost), Color.Transparent),
                    center = Offset(w / 2f, h * 0.6f),
                    radius = w * 1.1f
                ),
                radius = w * 1.1f,
                center = Offset(w / 2f, h * 0.6f)
            )
        }
        val top = h - (h - 10.dp.toPx()) * p.coerceIn(0.05f, 1f)
        clipPath(body) {
            drawRect(SPanel.copy(alpha = 0.5f), Offset(x0, 0f), Size(tw, h))
            val wave = Path().apply {
                moveTo(x0, top)
                var x = x0
                while (x <= x0 + tw) {
                    lineTo(x, top + 3.dp.toPx() * sin(((x - x0) / tw) * 9.424778f + t * 6.2831855f))
                    x += 3f
                }
                lineTo(x0 + tw, h)
                lineTo(x0, h)
                close()
            }
            drawPath(wave, Brush.verticalGradient(listOf(SLime, STeal, SViolet), startY = top, endY = h))
            for (i in 0 until 6) {
                val bx = x0 + tw * (0.2f + 0.6f * ((i * 37) % 10) / 10f)
                val ph = (t * (1f + boost * 2.5f) + i / 6f) % 1f
                val by = h - ph * (h - top)
                if (by > top + 4f) {
                    drawCircle(Color.White.copy(alpha = 0.55f), (2f + (i % 3)) * density, Offset(bx, by))
                }
            }
        }
        drawPath(outline, SInk.copy(alpha = 0.55f), style = Stroke(2.dp.toPx(), cap = StrokeCap.Round))
        drawLine(
            SInk.copy(alpha = 0.55f),
            Offset(x0 - 5.dp.toPx(), 1.dp.toPx()), Offset(x0 + tw + 5.dp.toPx(), 1.dp.toPx()),
            2.dp.toPx(), StrokeCap.Round
        )
        for (i in 1..5) {
            val ty = h * 0.12f + (h * 0.62f) * i / 5f
            drawLine(
                SInk.copy(alpha = 0.35f),
                Offset(x0 + tw - 7.dp.toPx(), ty), Offset(x0 + tw, ty), 1.dp.toPx()
            )
        }
    }
}

@Composable
private fun Flask(level: Float, boxW: Dp, boxH: Dp, tint: Color, bubbly: Boolean) {
    val t by rememberInfiniteTransition(label = "flask").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2200, easing = LinearEasing)),
        label = "flaskT"
    )
    Canvas(modifier = Modifier.size(boxW, boxH)) {
        val w = size.width
        val h = size.height
        val neckW = w * 0.32f
        val nx = (w - neckW) / 2f
        val path = Path().apply {
            moveTo(nx, 0f)
            lineTo(nx, h * 0.34f)
            lineTo(w * 0.04f, h * 0.92f)
            quadraticBezierTo(w * 0.04f, h, w * 0.16f, h)
            lineTo(w * 0.84f, h)
            quadraticBezierTo(w * 0.96f, h, w * 0.96f, h * 0.92f)
            lineTo(nx + neckW, h * 0.34f)
            lineTo(nx + neckW, 0f)
            close()
        }
        val top = h - h * 0.78f * level.coerceIn(0.1f, 1f)
        clipPath(path) {
            drawRect(
                Brush.verticalGradient(listOf(tint.copy(alpha = 0.95f), tint.copy(alpha = 0.55f)), startY = top, endY = h),
                Offset(0f, top), Size(w, h - top)
            )
            if (bubbly) {
                for (i in 0 until 3) {
                    val ph = (t + i / 3f) % 1f
                    drawCircle(
                        Color.White.copy(alpha = 0.6f),
                        (1.6f + i) * density,
                        Offset(w * (0.35f + 0.15f * i), h - ph * (h - top))
                    )
                }
            }
        }
        drawPath(path, SInk.copy(alpha = 0.55f), style = Stroke(1.6.dp.toPx(), cap = StrokeCap.Round))
        drawLine(
            SInk.copy(alpha = 0.55f),
            Offset(nx - 3.dp.toPx(), 1.dp.toPx()), Offset(nx + neckW + 3.dp.toPx(), 1.dp.toPx()),
            2.dp.toPx(), StrokeCap.Round
        )
    }
}

@Composable
private fun SciCard(title: String, trailing: String?, content: @Composable ColumnScope.() -> Unit) {
    val shape = RoundedCornerShape(18.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(SPanel, shape)
            .border(1.dp, SLine, shape)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Sc(title, SMuted, 11, true, spacing = 2.5.sp)
            if (trailing != null) Sc(trailing, STeal, 13, true)
        }
        Spacer(Modifier.height(12.dp))
        content()
    }
}

@Composable
private fun ReagentButton(
    m: Int,
    chip: String,
    enabled: Boolean,
    tint: Color,
    modifier: Modifier,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(16.dp)
    Row(
        modifier = modifier
            .alpha(if (enabled) 1f else 0.5f)
            .then(if (enabled) Modifier.bounceClick { onClick() } else Modifier)
            .background(SPanel, shape)
            .border(1.dp, tint.copy(alpha = 0.5f), shape)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Flask(m / 90f, 30.dp, 40.dp, tint, false)
        Spacer(Modifier.width(10.dp))
        Column {
            Sc("$m${T("m")}", SInk, 18, true)
            Spacer(Modifier.height(3.dp))
            Box(
                Modifier
                    .background(tint.copy(alpha = 0.16f), RoundedCornerShape(50))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Sc(chip, tint, 12, true)
            }
        }
    }
}

@Composable
private fun SpecimenTag(label: String, value: String, color: Color, modifier: Modifier) {
    val shape = RoundedCornerShape(12.dp)
    Row(
        modifier = modifier
            .height(IntrinsicSize.Min)
            .clip(shape)
            .background(SBgTop.copy(alpha = 0.6f))
            .border(1.dp, SLine, shape)
    ) {
        Box(Modifier.width(5.dp).fillMaxHeight().background(color))
        Column(modifier = Modifier.padding(10.dp)) {
            Sc(label, SMuted, 10, true, spacing = 1.5.sp)
            Spacer(Modifier.height(3.dp))
            Sc(value, SInk, 15, true)
        }
    }
}

@Composable
private fun Spectrum(marker: Float) {
    Canvas(modifier = Modifier.fillMaxWidth().height(22.dp)) {
        val y = size.height * 0.62f
        drawRoundRect(
            brush = Brush.horizontalGradient(listOf(SViolet, STeal, SLime)),
            topLeft = Offset(0f, y - 4.dp.toPx()),
            size = Size(size.width, 8.dp.toPx()),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx())
        )
        val x = size.width * marker.coerceIn(0.02f, 0.98f)
        val tri = Path().apply {
            moveTo(x, y - 6.dp.toPx())
            lineTo(x - 5.dp.toPx(), y - 14.dp.toPx())
            lineTo(x + 5.dp.toPx(), y - 14.dp.toPx())
            close()
        }
        drawPath(tri, SInk)
    }
}

@Composable
private fun TubeChart(days: List<DayStat>, labels: List<String>) {
    val maxM = maxOf(60, days.maxOf { it.minutes })
    val fr = days.map { animatedProgress(it.minutes.toFloat() / maxM, 900) }
    Column {
        Canvas(modifier = Modifier.fillMaxWidth().height(110.dp)) {
            val n = days.size
            val slot = size.width / n
            val tw = slot * 0.42f
            val h = size.height
            for (i in 0 until n) {
                val x0 = i * slot + (slot - tw) / 2f
                val body = tubePath(x0, tw, h, true)
                val outline = tubePath(x0, tw, h, false)
                val top = h - (h - 8.dp.toPx()) * fr[i]
                clipPath(body) {
                    drawRect(SLine.copy(alpha = 0.35f), Offset(x0, 0f), Size(tw, h))
                    if (days[i].minutes > 0) {
                        drawRect(
                            Brush.verticalGradient(
                                listOf(if (days[i].isToday) SLime else STeal, SViolet),
                                startY = top, endY = h
                            ),
                            Offset(x0, top), Size(tw, h - top)
                        )
                    }
                }
                drawPath(outline, SInk.copy(alpha = 0.45f), style = Stroke(1.6.dp.toPx(), cap = StrokeCap.Round))
            }
        }
        Spacer(Modifier.height(4.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            labels.forEachIndexed { i, l ->
                Sc(l, if (days[i].isToday) STeal else SMuted, 11, days[i].isToday, Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun PetriHeat(cols: List<List<Int?>>) {
    val weeks = cols.size
    val gap = 3.dp
    val prog = animatedProgress(1f, 1500)
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val cell = (maxWidth - gap * (weeks - 1)) / weeks
        val h = cell * 7 + gap * 6
        Canvas(modifier = Modifier.fillMaxWidth().height(h)) {
            val c = cell.toPx()
            val g = gap.toPx()
            for (w in 0 until weeks) {
                for (r in 0 until 7) {
                    val m = cols[w][r] ?: continue
                    val t = (prog * (weeks + 5) - w).coerceIn(0f, 1f)
                    if (t <= 0f) continue
                    val lvl = heatLevel(m)
                    val col = when (lvl) {
                        0 -> SLine.copy(alpha = 0.7f)
                        1 -> STeal.copy(alpha = 0.55f)
                        2 -> STeal
                        3 -> SViolet
                        else -> SLime
                    }
                    val rad = c / 2f * (0.34f + 0.16f * lvl) * t
                    drawCircle(col, rad, Offset(w * (c + g) + c / 2f, r * (c + g) + c / 2f))
                }
            }
        }
    }
}

@Composable
private fun Bubbles(modifier: Modifier) {
    val t by rememberInfiniteTransition(label = "bub").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(12000, easing = LinearEasing)),
        label = "bubT"
    )
    Canvas(modifier = modifier) {
        for (i in 0 until 16) {
            val x = size.width * ((i * 0.137f + 0.04f) % 0.96f)
            val y = size.height - size.height * ((t * (0.6f + (i % 4) * 0.2f) + i * 0.13f) % 1f)
            drawCircle(
                STeal.copy(alpha = 0.07f + (i % 3) * 0.03f),
                (4 + (i % 5) * 4) * density,
                Offset(x, y)
            )
        }
    }
}

@Composable
private fun Orbits(modifier: Modifier) {
    val t by rememberInfiniteTransition(label = "orbit").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(3600, easing = LinearEasing)),
        label = "orbitT"
    )
    Canvas(modifier = modifier) {
        val c = Offset(size.width / 2f, size.height / 2f)
        val a = size.minDimension / 2f * 0.95f
        val b = a * 0.38f
        for (k in 0 until 3) {
            rotate(k * 60f, c) {
                drawOval(
                    STeal.copy(alpha = 0.45f),
                    Offset(c.x - a, c.y - b), Size(2f * a, 2f * b),
                    style = Stroke(1.5.dp.toPx())
                )
                val ang = (t + k / 3f) * 6.2831855f
                drawCircle(SLime, 5.dp.toPx(), Offset(c.x + a * cos(ang), c.y + b * sin(ang)))
            }
        }
    }
}

@Composable
private fun SciFloat(text: String, id: Int, modifier: Modifier) {
    val prog = remember { Animatable(1f) }
    LaunchedEffect(id) {
        if (id > 0) {
            prog.snapTo(0f)
            prog.animateTo(1f, tween(1300, easing = FastOutSlowInEasing))
        }
    }
    val p = prog.value
    if (id > 0 && p < 1f) {
        Box(
            modifier = modifier
                .offset(y = (-70f * p).dp)
                .alpha(if (p < 0.6f) 1f else 1f - (p - 0.6f) / 0.4f)
                .background(Brush.horizontalGradient(listOf(STeal, SLime)), RoundedCornerShape(50))
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Sc(text, Color(0xFF04201C), 20, true)
        }
    }
}

object ScienceSkin : ArenaSkin {

    @Composable
    override fun PickerTile(name: String, on: Boolean, modifier: Modifier, onClick: () -> Unit) {
        val border by animateColorAsState(if (on) STeal else SLine, tween(200), label = "sb")
        val sc by animateFloatAsState(
            if (on) 1.05f else 1f,
            spring(dampingRatio = 0.5f, stiffness = 500f),
            label = "ssc"
        )
        val shape = RoundedCornerShape(14.dp)
        Box(
            modifier = modifier
                .height(88.dp)
                .scale(sc)
                .clip(shape)
                .background(Brush.verticalGradient(listOf(SBgTop, SPanel)))
                .border(1.dp, border, shape)
                .clickable { onClick() }
        ) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Flask(0.65f, 30.dp, 40.dp, STeal, true)
                Spacer(Modifier.height(6.dp))
                Sc(A(name), SInk, 13, true)
            }
            if (on) {
                PickCheck(STeal, Color.White, Modifier.align(Alignment.TopEnd).padding(6.dp))
            }
        }
    }

    @Composable
    override fun HomeCard(
        name: String,
        store: Store,
        tick: Int,
        editing: Boolean,
        canRemove: Boolean,
        modifier: Modifier,
        onClick: () -> Unit
    ) {
        val xp = remember(tick) { store.xp(name) }
        val info = Levels.of(xp)
        val p = animatedProgress(info.into.toFloat() / info.need, 900)
        val shape = RoundedCornerShape(18.dp)
        Box(
            modifier = modifier
                .height(126.dp)
                .clip(shape)
                .background(Brush.verticalGradient(listOf(SBgTop, SPanel)))
                .border(1.dp, STeal.copy(alpha = 0.45f), shape)
                .clickable { onClick() }
        ) {
            ElementTile(info.level, 46.dp, false, Modifier.align(Alignment.TopStart).padding(10.dp))
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp)
                    .background(STeal.copy(alpha = 0.15f), RoundedCornerShape(50))
                    .padding(horizontal = 9.dp, vertical = 3.dp)
            ) {
                if (editing && canRemove) {
                    Icon(Icons.Outlined.Close, null, tint = Danger, modifier = Modifier.size(12.dp))
                } else {
                    Sc("${T("LV")} ${info.level}", STeal, 12, true)
                }
            }
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Sc(A(name), SInk, 15, true)
                Sc(elName(info.level), STeal, 11, true)
                Spacer(Modifier.height(6.dp))
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .background(SLine, RoundedCornerShape(6.dp))
                ) {
                    Box(
                        Modifier
                            .fillMaxWidth(p.coerceIn(0.03f, 1f))
                            .fillMaxHeight()
                            .background(
                                Brush.horizontalGradient(listOf(STeal, SViolet)),
                                RoundedCornerShape(6.dp)
                            )
                    )
                }
            }
        }
    }

    @Composable
    override fun Screen(s: AreaState, onBack: () -> Unit) {
        val info = s.info
        val st = s.stats()
        val barP = animatedXpBar(info.level, info.into.toFloat() / info.need)
        val xpTodayShown = animatedCount(st.xpToday)
        val typed = s.input.toIntOrNull()
        val todayIdx = (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) + 5) % 7
        val weekLabels = (6 downTo 0).map { dayLetter(((todayIdx - it) % 7 + 7) % 7) }
        val fmt = remember(Lang.tr) {
            SimpleDateFormat("d MMM  ·  HH:mm", if (Lang.tr) Locale("tr", "TR") else Locale.US)
        }
        val boost = remember { Animatable(0f) }
        val dust = remember { Animatable(1f) }
        val lines = remember { mutableStateListOf<String>() }

        LaunchedEffect(s.popId) {
            if (s.popId > 0) {
                launch {
                    boost.snapTo(1f)
                    boost.animateTo(0f, tween(2400, easing = FastOutSlowInEasing))
                }
                launch {
                    dust.snapTo(0f)
                    dust.animateTo(1f, tween(1100, easing = FastOutSlowInEasing))
                }
                lines.add(tr("reagent added: ${s.lastMin}m", "reaktif eklendi: ${s.lastMin}dk"))
                delay(350L)
                lines.add(tr("reaction complete", "reaksiyon tamamlandı"))
                delay(350L)
                lines.add(tr("+${s.lastGain} XP absorbed", "+${s.lastGain} XP emildi"))
                while (lines.size > 3) lines.removeAt(0)
            }
        }

        HonestyHost(s, SPanel, SInk, SMuted, STeal)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(SBgTop, SBg)))
        ) {
            Bubbles(Modifier.fillMaxSize())
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(Modifier.height(10.dp))
                Sc(T("←  Back"), SMuted, 14, false, Modifier.clickable { onBack() }.padding(vertical = 8.dp))
                Spacer(Modifier.height(8.dp))

                val heroShape = RoundedCornerShape(24.dp)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(heroShape)
                        .background(Brush.horizontalGradient(listOf(STeal.copy(alpha = 0.16f), SViolet.copy(alpha = 0.12f))))
                        .border(1.dp, STeal.copy(alpha = 0.4f), heroShape)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ElementTile(info.level, 92.dp, true)
                        Spacer(Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Sc(A(s.area), SInk, 24, true)
                            Sc(up(T(Ranks.title(info.level))), SMuted, 10, true, spacing = 2.sp)
                            Spacer(Modifier.height(8.dp))
                            Sc(
                                "→ ${elName(info.level + 1)} (${atomic(info.level + 1)})",
                                STeal, 12, true
                            )
                            Sc("${info.into} / ${info.need} XP", SMuted, 12)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            TestTube(barP, boost.value, Modifier.size(width = 52.dp, height = 112.dp))
                            Sc("${(barP * 100).toInt()}%", STeal, 12, true)
                        }
                    }
                }

                Spacer(Modifier.height(14.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    listOf(T("Overview"), tr("Lab notebook", "Laboratuvar defteri")).forEachIndexed { i, label ->
                        val on = s.tab == i
                        Column(
                            modifier = Modifier.weight(1f).clickable { s.tab = i },
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Sc(label, if (on) STeal else SMuted, 14, on, Modifier.padding(vertical = 10.dp))
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .height(2.dp)
                                    .background(if (on) STeal else SLine)
                            )
                        }
                    }
                }
                Spacer(Modifier.height(18.dp))

                if (s.tab == 0) {
                    Sc(T("LOG YOUR WORK"), SMuted, 11, true, spacing = 2.5.sp)
                    Spacer(Modifier.height(10.dp))
                    val mins = listOf(15, 30, 60, 90)
                    val tints = listOf(STeal, SViolet, SLime, SPink)
                    for (r in 0..1) {
                        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)) {
                            for (c in 0..1) {
                                val idx = r * 2 + c
                                val m = mins[idx]
                                val ok = s.fits(m)
                                if (c > 0) Spacer(Modifier.width(10.dp))
                                ReagentButton(
                                    m,
                                    if (ok) "+${s.gainFor(m)} XP" else "24${T("h")}",
                                    ok, tints[idx], Modifier.weight(1f)
                                ) { s.request(m) }
                            }
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(SPanel, RoundedCornerShape(26.dp))
                                .border(1.dp, SLine, RoundedCornerShape(26.dp))
                                .padding(horizontal = 20.dp)
                        ) {
                            BasicTextField(
                                value = s.input,
                                onValueChange = { v ->
                                    s.input = v.filter { it.isDigit() }.take(4)
                                    s.error = ""
                                },
                                singleLine = true,
                                textStyle = TextStyle(color = SInk, fontSize = 16.sp, fontWeight = FontWeight.SemiBold),
                                cursorBrush = SolidColor(STeal),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                                decorationBox = { inner ->
                                    Box {
                                        if (s.input.isEmpty()) Sc(T("Other (min)"), SMuted, 16)
                                        inner()
                                    }
                                }
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .bounceClick { s.submitInput() }
                                .height(52.dp)
                                .background(Brush.horizontalGradient(listOf(STeal, SViolet)), RoundedCornerShape(26.dp))
                                .padding(horizontal = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Sc(tr("MIX", "KARIŞTIR"), Color.White, 12, true, spacing = 1.sp)
                        }
                    }
                    Spacer(Modifier.height(6.dp))
                    Sc(
                        when {
                            s.error.isNotEmpty() -> s.error
                            typed != null && typed >= 1 && s.fits(typed) ->
                                TF("+%d XP  ·  %d min", s.gainFor(typed), typed)
                            typed != null && typed >= 1 -> T("A day only has 24 hours.")
                            else -> T("Type any amount, for example 5.")
                        },
                        if (s.error.isNotEmpty()) Danger else SMuted, 12
                    )

                    Spacer(Modifier.height(12.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 72.dp)
                            .background(SBgTop.copy(alpha = 0.7f), RoundedCornerShape(14.dp))
                            .border(1.dp, SLine, RoundedCornerShape(14.dp))
                            .padding(12.dp)
                    ) {
                        Sc("// lab log", SMuted, 10, false, Modifier, mono = true)
                        Spacer(Modifier.height(4.dp))
                        if (lines.isEmpty()) {
                            Sc("› " + tr("waiting for reagent…", "reaktif bekleniyor…"), SMuted, 12, false, Modifier, mono = true)
                        }
                        lines.takeLast(3).forEach { line ->
                            Sc(
                                "› $line",
                                if (line.startsWith("+")) SLime else STeal,
                                12, false, Modifier, mono = true
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                    SciCard(T("TODAY'S XP"), null) {
                        Row(verticalAlignment = Alignment.Bottom) {
                            Sc("+$xpTodayShown", STeal, 40, true)
                            Spacer(Modifier.width(6.dp))
                            Sc("XP", SMuted, 14, true, Modifier.padding(bottom = 8.dp))
                        }
                        Spacer(Modifier.height(6.dp))
                        Spectrum(st.xpToday / (st.xpToday + 150f))
                        Spacer(Modifier.height(10.dp))
                        Row(modifier = Modifier.fillMaxWidth()) {
                            SpecimenTag(T("TODAY"), ftime(st.todaySec), STeal, Modifier.weight(1f))
                            Spacer(Modifier.width(8.dp))
                            SpecimenTag(T("TOTAL"), ftime(st.totalSec), SViolet, Modifier.weight(1f))
                            Spacer(Modifier.width(8.dp))
                            SpecimenTag(T("LOGS"), "${st.count}", SLime, Modifier.weight(1f))
                        }
                    }

                    Spacer(Modifier.height(14.dp))
                    SciCard(T("LAST 7 DAYS"), ftime(st.weekTotal * 60)) {
                        TubeChart(st.week, weekLabels)
                    }
                    Spacer(Modifier.height(14.dp))
                    SciCard(T("LAST 16 WEEKS"), daysText(st.activeDays)) {
                        PetriHeat(st.heat)
                    }
                } else {
                    Sc(
                        tr("LAB NOTEBOOK", "LABORATUVAR DEFTERİ") + "  ·  ${st.hist.size}",
                        SMuted, 11, true, spacing = 2.sp
                    )
                    Spacer(Modifier.height(12.dp))
                    if (st.hist.isEmpty()) {
                        Sc(T("No logs yet. Log your first session in Overview."), SMuted, 14)
                    }
                    st.hist.forEachIndexed { idx, e ->
                        val shape = RoundedCornerShape(14.dp)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                                .background(SPanel, shape)
                                .border(1.dp, SLine, shape)
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Flask(e.minutes / 90f, 22.dp, 30.dp, STeal, false)
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Sc("EXP #${st.hist.size - idx}", STeal, 12, true, Modifier, mono = true)
                                Sc("${ftime(e.minutes * 60)}  ·  +${e.gain} XP", SInk, 15, true)
                                Sc(fmt.format(Date(e.ts)), SMuted, 11)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(40.dp))
            }

            if (dust.value < 1f) {
                BurstParticles(dust.value, SLime, Modifier.align(Alignment.TopEnd).padding(top = 90.dp).size(220.dp))
            }
            SciFloat(
                "+${s.lastGain} XP", s.popId,
                Modifier.align(Alignment.TopCenter).statusBarsPadding().padding(top = 170.dp)
            )
            BannerHost(
                s,
                Modifier.align(Alignment.TopCenter).statusBarsPadding().padding(top = 8.dp),
                SPanel, STeal, SInk
            )
            LevelUpHost(s, ScienceSkin)
        }
    }

    @Composable
    override fun LevelUp(area: String, level: Int, gained: Int, onDismiss: () -> Unit) {
        val fade = remember { Animatable(0f) }
        val outP = remember { Animatable(0f) }
        val pop = remember { Animatable(0.2f) }
        val burst = remember { Animatable(0f) }
        LaunchedEffect(Unit) {
            launch { fade.animateTo(1f, tween(300)) }
            launch { outP.animateTo(1f, tween(900, easing = FastOutSlowInEasing)) }
            delay(700L)
            launch { pop.animateTo(1f, spring(dampingRatio = 0.45f, stiffness = 250f)) }
            launch { burst.animateTo(1f, tween(1800, easing = FastOutSlowInEasing)) }
        }
        BackHandler { onDismiss() }
        val before = (level - gained).coerceAtLeast(1)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF04141A).copy(alpha = 0.94f * fade.value))
                .clickable { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            Orbits(Modifier.size(320.dp).alpha(fade.value))
            BurstParticles(burst.value, SLime, Modifier.size(380.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Sc(
                    if (gained > 1) "+$gained  ${T("LV")}" else tr("NEW ELEMENT DISCOVERED", "YENİ ELEMENT KEŞFEDİLDİ"),
                    STeal, 13, true, spacing = 3.sp
                )
                Spacer(Modifier.height(18.dp))
                Box(contentAlignment = Alignment.Center) {
                    ElementTile(
                        before, 150.dp, true,
                        Modifier.alpha(1f - outP.value).scale(1f - 0.4f * outP.value)
                    )
                    ElementTile(level, 150.dp, true, Modifier.scale(pop.value).alpha(pop.value.coerceIn(0f, 1f)))
                }
                Spacer(Modifier.height(18.dp))
                Sc(elName(level), SInk, 26, true)
                Sc("${tr("Atomic number", "Atom numarası")} ${atomic(level)}", SMuted, 13)
                Spacer(Modifier.height(4.dp))
                Sc(up(T(Ranks.title(level))), STeal, 14, true, spacing = 3.sp)
                Sc(A(area), SMuted, 13)
                Spacer(Modifier.height(24.dp))
                Sc(T("Tap to continue"), SMuted, 12)
            }
        }
    }
}
