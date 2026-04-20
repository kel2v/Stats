package com.example.stats.services

import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.example.stats.notification.StatsNotificationManager
import com.example.stats.repository.BatteryStateRepository
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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
        var _isRunning = MutableStateFlow(false)
        val isRunning = _isRunning.asStateFlow()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("DEBUGGING LOGS", "Running onStartCommand")

        scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

        statsNotificationManager.createStatsNotificationChannel()
        _isRunning.value = true
        startForeground(1, statsNotificationManager.buildStatsNotification(batteryStateRepository.batteryStateStateFlow.value))

        scope.launch {
            while(isActive) {
                val notificationManager = appContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                val notification = statsNotificationManager.buildStatsNotification(batteryStateRepository.batteryStateStateFlow.value)
                notificationManager.notify(1, notification)
                Log.d("DEBUGGING LOGS", "${notification.extras.getString(Notification.EXTRA_TITLE, "NA")}\n${notification.extras.getString(Notification.EXTRA_TEXT, "NA")}")
                delay(1000)
            }
        }

        Log.d("DEBUGGING LOGS", "Exiting onStartCommand")
        return START_STICKY
    }

    override fun onDestroy() {
        Log.d("DEBUGGING LOGS", "Running StatsNotificationService.onDestroy")
        scope.cancel()
        stopForeground(STOP_FOREGROUND_REMOVE)
        _isRunning.value = false
        super.onDestroy()
    }
}