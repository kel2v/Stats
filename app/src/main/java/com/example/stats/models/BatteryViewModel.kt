package com.example.stats.models

import androidx.lifecycle.ViewModel
import com.example.stats.data.BatteryState
import com.example.stats.data.BatteryStateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class BatteryViewModel @Inject constructor(batteryStateRepository: BatteryStateRepository) : ViewModel() {
    val batteryStateStateFlow: StateFlow<BatteryState> = batteryStateRepository.batteryStateStateFlow
}