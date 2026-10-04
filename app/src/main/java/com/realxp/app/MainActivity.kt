package com.realxp.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.realxp.app.data.Store
import com.realxp.app.ui.App
import com.realxp.app.ui.RealXpTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val store = Store(applicationContext)
        setContent {
            RealXpTheme {
                App(store)
            }
        }
    }
}
