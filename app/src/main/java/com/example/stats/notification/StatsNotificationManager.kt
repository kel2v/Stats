package com.example.stats.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.stats.MainActivity
import com.example.stats.R
import com.example.stats.data.BatteryState
import com.example.stats.data.BatteryStateRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


object StatsNotificationManager {
    private lateinit var appContext: Context
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private lateinit var callbackCoroutine: Job

    fun init(context: Context) {
        appContext = context.applicationContext
        createStatsNotificationChannel()
    }

    private fun createStatsNotificationChannel() {
        Log.d("PERMISSION DIALOG", "Creating notification channel")

        val channelId = "Stats"
        val channelName = "Battery info"
        val channelDescription = "battery parameters updates"
        val importance = NotificationManager.IMPORTANCE_DEFAULT

        val channel = NotificationChannel(channelId, channelName, importance).apply {
            description = channelDescription
        }

        val notificationManager = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    fun startStatsNotificationBroadcast() {
        Log.d("PERMISSION DIALOG", "Starting notification broadcast")
        callbackCoroutine = scope.launch {
            while(true) {
                showStatsLocalNotification(BatteryStateRepository.batteryStateStateFlow.value)
                delay(1000)
            }
        }
    }

    private fun showStatsLocalNotification(batteryState: BatteryState) {
        Log.d("PERMISSION DIALOG", "posting new notification ...")

        val channelId = "Stats"

        // Optional: tap notification to open MainActivity
        val intent = Intent(appContext, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            appContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(appContext, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("temp: ${batteryState.temperature} Celsius | level = ${batteryState.level}%")
            .setContentText("voltage: ${batteryState.voltage} V")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setSilent(true)
            .build()

        with(NotificationManagerCompat.from(appContext)) {
            if(ContextCompat.checkSelfPermission(appContext, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                notify(1001, notification)
                Log.d("PERMISSION DIALOG", "temp: ${batteryState.temperature} Celsius | level = ${batteryState.level}%\nvoltage: ${batteryState.voltage} V")
            } else {
                Log.d("PERMISSION DIALOG", "Unable to post notification. No permission to post")
            }
        }
    }

    fun destroyStatsNotificationBroadcast() {
        Log.d("PERMISSION DIALOG", "Destroying notification broadcast")
        callbackCoroutine.cancel()
    }
}