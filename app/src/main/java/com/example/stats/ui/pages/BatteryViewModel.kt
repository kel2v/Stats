package com.example.stats.ui.pages

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.stateIn
import java.util.Timer
import java.util.TimerTask

object CurrentNowRepository {
    private lateinit var appContext: Context

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    val currentNowStateFlow by lazy {
        callbackFlow {
            val timer = Timer()
            val task = object : TimerTask() {
                override fun run() {
                    val batteryManager =
                        appContext.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
                    val currentNow: Int =
                        batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW) / 1000
                    trySend(currentNow)
                }
            }

            timer.schedule(task, 0, 1000)

            awaitClose {
                timer.cancel()
            }
        }.stateIn(
            scope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
            started = SharingStarted.WhileSubscribed(1000),
            initialValue = 0
        )
    }
}

object BatteryStateRepository {
    private lateinit var appContext: Context

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    val batteryStateStateFlow by lazy {
        callbackFlow {
            val receiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    if(intent == null) return

                    val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                    val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                    val percent = (level * 100) / scale

                    val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
                    val charging = when(status) {
                        BatteryManager.BATTERY_STATUS_CHARGING -> "Charging"
                        BatteryManager.BATTERY_STATUS_NOT_CHARGING -> "Not charging"
                        BatteryManager.BATTERY_STATUS_FULL -> "Fully charged"
                        BatteryManager.BATTERY_STATUS_DISCHARGING -> "Discharging"
                        else -> "Unknown"
                    }

                    val temp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1) / 10f

                    val voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1) / 1000f

                    val technology = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY)

                    val health = when(intent.getIntExtra(BatteryManager.EXTRA_HEALTH, BatteryManager.BATTERY_HEALTH_UNKNOWN)) {
                        BatteryManager.BATTERY_HEALTH_GOOD -> "Good"
                        BatteryManager.BATTERY_HEALTH_OVERHEAT -> "Overheating"
                        BatteryManager.BATTERY_HEALTH_DEAD -> "Dead"
                        BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "Over voltage"
                        BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> "Unspecified failure"
                        BatteryManager.BATTERY_HEALTH_COLD -> "Cold"
                        else -> "Unknown"
                    }

                    trySend(
                        BatteryState(
                            level = percent,
                            voltage = voltage,
                            chargingStatus = charging,
                            temperature = temp,
                            technology = technology,
                            health = health
                        )
                    )
                }
            }

            appContext.registerReceiver(receiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))

            awaitClose {
                appContext.unregisterReceiver(receiver)
            }

        }.stateIn(
            scope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
            started = SharingStarted.WhileSubscribed(1000),
            initialValue = BatteryState(
                level = null,
                voltage = null,
                chargingStatus = null,
                temperature = null,
                technology = null,
                health = null
            )
        )
    }
}

class BatteryViewModel() : ViewModel() {

    val currentNowStateFlow: StateFlow<Int> = CurrentNowRepository.currentNowStateFlow
    val batteryStateStateFlow: StateFlow<BatteryState> = BatteryStateRepository.batteryStateStateFlow
}

data class BatteryState(
    val level: Int? = null,
    val voltage: Float? = null,
    val chargingStatus: String? = null,
    val temperature: Float? = null,
    val technology: String? = null,
    val health: String? = null
)