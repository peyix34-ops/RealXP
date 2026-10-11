package com.realxp.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.realxp.app.data.Levels
import com.realxp.app.data.Store

@Composable
fun ArenaPicker2(
    options: List<String>,
    title: String,
    buttonLabel: String,
    onBack: (() -> Unit)?,
    onDone: (List<String>) -> Unit
) {
    var selected by remember { mutableStateOf(listOf<String>()) }
    var cat by remember { mutableStateOf("All") }
    val ready = selected.isNotEmpty()
    val shownOpts = if (cat == "All") options else options.filter { Catalog.categoryOf(it) == cat }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RX.Page)
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(16.dp))
            if (onBack != null) {
                Text(
                    T("←  Back"),
                    color = RX.Muted, fontSize = 14.sp,
                    modifier = Modifier.clickable { onBack() }.padding(vertical = 8.dp)
                )
                Spacer(Modifier.height(4.dp))
            }
            Text(
                if (onBack == null) T("CHOOSE YOUR AREAS") else T("ADD AREAS"),
                color = RX.Accent, fontSize = 11.sp, letterSpacing = 3.sp, fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            Text(
                title,
                color = RX.Text, fontSize = 24.sp, fontWeight = FontWeight.Bold, lineHeight = 30.sp
            )
            Spacer(Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())) {
                Catalog.categories.forEach { c ->
                    MonthChip(T(c), c == cat) { cat = c }
                    Spacer(Modifier.width(8.dp))
                }
            }
            Spacer(Modifier.height(18.dp))
            if (options.isEmpty()) {
                Text(T("You've added every arena."), color = RX.Muted, fontSize = 15.sp)
            } else if (shownOpts.isEmpty()) {
                Text(T("Nothing left in this category."), color = RX.Muted, fontSize = 15.sp)
            }
            key(cat) {
                Column {
                    shownOpts.chunked(3).forEachIndexed { r, row ->
                        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)) {
                            row.forEachIndexed { i, name ->
                                if (i > 0) Spacer(Modifier.width(10.dp))
                                skinFor(name).PickerTile(
                                    name, name in selected,
                                    Modifier.weight(1f).staggerIn(minOf(r * 3 + i, 12), 35)
                                ) {
                                    selected = if (name in selected) selected - name else selected + name
                                }
                            }
                            repeat(3 - row.size) {
                                Spacer(Modifier.width(10.dp))
                                Spacer(Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
        }
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp)) {
            Text(
                if (ready) TF("%d selected", selected.size) else T("Select at least one area to continue."),
                color = RX.Muted, fontSize = 12.sp
            )
            Spacer(Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(
                        if (ready) Modifier.background(RX.Button, RoundedCornerShape(50))
                        else Modifier.border(1.dp, RX.Line, RoundedCornerShape(50))
                    )
                    .clickable(enabled = ready) { onDone(selected) }
                    .padding(vertical = 15.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "$buttonLabel  →",
                    color = if (ready) Color.White else RX.Muted,
                    fontSize = 14.sp, fontWeight = FontWeight.Bold, letterSpacing = 3.sp
                )
            }
        }
    }
}

