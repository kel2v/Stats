package com.example.stats.notification

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.stats.data.BatteryStateRepository.batteryStateStateFlow

class StatsNotificationChannel(val channelId: String, val channelName: String, val importance: Int, val channelDescription: String = "") {
    companion object {
        lateinit var appContext: Context
        fun init(context: Context) {
            appContext = context.applicationContext
        }

        lateinit var channel: NotificationChannel
    }

    fun createChannel(): NotificationChannel {
        Log.d("PERMISSION DIALOG", "Creating notification channel")

        channel = NotificationChannel(channelId, channelName, importance).apply {
            description = channelDescription
        }

        val notificationManager = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

        return channel
    }

    fun postNotification(notification: Notification, id: Int) {
        with(NotificationManagerCompat.from(appContext)) {
            if(ContextCompat.checkSelfPermission(appContext, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                notify(id, notification)
                Log.d("PERMISSION DIALOG", "temp: ${batteryStateStateFlow.value.temperature} Celsius | level = ${batteryStateStateFlow.value.level}%\nvoltage: ${batteryStateStateFlow.value.voltage} V")
            } else {
                Log.d("PERMISSION DIALOG", "Unable to post notification. No permission to post")
            }
        }
    }
}