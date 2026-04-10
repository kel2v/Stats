package com.example.stats.models

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.example.stats.data.BatteryState
import com.example.stats.data.BatteryStateRepository
import com.example.stats.services.StatsNotificationService
import com.example.stats.utils.StatsNotificationServiceController
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class BatteryViewModel @Inject constructor(@ApplicationContext private val appContext: Context, batteryStateRepository: BatteryStateRepository, private val statsNotificationServiceController: StatsNotificationServiceController) : ViewModel() {
    //unit testing NOT required
    val batteryStateStateFlow: StateFlow<BatteryState> = batteryStateRepository.batteryStateStateFlow

    // testing is REQUIRED
    fun togglePersistenceNotification() {
        if(StatsNotificationService.isRunning.value) {
            statsNotificationServiceController.stopService()
        } else {
            statsNotificationServiceController.startForegroundService()
        }
    }
}