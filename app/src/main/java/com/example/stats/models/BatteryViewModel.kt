package com.example.stats.models

import androidx.lifecycle.ViewModel
import com.example.stats.data.BatteryState
import com.example.stats.data.BatteryStateRepository
import kotlinx.coroutines.flow.StateFlow

class BatteryViewModel : ViewModel() {
    val batteryStateStateFlow: StateFlow<BatteryState> = BatteryStateRepository.batteryStateStateFlow
}