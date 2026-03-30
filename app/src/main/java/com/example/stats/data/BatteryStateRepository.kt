package com.example.stats.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.util.Log
import com.example.stats.R
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Singleton
import com.example.stats.utils.BatteryStateRepositoryUtils

@Singleton
class BatteryStateRepository @Inject constructor(@ApplicationContext private val appContext: Context) {

    // This ia a Flow object that emits BatteryState whenever new Intent indicating Intent.ACTION_BATTERY_CHANGED is broadcasted.
    // It is defined lazy, means it is initialized on its first access.
    val batteryStateFlow = callbackFlow {
        Log.d("PERMISSION DIALOG", "callbackFlow is started executing")

        // creates an object of type BroadcastReceiver that also overrides `onReceive` method, which is called by Android when any Intent of Intent.ACTION_BATTERY_CHANGED type occurs
        val receiver = object : BroadcastReceiver() {

            // It is called by the Android whenever an Intent of Intent.ACTION_BATTERY_CHANGED type occurs
            override fun onReceive(context: Context?, intent: Intent?) {
                if(intent == null) return

                val batteryStateRepositoryUtils = BatteryStateRepositoryUtils()
                val state = BatteryState(
                    level = batteryStateRepositoryUtils.getBatteryLevelPercentage(
                        level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1),
                        scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                    ),
                    temperature = batteryStateRepositoryUtils.getTemperatureInCelsius(
                        intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, Int.MIN_VALUE)
                    ),
                    voltage = batteryStateRepositoryUtils.getVoltage(
                        intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1)
                    ),
                    technology = batteryStateRepositoryUtils.getTechnology(intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY)),

                    health = when (intent.getIntExtra(BatteryManager.EXTRA_HEALTH, BatteryManager.BATTERY_HEALTH_UNKNOWN)) {
                        BatteryManager.BATTERY_HEALTH_COLD -> appContext.getString(R.string.battery_health_cold)
                        BatteryManager.BATTERY_HEALTH_DEAD -> appContext.getString(R.string.battery_health_dead)
                        BatteryManager.BATTERY_HEALTH_GOOD -> appContext.getString(R.string.battery_health_good)
                        BatteryManager.BATTERY_HEALTH_OVERHEAT -> appContext.getString(R.string.battery_health_overheat)
                        BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> appContext.getString(R.string.battery_health_over_voltage)
                        BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> appContext.getString(R.string.battery_health_unspecified_failure)
                        BatteryManager.BATTERY_HEALTH_UNKNOWN -> appContext.getString(R.string.battery_health_unknown)
                        else -> appContext.getString(R.string.battery_health_unknown)
                    },
                    chargingStatus = when(intent.getIntExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_UNKNOWN)) {
                        BatteryManager.BATTERY_STATUS_FULL -> appContext.getString(R.string.battery_status_full)
                        BatteryManager.BATTERY_STATUS_CHARGING -> appContext.getString(R.string.battery_status_charging)
                        BatteryManager.BATTERY_STATUS_DISCHARGING -> appContext.getString(R.string.battery_status_discharging)
                        BatteryManager.BATTERY_STATUS_NOT_CHARGING -> appContext.getString(R.string.battery_status_not_charging)
                        BatteryManager.BATTERY_STATUS_UNKNOWN -> appContext.getString(R.string.battery_status_unknown)
                        else -> appContext.getString(R.string.battery_status_unknown)
                    }
                )

                trySend(state)
            }
        }

        appContext.registerReceiver(receiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))

        awaitClose {
            appContext.unregisterReceiver(receiver)
        }
    }

    val batteryStateStateFlow = batteryStateFlow.stateIn(
        scope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
        started = SharingStarted.Eagerly,
        initialValue = BatteryState(
            level = Int.MIN_VALUE,
            voltage = Float.MIN_VALUE,
            chargingStatus = appContext.getString(R.string.na),
            temperature = Float.MIN_VALUE,
            technology = "Not available",
            health = appContext.getString(R.string.na)
        )
    )
}