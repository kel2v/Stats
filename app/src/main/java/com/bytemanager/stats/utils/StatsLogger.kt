package com.bytemanager.stats.utils

import android.app.NotificationManager
import android.content.Context
import android.util.Log
import com.bytemanager.stats.data.repository.BatteryStateRepository
import com.bytemanager.stats.data.repository.DataStoreRepository
import com.bytemanager.stats.data.repository.Settings
import com.bytemanager.stats.data.repository.ThermalStateRepository
import com.bytemanager.stats.data.repository.TimestampedBatteryStatesDbRepository
import com.bytemanager.stats.data.types.TimestampedRawBatteryState
import com.bytemanager.stats.data.types.TimestampedThermalState
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class StatsLogger @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val batteryStateRepository: BatteryStateRepository,
    private val timestampedBatteryStatesDbRepository: TimestampedBatteryStatesDbRepository,
    private val dataStoreRepository: DataStoreRepository,
    private val errorHandler: ErrorHandler,
    private val thermalStateRepository: ThermalStateRepository,
) {
    private data class CombinedFlow(
        val timestampedRawBatteryState: TimestampedRawBatteryState,
        val timestampedThermalState: TimestampedThermalState,
        val settings: Settings,
    )

    suspend fun startStatsLogger() {
        Log.d("StatsLogger", "StatsLogger started")
        logger()
    }

    private suspend fun logger() {
        val notificationManager = appContext.getSystemService(NotificationManager::class.java)
        combine(
            batteryStateRepository.flowOfTimestampedRawBatteryState,
            thermalStateRepository.flowOfTimestampedThermalState,
            dataStoreRepository.flowOfSettings
        ) { timestampedRawBatteryState, timestampedThermalState, settings ->

            CombinedFlow(
                timestampedRawBatteryState = timestampedRawBatteryState,
                timestampedThermalState = timestampedThermalState,
                settings = settings,
            )
        }.collect { combinedData ->
            val timestampedRawBatteryState = combinedData.timestampedRawBatteryState
            val throttlingStatus = combinedData.timestampedThermalState.throttlingStatus
            val notificationIconInfoPreference = combinedData.settings.notificationIconInfoPreference
            val isNotificationsEnabled = combinedData.settings.isNotificationsEnabled
            val isDataLoggingEnabled = combinedData.settings.isDataLoggingEnabled
            val isFahrenheit = combinedData.settings.isFahrenheit

            Log.d("StatsLogger", "received new combinedData = $combinedData")

            if(isDataLoggingEnabled) {
                logTemperature(timestampedRawBatteryState)
            }

            if(isNotificationsEnabled) {
                notifyLogging(
                    timestampedRawBatteryState = timestampedRawBatteryState,
                    notificationManager = notificationManager,
                    throttlingStatus = throttlingStatus,
                    notificationIconInfoType = notificationIconInfoPreference,
                    isFahrenheit = isFahrenheit
                )
            }
        }
    }

    private suspend fun logTemperature(timestampedRawBatteryState: TimestampedRawBatteryState) {
        try {
            timestampedBatteryStatesDbRepository.dbDao.insertItemWithReplace(timestampedRawBatteryState)

        } catch(e: CancellationException) {
            throw e

        } catch(e: Throwable) {
            Log.d("StatsLogger", "Error occurred: $e")

            val insertingItem = timestampedRawBatteryState
            val existingItem = timestampedBatteryStatesDbRepository.dbDao.getItem(insertingItem.timestamp).first()

            val errorMessage = errorHandler.createErrorMessage(
                function = "StatsLogger::logTemperature",
                error = e,
                message = "insertingItem = $insertingItem, existingItem = $existingItem"
            )

            errorHandler.logError(errorMessage)

            throw CancellationException("Caught error: $e")
        }
    }

    private fun notifyLogging(
        timestampedRawBatteryState: TimestampedRawBatteryState,
        notificationManager: NotificationManager,
        throttlingStatus: Int?,
        notificationIconInfoType: Int,
        isFahrenheit: Boolean,
    ) {
        val batteryState = BatteryStateRepositoryUtils.createTimestampedBatteryState(
            timestampedRawBatteryState,
            "-"
        )

        val notification = StatsNotificationManager.buildStatsNotification(
            appContext = appContext,
            timestampedBatteryState = batteryState,
            throttlingStatus = throttlingStatus,
            notificationIconInfoPreference = notificationIconInfoType,
            isFahrenheit = isFahrenheit
        )
        notificationManager.notify(1, notification)

        Log.d("StatsLogger", "Posted new notification")
    }
}