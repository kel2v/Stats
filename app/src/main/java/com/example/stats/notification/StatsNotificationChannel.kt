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
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class StatsNotificationChannel @Inject constructor(@ApplicationContext private val appContext: Context) {
    private lateinit var channelId: String
    private lateinit var channelName: String
    private var importance: Int = 0
    private lateinit var channelDescription: String

    private lateinit var channel: NotificationChannel


    fun init(
        _channelId: String,
        _channelName: String,
        _importance: Int,
        _channelDescription: String
    ) {
        channelId = _channelId
        channelName = _channelName
        importance = _importance
        channelDescription = _channelDescription
    }

    fun createChannel() {
        Log.d("DEBUGGING LOGS", "Creating notification channel")

        channel = NotificationChannel(channelId, channelName, importance).apply {
            description = channelDescription
        }

        val notificationManager = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    fun postNotification(notification: Notification, id: Int) {
        with(NotificationManagerCompat.from(appContext)) {
            if(ContextCompat.checkSelfPermission(appContext, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                notify(id, notification)
                Log.d("DEBUGGING LOGS", "${notification.extras.getString(Notification.EXTRA_TITLE, "NA")}\n${notification.extras.getString(Notification.EXTRA_TEXT, "NA")}")
            } else {
                Log.d("DEBUGGING LOGS", "Unable to post notification. No permission to post")
            }
        }
    }
}