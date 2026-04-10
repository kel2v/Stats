package com.example.stats.models

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.example.stats.data.BatteryState
import com.example.stats.data.BatteryStateRepository
import com.example.stats.services.StatsNotificationService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class BatteryViewModel @Inject constructor(@ApplicationContext private val appContext: Context, batteryStateRepository: BatteryStateRepository) : ViewModel() {
    //unit testing NOT required
    val batteryStateStateFlow: StateFlow<BatteryState> = batteryStateRepository.batteryStateStateFlow

    //unit testing REQUIRED
    /*
    *  implementation something like this is required instead
    *
    *  fun togglePersistenceNotification() {
    *       val serviceController = StatsNotificationServiceController()  // StatsNotificationServiceController is a class that implements StatsNotificationServiceControllerInterface interface that has methods: startForegroundService and stopService
    *
    *       if(StatsNotificationService.isRunning.value) {
                serviceController.stopService()
            } else {
                serviceController.startForegroundService()
            }
       }
    * */
    fun togglePersistenceNotification() {
        val intent = Intent(appContext, StatsNotificationService::class.java)
        if(StatsNotificationService.isRunning.value) {
            appContext.stopService(intent)
        } else {
            ContextCompat.startForegroundService(appContext,intent)
        }
    }
}