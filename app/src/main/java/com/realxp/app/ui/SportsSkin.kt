package com.realxp.app.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.realxp.app.data.Store

private val SportBg = Color(0xFF090E12)
private val SportPanel = Color(0xFF121B22)
private val SportPanelLight = Color(0xFF19252D)
private val SportGreen = Color(0xFFB6F36A)
private val SportGreenDark = Color(0xFF6FAF32)
private val SportWhite = Color(0xFFF1F5F0)
private val SportMuted = Color(0xFF8D9B9F)
private val SportLine = Color(0xFF29363B)
private val SportOrange = Color(0xFFFF9B54)

object SportsSkin : ArenaSkin {

    @Composable
    override fun PickerTile(
        name: String,
        on: Boolean,
        modifier: Modifier,
        onClick: () -> Unit
    ) {
        ArenaChoice(name, on, modifier, onClick)
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
        SportHomeCard(
            name = name,
            store = store,
            tick = tick,
            modifier = modifier,
            onClick = onClick
        )
    }

    @Composable
    override fun Screen(s: AreaState, onBack: () -> Unit) {
        SportsScreen(s, onBack)
    }

    @Composable
    override fun LevelUp(
        area: String,
        level: Int,
        gained: Int,
        onDismiss: () -> Unit
    ) {
        LevelUpOverlay(area, level, gained, onDismiss)
    }
}

@Composable
private fun SportHomeCard(
    name: String,
    store: Store,
    tick: Int,
    modifier: Modifier,
    onClick: () -> Unit
) {
    val xp = remember(tick) { store.xp(name) }
    val info = remember(xp) { com.realxp.app.data.Levels.of(xp) }
    val progress by animateFloatAsState(
        targetValue = (info.into.toFloat() / info.need.coerceAtLeast(1))
            .coerceIn(0f, 1f),
        animationSpec = tween(700),
        label = "sportHomeProgress"
    )

    Column(
        modifier = modifier
            .height(132.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0xFF263C27),
                        SportPanel,
                        Color(0xFF11191C)
                    )
                )
            )
            .border(1.dp, SportGreen.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(SportGreen.copy(alpha = 0.12f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.FitnessCenter,
                    contentDescription = null,
                    tint = SportGreen,
                    modifier = Modifier.size(23.dp)
                )
            }

            Spacer(Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    name.uppercase(),
                    color = SportWhite,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1
                )
                Text(
                    "LEVEL ${info.level}",
                    color = SportGreen,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
            }

            Icon(
                Icons.Filled.Bolt,
                contentDescription = null,
                tint = SportGreen,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("TRAINING XP", color = SportMuted, fontSize = 10.sp, letterSpacing = 1.5.sp)
            Text("$xp XP", color = SportGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(7.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(7.dp)
                .clip(CircleShape)
                .background(SportLine)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .fillMaxHeight()
                    .clip(CircleShape)
                    .background(
                        Brush.horizontalGradient(
                            listOf(SportGreenDark, SportGreen)
                        )
                    )
            )
        }
    }
}

