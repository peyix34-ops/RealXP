package com.realxp.app.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

object RX {
    var dark by mutableStateOf(false)

    val Bg: Color
        get() = if (dark) Color(0xFF060B1D) else Color(0xFFF7FAFF)
    val BgTop: Color
        get() = if (dark) Color(0xFF0B1840) else Color(0xFFE6EFFF)
    val Panel: Color
        get() = if (dark) Color(0xFF0D1731) else Color(0xFFFFFFFF)
    val Line: Color
        get() = if (dark) Color(0xFF1B2B58) else Color(0xFFDCE6F7)
    val Accent: Color
        get() = if (dark) Color(0xFF1EC8FF) else Color(0xFF1A6CF0)
    val Blue: Color
        get() = Color(0xFF1E6BFF)
    val Text: Color
        get() = if (dark) Color(0xFFF2F6FF) else Color(0xFF0B1B3F)
    val Muted: Color
        get() = if (dark) Color(0xFF8EA0CC) else Color(0xFF6F7F9E)
    val Button: Brush
        get() = if (dark) {
            Brush.horizontalGradient(listOf(Color(0xFF0A7BFF), Color(0xFF17C3FF)))
        } else {
            Brush.horizontalGradient(listOf(Color(0xFF1565FF), Color(0xFF2F8CFF)))
        }
    val Page: Brush
        get() = Brush.verticalGradient(listOf(BgTop, Bg))
}

@Composable
fun RealXpTheme(content: @Composable () -> Unit) {
    val scheme = if (RX.dark) {
        darkColorScheme(
            background = RX.Bg,
            surface = RX.Panel,
            primary = RX.Accent,
            onBackground = RX.Text,
            onSurface = RX.Text
        )
    } else {
        lightColorScheme(
            background = RX.Bg,
            surface = RX.Panel,
            primary = RX.Accent,
            onBackground = RX.Text,
            onSurface = RX.Text
        )
    }
    MaterialTheme(colorScheme = scheme, content = content)
}
