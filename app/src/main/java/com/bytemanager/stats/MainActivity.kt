package com.bytemanager.stats

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.os.Bundle
import android.os.PowerManager
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.bytemanager.stats.data.repository.DataStoreRepository
import com.bytemanager.stats.services.notification.StatsLoggingService
import com.bytemanager.stats.ui.theme.StatsTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity: ComponentActivity() {
    @Inject
    lateinit var dataStoreRepository: DataStoreRepository

    @SuppressLint("BatteryLife")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("MainActivity", "running onCreate")
        enableEdgeToEdge()

        lifecycleScope.launch {
            if(dataStoreRepository.isDataLoggingEnabled()) {
                StatsLoggingService.startForegroundService(applicationContext)
            }
        }

        setContent {
            StatsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    StatsApp()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        val powerManager = getSystemService(PowerManager::class.java)
        val exempt = powerManager.isIgnoringBatteryOptimizations(packageName)
        lifecycleScope.launch {
            dataStoreRepository.setIsIgnoringBatteryOptimizations(exempt)
        }
        Log.d("MainActivity", "battery optimization exemption: ${if(exempt) "Exempted" else "Not exempted"}")


        val notificationManager = getSystemService(NotificationManager::class.java)
        val areNotificationsEnabled = notificationManager.areNotificationsEnabled()
        lifecycleScope.launch { dataStoreRepository.setIsNotificationsEnabled(areNotificationsEnabled) }
        Log.d("MainActivity", "areNotificationsEnabled = $areNotificationsEnabled")
    }
}