package com.bytemanager.stats.ui.pages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bytemanager.stats.data.repository.BatteryStateRepository
import com.bytemanager.stats.data.repository.DataStoreRepository
import com.bytemanager.stats.data.repository.ThermalStateRepository
import com.bytemanager.stats.data.types.TimestampedBatteryState
import com.bytemanager.stats.data.types.TimestampedThermalState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class BatteryViewModel @Inject constructor(
    batteryStateRepository: BatteryStateRepository,
    dataStoreRepository: DataStoreRepository,
    thermalStateRepository: ThermalStateRepository,
) : ViewModel() {
    val stateFlowOfBatteryUiState: StateFlow<BatteryUiState> = combine(
        batteryStateRepository.flowOfTimestampedBatteryState,
        dataStoreRepository.flowOfSettings,
        thermalStateRepository.flowOfTimestampedThermalState,
    ) { timestampedBatteryState, settings, timestampedThermalState ->

        BatteryUiState(
            timestampedBatteryState = timestampedBatteryState,
            timestampedThermalState = timestampedThermalState,
            isFahrenheit = settings.isFahrenheit
        )

    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = BatteryUiState()
    )
}


data class BatteryUiState(
    val timestampedBatteryState: TimestampedBatteryState = TimestampedBatteryState(),
    val timestampedThermalState: TimestampedThermalState = TimestampedThermalState(0),
    val isFahrenheit: Boolean = false,
)