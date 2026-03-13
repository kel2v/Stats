package com.example.stats.notification

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
import com.example.stats.ui.pages.BatteryState


fun createNotificationChannel(context: Context) {
    val channelId = "Stats"
    val channelName = "Battery info"
    val channelDescription = "battery parameters updates"
    val importance = NotificationManager.IMPORTANCE_DEFAULT

    val channel = NotificationChannel(channelId, channelName, importance).apply {
        description = channelDescription
    }

    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.createNotificationChannel(channel)
}

fun showLocalNotification(context: Context, batteryState: BatteryState) {
    val channelId = "Stats"

    // Optional: tap notification to open MainActivity
    val intent = Intent(context, MainActivity::class.java)
    val pendingIntent = PendingIntent.getActivity(
        context,
        0,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val notification = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle("temp: ${batteryState.temperature/10f} Celsius | level = ${batteryState.level}%")
        .setContentText("voltage: ${batteryState.voltage/1000f} V")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setContentIntent(pendingIntent)
        .setOngoing(true)
        .build()

    with(NotificationManagerCompat.from(context)) {
        if(ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            notify(1001, notification)
            Log.d("NOTIFICATION", "temp: ${batteryState.temperature/10f} Celsius | level = ${batteryState.level}%\nvoltage: ${batteryState.voltage/1000f} V")
        }
    }
}