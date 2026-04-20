package com.example.stats.models

import androidx.lifecycle.ViewModel
import com.example.stats.data_structure.BatteryState
import com.example.stats.interfaces.BatteryViewModelInterface
import com.example.stats.repository.BatteryStateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class BatteryViewModel @Inject constructor(batteryStateRepository: BatteryStateRepository) : BatteryViewModelInterface, ViewModel() {
    //unit testing NOT required
    override val batteryStateStateFlow: StateFlow<BatteryState> = batteryStateRepository.batteryStateStateFlow
}