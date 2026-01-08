package com.example.stats

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.stats.ui.bottombar.pages.BatteryStateRepository
import com.example.stats.ui.bottombar.pages.CurrentNowRepository

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        CurrentNowRepository.init(this.applicationContext)
        BatteryStateRepository.init(this.applicationContext)
        setContent {
            StatsApp()
        }
    }
}