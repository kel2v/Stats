package com.example.stats.models

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.stats.data_structure.BatteryState
import com.example.stats.repository.BatteryStateRepository
import com.example.stats.utils.StatsNotificationServiceController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class BatteryViewModel @Inject constructor(batteryStateRepository: BatteryStateRepository) : ViewModel() {
    val batteryStateStateFlow: StateFlow<BatteryState> = batteryStateRepository.batteryStateStateFlow


    companion object {
        val isLoggingEnabled = mutableStateOf(true)

        fun toggleLogging(statsNotificationServiceController: StatsNotificationServiceController) {
            isLoggingEnabled.value = !isLoggingEnabled.value

            if(isLoggingEnabled()) {
                statsNotificationServiceController.startForegroundService()
            } else {
                statsNotificationServiceController.stopService()
            }
        }

        fun isLoggingEnabled(): Boolean {
            return isLoggingEnabled.value
        }
    }
}