package com.bytemanager.stats.ui.pages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bytemanager.stats.data.repository.BatteryStateRepository
import com.bytemanager.stats.data.repository.DataStoreRepository
import com.bytemanager.stats.data.types.TimestampedBatteryState
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
) : ViewModel() {
    val stateFlowOfBatteryUiState: StateFlow<BatteryUiState> = combine(
        batteryStateRepository.stateFlowOfTimestampedBatteryState,
        dataStoreRepository.stateFlowOfSettings,
        batteryStateRepository.stateFlowOfBatteryTechnology,
    ) { timestampedBatteryState, settings, batteryTechnology, ->
        BatteryUiState(
            timestampedBatteryState = timestampedBatteryState,
            batteryTechnology = batteryTechnology,
            isFahrenheit = settings.isFahrenheit,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = BatteryUiState()
    )
}


data class BatteryUiState(
    val timestampedBatteryState: TimestampedBatteryState = TimestampedBatteryState(),
    val batteryTechnology: String = "null",
    val isFahrenheit: Boolean = false,
)