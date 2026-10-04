package com.realxp.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.realxp.app.ui.RealXpTheme
import com.realxp.app.ui.WelcomeScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RealXpTheme {
                WelcomeScreen(onStart = {})
            }
        }
    }
}
