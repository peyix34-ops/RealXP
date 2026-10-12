package com.realxp.app.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.realxp.app.data.DayStat
import com.realxp.app.data.LevelInfo
import com.realxp.app.data.Levels
import com.realxp.app.data.LogEntry
import com.realxp.app.data.Store
import com.realxp.app.data.Xp
import kotlinx.coroutines.delay

object Chrome {
    var darkSurface by mutableStateOf(false)
}

@Composable
fun PickCheck(bg: Color, tint: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(16.dp)
            .background(bg, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(Icons.Outlined.Check, contentDescription = null, tint = tint, modifier = Modifier.size(11.dp))
    }
}

data class AreaStats(
    val todaySec: Int,
    val totalSec: Int,
    val count: Int,
    val week: List<DayStat>,
    val heat: List<List<Int?>>,
    val hist: List<LogEntry>,
    val xpToday: Int,
    val weekTotal: Int,
    val activeDays: Int
)

@Stable
class AreaState(val store: Store, val area: String, private val haptic: HapticFeedback) {
    var xp by mutableStateOf(store.xp(area))
    var rev by mutableStateOf(0)
    var tab by mutableStateOf(0)
    var input by mutableStateOf("")
    var error by mutableStateOf("")
    var pendingMin by mutableStateOf<Int?>(null)
    var bannerText by mutableStateOf<String?>(null)
    var shown by mutableStateOf("")
    var undoable by mutableStateOf(false)
    var bannerId by mutableStateOf(0)
    var levelUp by mutableStateOf<Int?>(null)
    var gained by mutableStateOf(1)
    var popText by mutableStateOf("")
    var popId by mutableStateOf(0)
    var lastMin by mutableStateOf(0)
    var lastGain by mutableStateOf(0)

    val info: LevelInfo
        get() = Levels.of(xp)

    private fun todayMin(): Double = store.secondsToday(area) / 60.0

    fun gainFor(m: Int): Int = Xp.forMinutes(todayMin() + m) - Xp.forMinutes(todayMin())

    fun fits(m: Int): Boolean = todayMin() + m <= Xp.DAY_MINUTES

    fun show(text: String, undo: Boolean) {
        shown = text
        bannerText = text
        undoable = undo
        bannerId++
    }

    fun doLog(m: Int) {
        val before = Levels.of(xp).level
        val res = store.logMinutes(area, m)
        if (!res.ok) {
            show(
                if (res.leftMin > 0) TF("A day has 24 hours. %d min left today.", res.leftMin)
                else T("A day only has 24 hours."),
                false
            )
            return
        }
        xp = store.xp(area)
        rev++
        input = ""
        error = ""
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        lastMin = m
        lastGain = res.gain
        if (res.gain > 0) {
            popText = "+${res.gain} XP"
            popId++
        }
        val after = Levels.of(xp).level
        if (after > before) {
            gained = after - before
            levelUp = after
        }
        show(TF("+%d XP  ·  %d min", res.gain, m), true)
    }

    fun request(m: Int) {
        if (store.honestyDue()) {
            store.markHonesty()
            pendingMin = m
        } else {
            doLog(m)
        }
    }

    fun submitInput() {
        val m = input.toIntOrNull()
        if (m == null || m < 1) {
            error = T("Type how many minutes you worked.")
        } else if (!fits(m)) {
            error = T("A day only has 24 hours.")
        } else {
            request(m)
        }
    }

    fun undo() {
        store.undoLast()
        xp = store.xp(area)
        rev++
        show(T("Undone."), false)
    }

    fun answerHonest(yes: Boolean) {
        val m = pendingMin
        pendingMin = null
        if (yes && m != null) doLog(m) else show(T("Respect for the honesty."), false)
    }
}

@Composable
fun rememberAreaState(store: Store, area: String): AreaState {
    val haptic = LocalHapticFeedback.current
    return remember(area) { AreaState(store, area, haptic) }
}

@Composable
fun AreaState.stats(): AreaStats {
    val r = rev
    return remember(r) {
        val today = store.secondsToday(area)
        val week = store.week(area)
        val heat = store.heat(16, area)
        AreaStats(
            today,
            store.totalSeconds(area),
            store.sessionCount(area),
            week,
            heat,
            store.history(area, 100),
            Xp.forMinutes(today / 60.0),
            week.sumOf { it.minutes },
            heat.sumOf { col -> col.count { (it ?: 0) > 0 } }
        )
    }
}

@Composable
fun HonestyHost(s: AreaState, panel: Color, text: Color, muted: Color, accent: Color) {
    val pm = s.pendingMin
    if (pm != null) {
        AlertDialog(
            onDismissRequest = {},
            containerColor = panel,
            title = { Text(T("Be honest."), color = text) },
            text = {
                Text(
                    T("You actually did this, right? Or are you just fooling yourself? \uD83D\uDE42"),
                    color = muted
                )
            },
            confirmButton = {
                TextButton(onClick = { s.answerHonest(true) }) {
                    Text(T("Yes, I did"), color = accent)
                }
            },
            dismissButton = {
                TextButton(onClick = { s.answerHonest(false) }) {
                    Text(T("Not really"), color = muted)
                }
            }
        )
    }
}

@Composable
fun BannerHost(
    s: AreaState,
    modifier: Modifier,
    panel: Color,
    accent: Color,
    text: Color,
    mono: Boolean = false
) {
    LaunchedEffect(s.bannerId) {
        if (s.bannerId > 0) {
            delay(if (s.undoable) 4500L else 2500L)
            s.bannerText = null
        }
    }
    AnimatedVisibility(
        visible = s.bannerText != null,
        modifier = modifier,
        enter = fadeIn() + slideInVertically { -it },
        exit = fadeOut()
    ) {
        Row(
            modifier = Modifier
                .background(panel, RoundedCornerShape(12.dp))
                .border(1.dp, accent, RoundedCornerShape(12.dp))
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                s.shown,
                color = accent, fontWeight = FontWeight.Bold, fontSize = 14.sp,
                fontFamily = if (mono) FontFamily.Monospace else FontFamily.Default
            )
            if (s.undoable) {
                Spacer(Modifier.width(14.dp))
                Text(
                    up(T("Undo")),
                    color = text, fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp,
                    modifier = Modifier.clickable { s.undo() }.padding(4.dp)
                )
            }
        }
    }
}

interface ArenaSkin {
    @Composable
    fun PickerTile(name: String, on: Boolean, modifier: Modifier, onClick: () -> Unit)

    @Composable
    fun HomeCard(
        name: String,
        store: Store,
        tick: Int,
        editing: Boolean,
        canRemove: Boolean,
        modifier: Modifier,
        onClick: () -> Unit
    )

    @Composable
    fun Screen(s: AreaState, onBack: () -> Unit)

    @Composable
    fun LevelUp(area: String, level: Int, gained: Int, onDismiss: () -> Unit)
}

fun skinFor(area: String): ArenaSkin = when (area) {
    "Coding" -> CodingFixed
    "English" -> EnglishFixed
    "History" -> HistorySkin
    "AI" -> AISkin
    else -> DefaultSkin
}

@Composable
fun LevelUpHost(s: AreaState, skin: ArenaSkin) {
    val lu = s.levelUp
    if (lu != null) {
        skin.LevelUp(s.area, lu, s.gained) { s.levelUp = null }
    }
}
