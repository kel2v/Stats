package com.bytemanager.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bytemanager.stats.data.repository.DataStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class StatsAppViewModel @Inject constructor(
    dataStoreRepository: DataStoreRepository,
): ViewModel() {
    val stateFlowOfStatsAppUiState: StateFlow<StatsAppUiState> = dataStoreRepository.flowOfSettings.map { settings ->
        StatsAppUiState(
            showQuickSetup = settings.showQuickSetup
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = StatsAppUiState()
    )
}

data class StatsAppUiState(
    val showQuickSetup: Boolean = false
)