package com.example.stats.models

import androidx.lifecycle.ViewModel
import com.example.stats.data.BatteryState
import com.example.stats.data.BatteryStateRepository
import com.example.stats.interfaces.BatteryViewModelInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class BatteryViewModel @Inject constructor(batteryStateRepository: BatteryStateRepository) : BatteryViewModelInterface, ViewModel() {
    //unit testing NOT required
    override val batteryStateStateFlow: StateFlow<BatteryState> = batteryStateRepository.batteryStateStateFlow
}