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
    val batteryStateStateFlow: StateFlow<BatteryState> = batteryStateRepository.batteryStateStateFlow

    fun togglePersistenceNotification() {
        val intent = Intent(appContext, StatsNotificationService::class.java)
        if(StatsNotificationService.isRunning) {
            appContext.stopService(intent)
        } else {
            ContextCompat.startForegroundService(appContext,intent)
        }
    }
}