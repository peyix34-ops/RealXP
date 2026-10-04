package com.realxp.app.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

object RX {
    val Bg = Color(0xFF060B1D)
    val BgTop = Color(0xFF0B1840)
    val Panel = Color(0xFF0D1731)
    val Line = Color(0xFF1B2B58)
    val Accent = Color(0xFF1EC8FF)
    val Blue = Color(0xFF1E6BFF)
    val Text = Color(0xFFF2F6FF)
    val Muted = Color(0xFF8EA0CC)
    val Button = Brush.horizontalGradient(listOf(Color(0xFF0A7BFF), Color(0xFF17C3FF)))
    val Page = Brush.verticalGradient(listOf(BgTop, Bg))
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
