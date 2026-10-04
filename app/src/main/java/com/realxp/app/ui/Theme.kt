package com.realxp.app.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object RX {
    val Bg = Color(0xFF0B0D12)
    val Panel = Color(0xFF141821)
    val Line = Color(0xFF262C3A)
    val Accent = Color(0xFF3DDCFF)
    val Text = Color(0xFFEAF0FF)
    val Muted = Color(0xFF8A94A8)
}

@Composable
fun RealXpTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            background = RX.Bg,
            surface = RX.Panel,
            primary = RX.Accent,
            onBackground = RX.Text,
            onSurface = RX.Text
        ),
        content = content
    )
}
