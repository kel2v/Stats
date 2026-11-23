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
import com.bytemanager.stats.data.repository.BatteryStateRepository
import com.bytemanager.stats.data.repository.DataStoreRepository
import com.bytemanager.stats.data.repository.NotificationIconInfoType
import com.bytemanager.stats.data.repository.ThermalStateRepository
import com.bytemanager.stats.data.types.TimestampedBatteryState
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class BatteryInfoUpdatesNotifier @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val batteryStateRepository: BatteryStateRepository,
    private val dataStoreRepository: DataStoreRepository,
    private val thermalStateRepository: ThermalStateRepository,
) {
    private val BATTERY_INFO_CHANNEL_ID = "battery_info"
    private data class CombinedData(
        val notification: Notification,
        val notificationSummary: Notification,
        val isNotificationEnabled: Boolean,
        val shouldNotifyBatteryInfoUpdates: Boolean,
    )

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

    private fun getNotificationIconType(notificationIconInfoOrdinal: Int): NotificationIconInfoType {
        return NotificationIconInfoType.entries[notificationIconInfoOrdinal]
    }

    private fun getDynamicSmallIcon(
        notificationIconInfoPreference: Int,
        timestampedBatteryState: TimestampedBatteryState,
        isFahrenheit: Boolean,
        throttlingStatus: Int?
    ): IconCompat {
        return when (getNotificationIconType(notificationIconInfoPreference)) {
            NotificationIconInfoType.APP_ICON -> IconCompat.createWithResource(
                appContext,
                R.drawable.status_bar_icon
            )

            NotificationIconInfoType.BATTERY_TEMPERATURE -> IconCompat.createWithBitmap(
                textBitmap(
                    BatteryStateRepositoryUtils.getTemperatureVerbose(
                        timestampedBatteryState,
                        isFahrenheit,
                        shorter = true
                    )
                )
            )

            NotificationIconInfoType.THROTTLING_STATUS -> IconCompat.createWithBitmap(
                textBitmap(
                    ThermalStateRepositoryUtils.getThermalThrottlingStatusShortString(
                        throttlingStatus
                    )
                )
            )
        }
    }

    fun createBatteryInfoNotificationChannel() {
        val channel = NotificationChannel(
            BATTERY_INFO_CHANNEL_ID,
            "Battery Info Updates",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Delivers latest battery state info"
        }


        val notificationManager = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

        Log.d("StatsNotificationManager", "'Battery Info Updates' notification channel created")
    }

    fun closeBatteryInfoNotificationChannel() {
        val notificationManager = appContext.getSystemService(NotificationManager::class.java)
        notificationManager.deleteNotificationChannel(BATTERY_INFO_CHANNEL_ID)
    }



    private fun batteryInfoUpdateNotificationBuilder(
        timestampedBatteryState: TimestampedBatteryState,
        throttlingStatus: Int?,
        dynamicSmallIcon: IconCompat,
        isFahrenheit: Boolean,
    ): NotificationCompat.Builder {
        val contentTitle = BatteryStateRepositoryUtils.getBatteryLevelPercentageVerbose(timestampedBatteryState) +
                " | ${BatteryStateRepositoryUtils.getTemperatureVerbose(timestampedBatteryState, isFahrenheit)}" +
                " | ${BatteryStateRepositoryUtils.getVoltageVerbose(timestampedBatteryState)}"

        val pendingIntent = PendingIntent.getActivity(
            appContext,
            0,
            Intent(appContext, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(appContext, BATTERY_INFO_CHANNEL_ID)
            .setSmallIcon(dynamicSmallIcon)
            .setContentTitle(contentTitle)
            .setContentText(ThermalStateRepositoryUtils.getThermalThrottlingStatusVerbose(throttlingStatus))
            .setOngoing(true)
            .setSilent(true)
            .setGroup("Stats")
            .setContentIntent(pendingIntent)

        return notificationBuilder
    }

    suspend fun startNotifying() {
        createBatteryInfoNotificationChannel()

        val notificationManager = appContext.getSystemService(NotificationManager::class.java)
        combine(
            batteryStateRepository.flowOfTimestampedBatteryState,
            thermalStateRepository.flowOfTimestampedThermalState,
            dataStoreRepository.flowOfSettings
        ) { batteryState, thermalState, settings ->
            val dynamicSmallIcon = getDynamicSmallIcon(
                notificationIconInfoPreference = settings.notificationIconInfoPreference,
                timestampedBatteryState = batteryState,
                isFahrenheit = settings.isFahrenheit,
                throttlingStatus = thermalState.throttlingStatus
            )

            CombinedData(
                notification = batteryInfoUpdateNotificationBuilder(
                    timestampedBatteryState = batteryState,
                    throttlingStatus = thermalState.throttlingStatus,
                    dynamicSmallIcon = dynamicSmallIcon,
                    isFahrenheit = settings.isFahrenheit
                )
                    .setSortKey("1")
                    .build(),

                notificationSummary = batteryInfoUpdateNotificationBuilder(
                    timestampedBatteryState = batteryState,
                    throttlingStatus = thermalState.throttlingStatus,
                    dynamicSmallIcon = dynamicSmallIcon,
                    isFahrenheit = settings.isFahrenheit
                )
                    .setGroupSummary(true)
                    .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_CHILDREN)
                    .build(),

                isNotificationEnabled = settings.isNotificationsEnabled,
                shouldNotifyBatteryInfoUpdates = settings.shouldNotifyBatteryInfoUpdates
            )

        }.collect { combinedData ->
            if(combinedData.isNotificationEnabled && combinedData.shouldNotifyBatteryInfoUpdates) {
                Log.d("StatsBatteryInfoUpdatesNotifier", "Posted new battery info update as notification")
                notificationManager.notify(2, combinedData.notification)
                notificationManager.notify(3, combinedData.notificationSummary)
            }
        }
    }
}