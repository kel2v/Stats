package com.bytemanager.stats.utils

import android.app.NotificationManager
import android.content.Context
import android.util.Log
import com.bytemanager.stats.data.repository.BatteryStateRepository
import com.bytemanager.stats.data.repository.DataStoreRepository
import com.bytemanager.stats.data.repository.TimestampedBatteryStatesDbRepository
import com.bytemanager.stats.data.types.TimestampedRawBatteryState
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class StatsLogger @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val batteryStateRepository: BatteryStateRepository,
    private val timestampedBatteryStatesDbRepository: TimestampedBatteryStatesDbRepository,
    private val dataStoreRepository: DataStoreRepository,
    private val errorHandler: ErrorHandler
) {
    suspend fun startStatsLogger() {
        Log.d("StatsLogger", "StatsLogger started")
        logger()
    }

    private suspend fun logger() {
        val notificationManager = appContext.getSystemService(NotificationManager::class.java)

        batteryStateRepository.flowOfTimestampedRawBatteryState.collect { timestampedRawBatteryState ->
            if(dataStoreRepository.isDataLoggingEnabled()) {
                logTemperature(timestampedRawBatteryState)
            }

            if(
                dataStoreRepository.isNotificationPermissionObtained() &&
                dataStoreRepository.isNotificationEnabled()
            ) {
                notifyLogging(timestampedRawBatteryState, notificationManager)
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

    private fun notifyLogging(timestampedRawBatteryState: TimestampedRawBatteryState, notificationManager: NotificationManager) {
        val batteryState = BatteryStateRepositoryUtils.convertTimestampedBatteryStateToBatteryState(
            timestampedRawBatteryState
        )

        val notification = StatsNotificationManager.buildStatsNotification(appContext, batteryState)
        notificationManager.notify(1, notification)

        Log.d("StatsLogger", "Posted new notification")
    }
}