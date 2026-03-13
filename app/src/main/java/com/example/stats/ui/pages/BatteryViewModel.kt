package com.example.stats.ui.pages

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import androidx.lifecycle.ViewModel
import com.example.stats.notification.showLocalNotification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.stateIn

object BatteryStateRepository {
    private lateinit var appContext: Context

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    // This ia a Flow object that emits BatteryState whenever new Intent indicating Intent.ACTION_BATTERY_CHANGED is broadcasted.
    // It is defined lazy, means it is initialized on its first access.
    val batteryStateFlow by lazy {

        // callbackFlow creates a Flow object that reacts to new events it is concerned with by emitting a value.
        // Here, on receiving a broadcast for Intent.ACTION_BATTERY_CHANGED, it emits a BatteryState object with new values initialized as provided by the broadcast.
        callbackFlow {

            // creates an object of type BroadcastReceiver that also overrides `onReceive` method, which is called by Android when any Intent of Intent.ACTION_BATTERY_CHANGED type occurs
            val receiver = object : BroadcastReceiver() {

                // It is called by the Android whenever an Intent of Intent.ACTION_BATTERY_CHANGED type occurs
                override fun onReceive(context: Context?, intent: Intent?) {
                    if(intent == null) return

                    val state = BatteryState(
                        level = (intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) * 100) / intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1),
                        chargingStatus = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1),
                        temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1),
                        voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1),
                        technology = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY),
                        health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, BatteryManager.BATTERY_HEALTH_UNKNOWN)
                    )

                    trySend(state)

                    showLocalNotification(appContext, state)
                }
            }

            appContext.registerReceiver(receiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))

            awaitClose {
                appContext.unregisterReceiver(receiver)
            }

        }
    }

    val batteryStateStateFlow by lazy {
        batteryStateFlow.stateIn(
            scope = appScope,
            started = SharingStarted.WhileSubscribed(1000),
            initialValue = BatteryState(
                level = Int.MIN_VALUE,
                voltage = Int.MIN_VALUE,
                chargingStatus = Int.MIN_VALUE,
                temperature = Int.MIN_VALUE,
                technology = null,
                health = Int.MIN_VALUE
            )
        )
    }
}

class BatteryViewModel : ViewModel() {
    val batteryStateStateFlow: StateFlow<BatteryState> = BatteryStateRepository.batteryStateStateFlow
}

data class BatteryState(
    val level: Int,
    val voltage: Int,
    val chargingStatus: Int,
    val temperature: Int,
    val technology: String?,
    val health: Int
)