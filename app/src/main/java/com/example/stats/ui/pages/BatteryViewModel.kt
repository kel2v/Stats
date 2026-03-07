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
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

object CurrentNowRepository {
    private lateinit var appContext: Context

    val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    fun init(context: Context) {
        appContext = context.applicationContext
    }



    val currentNowStateFlow by lazy {

        flow {
            val batteryManager =
                appContext.getSystemService(Context.BATTERY_SERVICE) as BatteryManager

            while (true) {
                emit(
                    batteryManager
                        .getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW) / 1000
                )
                delay(1_000)
            }
        }
        .flowOn(Dispatchers.IO)
        .distinctUntilChanged()
        .stateIn(
            scope = appScope,
            started = SharingStarted.WhileSubscribed(1_000),
            initialValue = 0
        )
    }
}

object BatteryStateRepository {
    private lateinit var appContext: Context

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    val batteryStateStateFlow by lazy {
        callbackFlow {
            val receiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    if(intent == null) return

                    appScope.launch {
                        val state = BatteryState(
                            level = (intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) * 100) / intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1),
                            chargingStatus = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1),
                            temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1),
                            voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1),
                            technology = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY),
                            health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, BatteryManager.BATTERY_HEALTH_UNKNOWN)
                        )

                        trySend(state)
                    }


                }
            }

            appContext.registerReceiver(receiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))

            awaitClose {
                appScope.cancel()
                appContext.unregisterReceiver(receiver)
            }

        }.stateIn(
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

class BatteryViewModel() : ViewModel() {

    val currentNowStateFlow: StateFlow<Int> = CurrentNowRepository.currentNowStateFlow
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