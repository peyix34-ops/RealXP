package com.realxp.app.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.realxp.app.data.Store
import java.util.Calendar

object DefaultSkin : ArenaSkin {
    @Composable
    override fun PickerTile(name: String, on: Boolean, modifier: Modifier, onClick: () -> Unit) =
        ArenaChoice(name, on, modifier, onClick)

    @Composable
    override fun HomeCard(
        name: String,
        store: Store,
        tick: Int,
        editing: Boolean,
        canRemove: Boolean,
        modifier: Modifier,
        onClick: () -> Unit
    ) = ArenaCard2(name, store, tick, editing, canRemove, modifier, onClick)

    @Composable
    override fun Screen(s: AreaState, onBack: () -> Unit) = DefaultAreaScreen(s, onBack)

    @Composable
    override fun LevelUp(area: String, level: Int, gained: Int, onDismiss: () -> Unit) =
        LevelUpOverlay(area, level, gained, onDismiss)
}

@Composable
fun DefaultAreaScreen(s: AreaState, onBack: () -> Unit) {
    val accent = arenaAccent(s.area)
    val info = s.info
    val barP = animatedXpBar(info.level, info.into.toFloat() / info.need)
    val st = s.stats()
    val xpTodayShown = animatedCount(st.xpToday)
    val todayIdx = (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) + 5) % 7
    val weekLabels = (6 downTo 0).map { dayLetter(((todayIdx - it) % 7 + 7) % 7) }
    val typed = s.input.toIntOrNull()

    HonestyHost(s, RX.Panel, RX.Text, RX.Muted, RX.Accent)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(RX.Page)
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(10.dp))
            Text(
                T("←  Back"),
                color = RX.Muted, fontSize = 14.sp,
                modifier = Modifier.clickable { onBack() }.padding(vertical = 8.dp)
            )
            Spacer(Modifier.height(8.dp))
            AreaHero(s.area, info, barP, s.popId)

            Spacer(Modifier.height(14.dp))
            TabBar(s.tab) { s.tab = it }
            Spacer(Modifier.height(20.dp))

            AnimatedContent(
                targetState = s.tab,
                transitionSpec = { fadeIn(tween(240)) togetherWith fadeOut(tween(120)) },
                label = "tab"
            ) { t ->
                Column(modifier = Modifier.fillMaxWidth()) {
                    if (t == 0) {
                        Text(T("LOG YOUR WORK"), color = RX.Muted, fontSize = 11.sp, letterSpacing = 3.sp)
                        Spacer(Modifier.height(10.dp))
                        Row(modifier = Modifier.fillMaxWidth()) {
                            listOf(15, 30, 60, 90).forEachIndexed { i, m ->
                                if (i > 0) Spacer(Modifier.width(8.dp))
                                val ok = s.fits(m)
                                QuickLog(
                                    m,
                                    if (ok) "+${s.gainFor(m)} XP" else "24${T("h")}",
                                    ok, accent,
                                    Modifier.weight(1f)
                                ) { s.request(m) }
                            }
                        }
                        Spacer(Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = s.input,
                                onValueChange = { v ->
                                    s.input = v.filter { it.isDigit() }.take(4)
                                    s.error = ""
                                },
                                label = { Text(T("Other (min)")) },
                                singleLine = true,
                                shape = RoundedCornerShape(14.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = RX.Text,
                                    unfocusedTextColor = RX.Text,
                                    focusedBorderColor = accent,
                                    unfocusedBorderColor = RX.Line,
                                    focusedContainerColor = RX.Panel,
                                    unfocusedContainerColor = RX.Panel,
                                    cursorColor = accent,
                                    focusedLabelColor = accent,
                                    unfocusedLabelColor = RX.Muted
                                )
                            )
                            Spacer(Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .bounceClick { s.submitInput() }
                                    .width(84.dp)
                                    .height(56.dp)
                                    .background(
                                        Brush.horizontalGradient(listOf(accent, accent.copy(alpha = 0.78f))),
                                        RoundedCornerShape(14.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(T("LOG"), color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                            }
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(
                            when {
                                s.error.isNotEmpty() -> s.error
                                typed != null && typed >= 1 && s.fits(typed) ->
                                    TF("+%d XP  ·  %d min", s.gainFor(typed), typed)
                                typed != null && typed >= 1 -> T("A day only has 24 hours.")
                                else -> T("Type any amount, for example 5.")
                            },
                            color = if (s.error.isNotEmpty()) Danger else RX.Muted,
                            fontSize = 12.sp
                        )

                        Spacer(Modifier.height(20.dp))
                        SectionCard(T("TODAY'S XP")) {
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    "+$xpTodayShown",
                                    color = accent, fontSize = 38.sp, fontWeight = FontWeight.ExtraBold
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    "XP", color = RX.Muted, fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 7.dp)
                                )
                            }
                            Spacer(Modifier.height(14.dp))
                            Box(Modifier.fillMaxWidth().height(1.dp).background(RX.Line))
                            Spacer(Modifier.height(14.dp))
                            Row(modifier = Modifier.fillMaxWidth()) {
                                MiniStat(T("TODAY"), ftime(st.todaySec), Modifier.weight(1f))
                                MiniStat(T("TOTAL"), ftime(st.totalSec), Modifier.weight(1f))
                                MiniStat(T("LOGS"), "${st.count}", Modifier.weight(1f))
                            }
                        }

                        Spacer(Modifier.height(14.dp))
                        SectionCard(T("LAST 7 DAYS"), ftime(st.weekTotal * 60)) {
                            WeekChart(st.week, accent, weekLabels)
                        }

                        Spacer(Modifier.height(14.dp))
                        SectionCard(T("LAST 16 WEEKS"), daysText(st.activeDays)) {
                            Heatmap(st.heat)
                            Spacer(Modifier.height(8.dp))
                            HeatLegend()
                        }
                    } else {
                        Text(
                            TF("YOUR LOGS  ·  %d", st.hist.size),
                            color = RX.Muted, fontSize = 11.sp, letterSpacing = 3.sp
                        )
                        Spacer(Modifier.height(12.dp))
                        if (st.hist.isEmpty()) {
                            Text(
                                T("No logs yet. Log your first session in Overview."),
                                color = RX.Muted, fontSize = 14.sp
                            )
                        }
                        st.hist.forEach { e -> HistoryRow(e) }
                    }
                }
            }
            Spacer(Modifier.height(32.dp))
        }

        FloatingGain(
            s.popText, s.popId,
            Modifier.align(Alignment.TopCenter).padding(top = 150.dp)
        )
        BannerHost(s, Modifier.align(Alignment.TopCenter).padding(top = 10.dp), RX.Panel, accent, RX.Text)
        LevelUpHost(s, DefaultSkin)
    }
}
