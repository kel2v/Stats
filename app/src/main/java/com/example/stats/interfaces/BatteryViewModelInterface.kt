package com.example.stats.interfaces

import com.example.stats.data.BatteryState
import com.example.stats.services.StatsNotificationService
import kotlinx.coroutines.flow.StateFlow

interface BatteryViewModelInterface {
    val batteryStateStateFlow: StateFlow<BatteryState>
    fun togglePersistenceNotification(statsNotificationServiceController: StatsNotificationServiceControllerInterface) {
        if(StatsNotificationService.isRunning.value) {
            statsNotificationServiceController.stopService()
        } else {
            statsNotificationServiceController.startForegroundService()
        }
    }
}