@Composable
private fun SportsScreen(s: AreaState, onBack: () -> Unit) {
    val info = s.info
    val stats = s.stats()
    val progress by animateFloatAsState(
        targetValue = (info.into.toFloat() / info.need.coerceAtLeast(1))
            .coerceIn(0f, 1f),
        animationSpec = tween(850, easing = FastOutSlowInEasing),
        label = "sportLevelProgress"
    )
    val pulse by rememberInfiniteTransition(label = "sportPulse").animateFloat(
        initialValue = 0.72f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sportPulseAlpha"
    )

    val typed = s.input.toIntOrNull()

    HonestyHost(
        s = s,
        panel = SportPanel,
        text = SportWhite,
        muted = SportMuted,
        accent = SportGreen
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SportBg)
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp)
        ) {
            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onBack() }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Filled.ArrowBack,
                    contentDescription = null,
                    tint = SportMuted,
                    modifier = Modifier.size(19.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    T("Back"),
                    color = SportMuted,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(Modifier.weight(1f))
                Text(
                    "REALXP / SPORT",
                    color = SportGreen.copy(alpha = 0.8f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
            }

            Spacer(Modifier.height(16.dp))

            // PERFORMANCE HERO
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(26.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(
                                Color(0xFF263D26),
                                Color(0xFF17251E),
                                Color(0xFF121B20)
                            )
                        )
                    )
                    .border(
                        1.dp,
                        SportGreen.copy(alpha = 0.3f),
                        RoundedCornerShape(26.dp)
                    )
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "YOUR TRAINING",
                            color = SportGreen.copy(alpha = 0.9f),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.5.sp
                        )

                        Spacer(Modifier.height(8.dp))

                        Text(
                            "BUILD YOUR\nSTRONGER SELF.",
                            color = SportWhite,
                            fontSize = 27.sp,
                            lineHeight = 32.sp,
                            fontWeight = FontWeight.Black
                        )

                        Spacer(Modifier.height(10.dp))

                        Text(
                            "Every minute counts. Keep showing up.",
                            color = SportMuted,
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .background(
                                SportGreen.copy(alpha = 0.10f),
                                CircleShape
                            )
                            .border(
                                1.dp,
                                SportGreen.copy(alpha = 0.35f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.FitnessCenter,
                            contentDescription = null,
                            tint = SportGreen.copy(alpha = pulse),
                            modifier = Modifier.size(34.dp)
                        )
                    }
                }

                Spacer(Modifier.height(25.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "CURRENT LEVEL",
                            color = SportMuted,
                            fontSize = 10.sp,
                            letterSpacing = 1.5.sp
                        )
                        Spacer(Modifier.height(3.dp))
                        Text(
                            "${info.level}",
                            color = SportGreen,
                            fontSize = 46.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            "TOTAL XP",
                            color = SportMuted,
                            fontSize = 10.sp,
                            letterSpacing = 1.5.sp
                        )
                        Spacer(Modifier.height(5.dp))
                        Text(
                            "${s.xp} XP",
                            color = SportWhite,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                Spacer(Modifier.height(13.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.08f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress)
                            .fillMaxHeight()
                            .clip(CircleShape)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(SportGreenDark, SportGreen)
                                )
                            )
                    )
                }

                Spacer(Modifier.height(9.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "${info.into} / ${info.need} XP",
                        color = SportGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "${(progress * 100).toInt()}% COMPLETE",
                        color = SportMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(22.dp))

            // QUICK TRAINING LOG
            SportSectionTitle("LOG YOUR TRAINING", "01")

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(9.dp)
            ) {
                listOf(15, 30, 45, 60).forEach { minutes ->
                    val fits = s.fits(minutes)

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (fits) SportPanel else SportPanel.copy(alpha = 0.5f)
                            )
                            .border(
                                1.dp,
                                if (fits) SportGreen.copy(alpha = 0.25f) else SportLine,
                                RoundedCornerShape(16.dp)
                            )
                            .clickable(enabled = fits) { s.request(minutes) }
                            .padding(vertical = 15.dp, horizontal = 3.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "$minutes",
                            color = if (fits) SportWhite else SportMuted,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            "MIN",
                            color = SportMuted,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            if (fits) "+${s.gainFor(minutes)} XP" else "LIMIT",
                            color = if (fits) SportGreen else SportMuted,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = s.input,
                    onValueChange = {
                        s.input = it.filter(Char::isDigit).take(4)
                        s.error = ""
                    },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    label = { Text("Minutes") },
                    placeholder = { Text("Enter duration") },
                    shape = RoundedCornerShape(15.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = SportWhite,
                        unfocusedTextColor = SportWhite,
                        focusedBorderColor = SportGreen,
                        unfocusedBorderColor = SportLine,
                        focusedContainerColor = SportPanel,
                        unfocusedContainerColor = SportPanel,
                        cursorColor = SportGreen,
                        focusedLabelColor = SportGreen,
                        unfocusedLabelColor = SportMuted
                    )
                )

                Spacer(Modifier.width(9.dp))

                Box(
                    modifier = Modifier
                        .height(56.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(SportGreen, Color(0xFF8BD64A))
                            )
                        )
                        .clickable { s.submitInput() }
                        .padding(horizontal = 17.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "LOG IT",
                        color = Color(0xFF11190D),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(Modifier.height(7.dp))

            Text(
                when {
                    s.error.isNotEmpty() -> s.error
                    typed != null && typed > 0 && s.fits(typed) ->
                        "+${s.gainFor(typed)} XP  ·  $typed min"
                    typed != null && typed > 0 ->
                        T("A day only has 24 hours.")
                    else -> "Record your completed exercise time."
                },
                color = if (s.error.isNotEmpty()) SportOrange else SportMuted,
                fontSize = 11.sp
            )

            Spacer(Modifier.height(24.dp))

            // TODAY'S PERFORMANCE
            SportSectionTitle("PERFORMANCE DATA", "02")

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SportStatCard(
                    modifier = Modifier.weight(1f),
                    icon = {
                        Icon(
                            Icons.Filled.LocalFireDepartment,
                            contentDescription = null,
                            tint = SportOrange,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    label = "TODAY",
                    value = ftime(stats.todaySec)
                )

                SportStatCard(
                    modifier = Modifier.weight(1f),
                    icon = {
                        Icon(
                            Icons.Filled.EmojiEvents,
                            contentDescription = null,
                            tint = SportGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    label = "ALL TIME",
                    value = ftime(stats.totalSec)
                )
            }

            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SportStatCard(
                    modifier = Modifier.weight(1f),
                    icon = {
                        Icon(
                            Icons.Filled.Bolt,
                            contentDescription = null,
                            tint = Color(0xFF69CFFF),
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    label = "SESSIONS",
                    value = "${stats.count}"
                )

                SportStatCard(
                    modifier = Modifier.weight(1f),
                    icon = {
                        Icon(
                            Icons.Filled.FitnessCenter,
                            contentDescription = null,
                            tint = SportGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    label = "THIS WEEK",
                    value = ftime(stats.weekTotal * 60)
                )
            }

            Spacer(Modifier.height(24.dp))

            // MOTIVATION FOOTER
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(SportPanel)
                    .border(1.dp, SportLine, RoundedCornerShape(18.dp))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(43.dp)
                        .background(SportGreen.copy(alpha = 0.12f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.EmojiEvents,
                        contentDescription = null,
                        tint = SportGreen,
                        modifier = Modifier.size(23.dp)
                    )
                }

                Spacer(Modifier.width(12.dp))

                Column {
                    Text(
                        "DISCIPLINE BUILDS RESULTS",
                        color = SportGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.7.sp
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Small sessions. Consistent effort. Real progress.",
                        color = SportMuted,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(Modifier.height(30.dp))
        }

        AnimatedVisibility(
            visible = s.bannerText != null,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(horizontal = 18.dp, vertical = 14.dp),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(15.dp))
                    .background(SportPanelLight)
                    .border(1.dp, SportGreen.copy(alpha = 0.45f), RoundedCornerShape(15.dp))
                    .padding(horizontal = 15.dp, vertical = 13.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    s.shown,
                    color = SportGreen,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                if (s.undoable) {
                    Text(
                        T("Undo"),
                        color = SportWhite,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable { s.undo() }
                            .padding(start = 12.dp, vertical = 5.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SportSectionTitle(title: String, number: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            number,
            color = SportGreen,
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp
        )

        Spacer(Modifier.width(10.dp))

        Text(
            title,
            color = SportWhite,
            fontSize = 12.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.6.sp
        )

        Spacer(Modifier.width(10.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .height(
