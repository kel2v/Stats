package com.bytemanager.stats.ui.pages

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bytemanager.stats.data.repository.DataStoreRepository
import com.bytemanager.stats.services.notification.StatsLoggingNotificationService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val dataStoreRepository: DataStoreRepository
): ViewModel() {
    val stateFlowOfSettingsUiState: StateFlow<SettingsUiState> = dataStoreRepository.stateFlowOfSettings
        .map { settings ->
        SettingsUiState(
            isDataLoggingEnabled = settings.isDataLoggingEnabled,
            isNotificationEnabled = settings.isNotificationEnabled,
            isFahrenheit = settings.isFahrenheit,
            isMovingAverageForVoltageGraphEnabled = settings.isMovingAverageForVoltageGraphEnabled,
            isCutmullRomCurveEnabled = settings.isCutmullRomCurveEnabled
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState()
    )

    fun toggleDataLogging() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                dataStoreRepository.toggleDataLogging()

                if(dataStoreRepository.stateFlowOfSettings.value.isDataLoggingEnabled) {
                    StatsLoggingNotificationService.startForegroundService(appContext)
                } else {
                    StatsLoggingNotificationService.stopService(appContext)
                }
            }
        }
    }

    fun toggleNotification() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                dataStoreRepository.toggleNotificationEnabled()
            }
        }
    }

    fun toggleFahrenheitChoice() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                dataStoreRepository.toggleFahrenheitChoice()
            }
        }
    }

    fun toggleMovingAverageForVoltageGraphChoice() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                dataStoreRepository.toggleIsMovingAverageForVoltageGraphEnabledChoice()
            }
        }
    }

    fun toggleCutmullRomCurveChoice() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                dataStoreRepository.toggleIsCutmullRomCurveEnabled()
            }
        }
    }
}

data class SettingsUiState(
    val isDataLoggingEnabled: Boolean = false,
    val isNotificationEnabled: Boolean = false,
    val isFahrenheit: Boolean = false,
    val isMovingAverageForVoltageGraphEnabled: Boolean = true,
    val isCutmullRomCurveEnabled: Boolean = true
)