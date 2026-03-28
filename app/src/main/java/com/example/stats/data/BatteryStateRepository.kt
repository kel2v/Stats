package com.example.stats.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.util.Log
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

@Singleton
class BatteryStateRepository @Inject constructor(@ApplicationContext private val appContext: Context) {

    // This ia a Flow object that emits BatteryState whenever new Intent indicating Intent.ACTION_BATTERY_CHANGED is broadcasted.
    // It is defined lazy, means it is initialized on its first access.
    val batteryStateFlow by lazy {

        // callbackFlow creates a Flow object that reacts to new events it is concerned with by emitting a value.
        // Here, on receiving a broadcast for Intent.ACTION_BATTERY_CHANGED, it emits a BatteryState object with new values initialized as provided by the broadcast.
        callbackFlow {
            Log.d("PERMISSION DIALOG", "callbackFlow is started executing")

            // creates an object of type BroadcastReceiver that also overrides `onReceive` method, which is called by Android when any Intent of Intent.ACTION_BATTERY_CHANGED type occurs
            val receiver = object : BroadcastReceiver() {

                // It is called by the Android whenever an Intent of Intent.ACTION_BATTERY_CHANGED type occurs
                override fun onReceive(context: Context?, intent: Intent?) {
                    if(intent == null) return

                    val state = BatteryState(
                        level = (intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) * 100) / intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1),
                        temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1) / 10f,
                        voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1)/1000f,
                        technology = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY),

                        health = when (intent.getIntExtra(BatteryManager.EXTRA_HEALTH, BatteryManager.BATTERY_HEALTH_UNKNOWN)) {
                            BatteryManager.BATTERY_HEALTH_COLD -> "Cold"
                            BatteryManager.BATTERY_HEALTH_DEAD -> "Dead"
                            BatteryManager.BATTERY_HEALTH_GOOD -> "Good"
                            BatteryManager.BATTERY_HEALTH_OVERHEAT -> "Overheat"
                            BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "Overvoltage"
                            BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> "Unspecified failure"
                            BatteryManager.BATTERY_HEALTH_UNKNOWN -> "Unknown"
                            else -> "Unknown"
                        },
                        chargingStatus = when(intent.getIntExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_UNKNOWN)) {
                            BatteryManager.BATTERY_STATUS_FULL -> "Full"
                            BatteryManager.BATTERY_STATUS_CHARGING -> "Charging"
                            BatteryManager.BATTERY_STATUS_DISCHARGING -> "Discharging"
                            BatteryManager.BATTERY_STATUS_NOT_CHARGING -> "Not charging"
                            BatteryManager.BATTERY_STATUS_UNKNOWN -> "Unknown"
                            else -> "Unknown"
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
    }

    val batteryStateStateFlow = batteryStateFlow.stateIn(
        scope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
        started = SharingStarted.Eagerly,
        initialValue = BatteryState(
            level = Int.MIN_VALUE,
            voltage = Float.MIN_VALUE,
            chargingStatus = "NA",
            temperature = Float.MIN_VALUE,
            technology = null,
            health = "NA"
        )
    )
}