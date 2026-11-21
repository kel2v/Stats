package com.bytemanager.stats.data.repository

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import com.bytemanager.stats.data.types.TimestampedBatteryState
import com.bytemanager.stats.data.types.TimestampedRawBatteryState
import com.bytemanager.stats.utils.BatteryStateRepositoryUtils
import com.bytemanager.stats.utils.StatsTimeUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BatteryStateRepository @Inject constructor(
    @ApplicationContext private val appContext: Context,
) {
    val flowOfTimestampedRawBatteryState = callbackFlow {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if(intent == null) return

                val statsTimeUtils = StatsTimeUtils()

                val timestamp = statsTimeUtils.currentEpochSecond()
                val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, Int.MIN_VALUE)
                val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, Int.MIN_VALUE)
                val voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, Int.MIN_VALUE)
                val temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, Int.MIN_VALUE)
                val batteryHealth = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, Int.MIN_VALUE)
                val chargingStatus = intent.getIntExtra(BatteryManager.EXTRA_STATUS, Int.MIN_VALUE)

                val timestampedRawBatteryState = TimestampedRawBatteryState(
                    timestamp = timestamp,
                    level = if(level != Int.MIN_VALUE) level else null,
                    scale = if(scale != Int.MIN_VALUE) scale else null,
                    voltage = if(voltage != Int.MIN_VALUE) voltage else null,
                    temperature = if(temperature != Int.MIN_VALUE) temperature else null,
                    batteryHealth = if(batteryHealth != Int.MIN_VALUE) batteryHealth else null,
                    chargingStatus = if(chargingStatus != Int.MIN_VALUE) chargingStatus else null,
                )

                trySend(timestampedRawBatteryState)
            }
        }

        appContext.registerReceiver(
            receiver,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        )


        awaitClose {
            appContext.unregisterReceiver(receiver)
        }
    }

    val flowOfBatteryTechnology = callbackFlow {
        val receiver = object: BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if(context != null && intent != null) {
                    val batteryTechnology = BatteryStateRepositoryUtils.getTechnology(
                        intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY)
                    )

                    trySend(batteryTechnology)
                }
            }
        }

        appContext.registerReceiver(receiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))

        awaitClose {
            appContext.unregisterReceiver(receiver)
        }
    }

    val flowOfTimestampedBatteryState = combine(
        flowOfTimestampedRawBatteryState,
        flowOfBatteryTechnology
    ) { timestampedRawBatteryState, batteryTechnology ->
        BatteryStateRepositoryUtils.createTimestampedBatteryState(
            timestampedRawBatteryState,
            batteryTechnology
        )
    }

    fun getStateFlowOfTimestampedBatteryState(
        scope: CoroutineScope
    ): StateFlow<TimestampedBatteryState> {
        return combine(
            flowOfTimestampedRawBatteryState,
            flowOfBatteryTechnology,
        ) { timestampedRawBatteryState, batteryTechnology ->
            BatteryStateRepositoryUtils.createTimestampedBatteryState(
                timestampedRawBatteryState,
                batteryTechnology
            )
        }.stateIn(
            scope = scope,
            started = SharingStarted.Eagerly,
            initialValue = TimestampedBatteryState()
        )
    }
}