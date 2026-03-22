package com.example.stats.notification

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.stats.MainActivity
import com.example.stats.R
import com.example.stats.data.BatteryState
import com.example.stats.data.BatteryStateRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class StatsNotificationManager @Inject constructor(@ApplicationContext private val appContext: Context) {
    private lateinit var callbackCoroutine: Job
    @Inject
    lateinit var channel: StatsNotificationChannel

    fun createStatsNotificationChannel() {
        Log.d("PERMISSION DIALOG", "Creating notification channel")

        channel.init(
            _channelId = "Stats",
            _channelName = "Battery info",
            _importance = NotificationManager.IMPORTANCE_DEFAULT,
            _channelDescription = "battery temperature, battery level and battery voltage"
        )
        channel.createChannel()
    }

    fun startStatsNotificationBroadcast() {
        Log.d("PERMISSION DIALOG", "Starting notification broadcast")
        callbackCoroutine = CoroutineScope(SupervisorJob() + Dispatchers.Default).launch {
            while(true) {
                showStatsLocalNotification(BatteryStateRepository.batteryStateStateFlow.value)
                delay(1000)
            }
        }
    }

    fun buildStatsNotification(): Notification {
        val channelId = "Stats"
        val intent = Intent(appContext, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            appContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(appContext, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("temp: ${BatteryStateRepository.batteryStateStateFlow.value.temperature} Celsius | level = ${BatteryStateRepository.batteryStateStateFlow.value.level}%")
            .setContentText("voltage: ${BatteryStateRepository.batteryStateStateFlow.value.voltage} V")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setSilent(true)
            .build()

        return notification
    }

    private fun showStatsLocalNotification(batteryState: BatteryState) {
        Log.d("PERMISSION DIALOG", "posting new notification ...")

        val notification = buildStatsNotification()
        channel.postNotification(notification, 1001)
    }

    fun destroyStatsNotificationBroadcast() {
        Log.d("PERMISSION DIALOG", "Destroying notification broadcast")
        callbackCoroutine.cancel()
    }
}