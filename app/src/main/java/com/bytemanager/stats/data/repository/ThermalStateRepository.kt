package com.bytemanager.stats.data.repository

import android.content.Context
import android.os.PowerManager
import android.util.Log
import com.bytemanager.stats.data.types.TimestampedThermalState
import com.bytemanager.stats.utils.StatsTimeUtils
import com.bytemanager.stats.utils.ThermalStateRepositoryUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThermalStateRepository @Inject constructor(
    @ApplicationContext private val appContext: Context,
) {
    val flowOfTimestampedThermalState = callbackFlow {
        val powerManager = appContext.getSystemService(PowerManager::class.java)
        trySend(
            TimestampedThermalState(
                timestamp = StatsTimeUtils().nowInTimestamp(),
                throttlingStatus = powerManager.currentThermalStatus
            )
        )

        val listener = PowerManager.OnThermalStatusChangedListener { status ->
            Log.d("PowerStateRepository", "thermalStatus changed to ${ThermalStateRepositoryUtils.getThermalThrottlingStatusVerbose(status)}")
            trySend(
                TimestampedThermalState(
                    timestamp = StatsTimeUtils().nowInTimestamp(),
                    throttlingStatus = status
                )
            )
        }

        powerManager.addThermalStatusListener(listener)

        awaitClose {
            powerManager.removeThermalStatusListener(listener)
        }
    }

    fun getStateFlowOfTimestampedThermalState(scope: CoroutineScope): StateFlow<TimestampedThermalState> {
        return flowOfTimestampedThermalState.stateIn(
            scope = scope,
            started = SharingStarted.Eagerly,
            initialValue = TimestampedThermalState(0)
        )
    }
}