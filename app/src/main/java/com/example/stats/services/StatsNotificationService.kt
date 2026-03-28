package com.example.stats.services

import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.stats.data.BatteryStateRepository
import com.example.stats.notification.StatsNotificationManager
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class StatsNotificationService: Service() {
    @Inject lateinit var statsNotificationManager: StatsNotificationManager
    @Inject @ApplicationContext lateinit var appContext: Context
    @Inject lateinit var batteryStateRepository: BatteryStateRepository
    private lateinit var scope: CoroutineScope

    companion object {
        var isRunning by mutableStateOf(false)
            private set
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("PERMISSION DIALOG", "Running onStartCommand")

        scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

        statsNotificationManager.createStatsNotificationChannel()
        isRunning = true
        startForeground(1, statsNotificationManager.buildStatsNotification(batteryStateRepository.batteryStateStateFlow.value))

        scope.launch {
            while(isActive) {
                val notificationManager = appContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                val notification = statsNotificationManager.buildStatsNotification(batteryStateRepository.batteryStateStateFlow.value)
                notificationManager.notify(1, notification)
                Log.d("PERMISSION DIALOG", "${notification.extras.getString(Notification.EXTRA_TITLE, "NA")}\n${notification.extras.getString(Notification.EXTRA_TEXT, "NA")}")
                delay(1000)
            }
        }

        Log.d("PERMISSION DIALOG", "Exiting onStartCommand")
        return START_STICKY
    }

    override fun onDestroy() {
        Log.d("PERMISSION DIALOG", "Running StatsNotificationService.onDestroy")
        scope.cancel()
        stopForeground(STOP_FOREGROUND_REMOVE)
        isRunning = false
        super.onDestroy()
    }
}