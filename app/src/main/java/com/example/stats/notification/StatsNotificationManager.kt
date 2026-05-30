package com.example.stats.notification

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.stats.MainActivity
import com.example.stats.R
import com.example.stats.data_structure.BatteryState
import com.example.stats.services.StatsLoggingNotificationService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

object StatsNotificationManager {
    var notificationPermissionGranted = false

    fun createStatsNotificationChannel(appContext: Context) {
        Log.d("DEBUGGING LOGS", "Creating notification channel")

        val channel = NotificationChannel("Stats", "Battery info", NotificationManager.IMPORTANCE_DEFAULT).apply {
            description = "battery temperature, battery level and battery voltage"
        }

        val notificationManager = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    fun closeStatsNotificationChannel(appContext: Context) {
        Log.d("DEBUGGING LOGS", "Closing notification channel")

        val notificationManager = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.deleteNotificationChannel("Stats")
    }

    fun buildStatsNotification(appContext: Context, batteryState: BatteryState): Notification {
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
            .setContentTitle("temp: ${batteryState.temperature} Celsius | level = ${batteryState.level}%")
            .setContentText("voltage: ${batteryState.voltage} V")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setSilent(true)
            .build()

        return notification
    }
}