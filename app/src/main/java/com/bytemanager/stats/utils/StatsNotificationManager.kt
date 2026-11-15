package com.bytemanager.stats.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.graphics.createBitmap
import androidx.core.graphics.drawable.IconCompat
import androidx.core.graphics.withScale
import com.bytemanager.stats.MainActivity
import com.bytemanager.stats.R
import com.bytemanager.stats.data.repository.NotificationIconInfoType
import com.bytemanager.stats.data.types.TimestampedBatteryState

object StatsNotificationManager {
    fun createStatsNotificationChannel(appContext: Context) {
        Log.d("StatsNotificationManager", "Creating notification channel")

        val channel = NotificationChannel(
            "Stats",
            "Battery info",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "battery temperature, battery level and battery voltage"
        }

        val notificationManager = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    fun closeStatsNotificationChannel(appContext: Context) {
        Log.d("StatsNotificationManager", "Closing notification channel")

        val notificationManager = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.deleteNotificationChannel("Stats")
    }

    fun buildStatsNotification(
        appContext: Context,
        timestampedBatteryState: TimestampedBatteryState,
        throttlingStatus: Int?,
        notificationIconInfoPreference: Int,
        isFahrenheit: Boolean,
    ): Notification {
        val channelId = "Stats"
        val intent = Intent(appContext, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            appContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val icon = when(notificationIconInfoPreference) {
            NotificationIconInfoType.APP_ICON.ordinal -> IconCompat.createWithResource(appContext, R.drawable.status_bar_icon)

            NotificationIconInfoType.BATTERY_TEMPERATURE.ordinal -> IconCompat.createWithBitmap(
                textBitmap(BatteryStateRepositoryUtils.getTemperatureVerbose(timestampedBatteryState, isFahrenheit, shorter = true))
            )

            NotificationIconInfoType.THROTTLING_STATUS.ordinal -> IconCompat.createWithBitmap(
                textBitmap(ThermalStateRepositoryUtils.getThermalThrottlingStatusShortString(throttlingStatus))
            )

            else -> IconCompat.createWithBitmap(
                textBitmap(BatteryStateRepositoryUtils.getTemperatureVerbose(timestampedBatteryState, isFahrenheit, shorter = true))
            )
        }

        val notification = NotificationCompat.Builder(appContext, channelId)
            .setSmallIcon(icon)
            .setContentTitle(
                    BatteryStateRepositoryUtils.getBatteryLevelPercentageVerbose(timestampedBatteryState) +
                    " | ${BatteryStateRepositoryUtils.getTemperatureVerbose(timestampedBatteryState, isFahrenheit)}" +
                    " | ${BatteryStateRepositoryUtils.getVoltageVerbose(timestampedBatteryState)}"
            )
            .setContentText(ThermalStateRepositoryUtils.getThermalThrottlingStatusVerbose(throttlingStatus))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setSilent(true)
            .build()

        return notification
    }

    private fun textBitmap(text: String): Bitmap {
        val width = 240
        val height = 240

        val bitmap = createBitmap(width, height)
        val canvas = Canvas(bitmap)

        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 120f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create("sans-serif-condensed", Typeface.BOLD)
        }

        val baseline = height / 2f - (paint.ascent() + paint.descent()) / 2f
        canvas.withScale(1f, 2f, width/2f, height/2f) {
            drawText(text, width / 2f, baseline, paint)
        }

        return bitmap
    }

}