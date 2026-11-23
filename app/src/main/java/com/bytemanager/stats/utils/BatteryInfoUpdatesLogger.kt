package com.bytemanager.stats.utils

import android.util.Log
import com.bytemanager.stats.data.repository.BatteryStateRepository
import com.bytemanager.stats.data.repository.DataStoreRepository
import com.bytemanager.stats.data.repository.Settings
import com.bytemanager.stats.data.repository.TimestampedBatteryStatesDbRepository
import com.bytemanager.stats.data.types.TimestampedRawBatteryState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class BatteryInfoUpdatesLogger @Inject constructor(
    private val batteryStateRepository: BatteryStateRepository,
    private val timestampedBatteryStatesDbRepository: TimestampedBatteryStatesDbRepository,
    private val dataStoreRepository: DataStoreRepository,
    private val errorHandler: ErrorHandler,
) {
    private data class CombinedFlow(
        val timestampedRawBatteryState: TimestampedRawBatteryState,
        val settings: Settings,
    )

    suspend fun startLogging() {
        Log.d("StatsLogger", "BatteryInfoLogging started")

        combine(
            batteryStateRepository.flowOfTimestampedRawBatteryState,
            dataStoreRepository.flowOfSettings
        ) { timestampedRawBatteryState, settings ->

            CombinedFlow(
                timestampedRawBatteryState = timestampedRawBatteryState,
                settings = settings,
            )

        }.collect { combinedData ->
            val timestampedRawBatteryState = combinedData.timestampedRawBatteryState
            val isDataLoggingEnabled = combinedData.settings.isDataLoggingEnabled

            Log.d("StatsLogger", "received new combinedData = $combinedData")

            if(isDataLoggingEnabled) {
                logTemperature(timestampedRawBatteryState)
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
}