@Composable
fun HomeHub2(
    store: Store,
    chosen: List<String>,
    tick: Int,
    onOpen: (String) -> Unit,
    onAdd: () -> Unit,
    onRemove: (String, Boolean) -> Unit,
    onToggleDark: () -> Unit,
    onSetLang: (Boolean) -> Unit,
    onProfile: () -> Unit,
    onReport: (Int, Int) -> Unit
) {
    val total = remember(tick) { store.totalXp() }
    val investedSec = remember(tick) { store.allSeconds() }
    val streak = remember(tick) { store.streak() }
    val pending = remember(tick) { store.pendingReport() }
    val info = Levels.overall(total)
    val p = animatedProgress(info.into.toFloat() / info.need, 1400)
    val intoShown = countUp(info.into, 1200)
    var editing by remember { mutableStateOf(false) }
    var toRemove by remember { mutableStateOf<String?>(null) }
    var showSettings by remember { mutableStateOf(false) }

    val rm = toRemove
    if (rm != null) {
        AlertDialog(
            onDismissRequest = { toRemove = null },
            containerColor = RX.Panel,
            title = { Text(TF("Remove %s?", A(rm)), color = RX.Text) },
            text = {
                Column {
                    Text(
                        T("Keep your progress hidden, or erase it for good?"),
                        color = RX.Muted, fontSize = 14.sp
                    )
                    Spacer(Modifier.height(16.dp))
                    DialogButton(T("Remove, keep progress"), RX.Accent) {
                        toRemove = null
                        onRemove(rm, false)
                    }
                    DialogButton(T("Remove and erase progress"), Danger) {
                        toRemove = null
                        onRemove(rm, true)
                    }
                    DialogButton(T("Cancel"), RX.Muted) { toRemove = null }
                }
            },
            confirmButton = {}
        )
    }

    if (showSettings) {
        AlertDialog(
            onDismissRequest = { showSettings = false },
            containerColor = RX.Panel,
            title = { Text(T("Settings"), color = RX.Text, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(T("Dark mode"), color = RX.Text, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                            Text(T("Easier on the eyes at night."), color = RX.Muted, fontSize = 12.sp)
                        }
                        Switch(
                            checked = RX.dark,
                            onCheckedChange = { onToggleDark() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = RX.Accent,
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = RX.Line,
                                uncheckedBorderColor = RX.Line
                            )
                        )
                    }
                    Spacer(Modifier.height(20.dp))
                    Text(T("Language"), color = RX.Text, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(10.dp))
                    Row {
                        MonthChip("English", !Lang.tr) { onSetLang(false) }
                        Spacer(Modifier.width(8.dp))
                        MonthChip("Türkçe", Lang.tr) { onSetLang(true) }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSettings = false }) {
                    Text(T("Done"), color = RX.Accent)
                }
            }
        )
    }

    val cells = buildList<String?> {
        addAll(chosen)
        if (!editing && chosen.size < Catalog.all.size) add(null)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RX.Page)
            .systemBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(14.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                buildAnnotatedString {
                    withStyle(SpanStyle(color = RX.Text)) { append("REAL ") }
                    withStyle(SpanStyle(color = RX.Accent)) { append("XP") }
                },
                fontSize = 18.sp, fontWeight = FontWeight.ExtraBold
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                RoundIconButton(Icons.Outlined.Person) { onProfile() }
                Spacer(Modifier.width(8.dp))
                RoundIconButton(Icons.Outlined.Settings) { showSettings = true }
            }
        }
        Spacer(Modifier.height(16.dp))
        HeroRing(p, info.level)
        Spacer(Modifier.height(12.dp))
        Text(
            "$intoShown / ${info.need} XP   ·   ${ftime(investedSec)} ${T("invested")}",
            color = RX.Muted, fontSize = 12.sp
        )
        if (streak > 0) {
            Spacer(Modifier.height(12.dp))
            StreakPill(streak)
        }
        if (pending != null) {
            Spacer(Modifier.height(16.dp))
            ReportCard(
                TF("%s report is ready", monthName(pending.second)),
                T("See how your month went"), true
            ) { onReport(pending.first, pending.second) }
        }
        Spacer(Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(T("YOUR ARENAS"), color = RX.Accent, fontSize = 11.sp, letterSpacing = 3.sp, fontWeight = FontWeight.Bold)
            if (chosen.size > 1) {
                Text(
                    if (editing) T("DONE") else T("EDIT"),
                    color = RX.Accent, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp,
                    modifier = Modifier.clickable { editing = !editing }.padding(vertical = 6.dp)
                )
            }
        }
        Spacer(Modifier.height(10.dp))
        cells.chunked(2).forEachIndexed { r, row ->
            Row(modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)) {
                row.forEachIndexed { c, name ->
                    if (c > 0) Spacer(Modifier.width(10.dp))
                    val idx = r * 2 + c
                    if (name == null) {
                        AddCell(Modifier.weight(1f).staggerIn(minOf(idx, 12))) { onAdd() }
                    } else {
                        skinFor(name).HomeCard(
                            name, store, tick, editing, chosen.size > 1,
                            Modifier.weight(1f).staggerIn(minOf(idx, 12))
                        ) {
                            if (editing) {
                                if (chosen.size > 1) toRemove = name
                            } else {
                                onOpen(name)
                            }
                        }
                    }
                }
                repeat(2 - row.size) {
                    Spacer(Modifier.width(10.dp))
                    Spacer(Modifier.weight(1f))
                }
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}
