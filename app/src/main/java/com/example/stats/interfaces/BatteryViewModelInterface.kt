package com.example.stats.interfaces

import com.example.stats.data_structure.BatteryState
import com.example.stats.services.StatsLoggingNotificationService
import kotlinx.coroutines.flow.StateFlow

interface BatteryViewModelInterface {
    val batteryStateStateFlow: StateFlow<BatteryState>
    fun togglePersistenceNotification(statsNotificationServiceController: StatsNotificationServiceControllerInterface) {
        if(StatsLoggingNotificationService.isRunning.value) {
            statsNotificationServiceController.stopService()
        } else {
            statsNotificationServiceController.startForegroundService()
        }
    }
}