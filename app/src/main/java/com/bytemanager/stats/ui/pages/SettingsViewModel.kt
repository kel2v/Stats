package com.bytemanager.stats.ui.pages

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bytemanager.stats.data.repository.DataStoreRepository
import com.bytemanager.stats.data.repository.NotificationIconInfoType
import com.bytemanager.stats.services.notification.StatsLoggingService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val dataStoreRepository: DataStoreRepository
): ViewModel() {
    val stateFlowOfSettingsUiState: StateFlow<SettingsUiState> = dataStoreRepository.flowOfSettings
        .map { settings ->
        SettingsUiState(
            isDataLoggingEnabled = settings.isDataLoggingEnabled,
            isNotificationsEnabled = settings.isNotificationsEnabled,
            isFahrenheit = settings.isFahrenheit,
            isMovingAverageForVoltageGraphEnabled = settings.isMovingAverageForVoltageGraphEnabled,
            isCutmullRomCurveEnabled = settings.isCutmullRomCurveEnabled,
            isIgnoringBatteryOptimizations = settings.isIgnoringBatteryOptimizations,
            isAutostartLoggingServiceEnabled = settings.isAutostartLoggingServiceEnabled,
            selectedNotificationIconInfo = settings.notificationIconInfoPreference
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState()
    )

    fun toggleDataLogging() {
        viewModelScope.launch {
            dataStoreRepository.toggleDataLogging()

            if(stateFlowOfSettingsUiState.value.isDataLoggingEnabled) {
                StatsLoggingService.startForegroundService(appContext)
            } else {
                StatsLoggingService.stopService(appContext)
            }
        }
    }

    fun toggleNotifications() {
        viewModelScope.launch {
            val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                .putExtra(Settings.EXTRA_APP_PACKAGE, appContext.packageName)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            appContext.startActivity(intent)
        }
    }

    fun toggleFahrenheitChoice() {
        viewModelScope.launch {
            dataStoreRepository.toggleFahrenheitChoice()
        }
    }

    fun toggleMovingAverageForVoltageGraphChoice() {
        viewModelScope.launch {
            dataStoreRepository.toggleIsMovingAverageForVoltageGraphEnabledChoice()
        }
    }

    fun toggleCutmullRomCurveChoice() {
        viewModelScope.launch {
            dataStoreRepository.toggleIsCutmullRomCurveEnabled()
        }
    }

    fun openBatteryOptimizationsSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            .setData("package:${appContext.packageName}".toUri())
            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        appContext.startActivity(intent)
    }

    fun toggleIsAutostartLoggingServiceChoice() {
        viewModelScope.launch {
            dataStoreRepository.toggleIsAutostartLoggingServiceChoice()
        }
    }

    fun setNotificationIconInfoPreference(choice: NotificationIconInfoType) {
        viewModelScope.launch {
            dataStoreRepository.setNotificationIconInfoPreference(choice)
        }
    }
}

data class SettingsUiState(
    val isDataLoggingEnabled: Boolean = false,
    val isNotificationsEnabled: Boolean = false,
    val isFahrenheit: Boolean = false,
    val isMovingAverageForVoltageGraphEnabled: Boolean = true,
    val isCutmullRomCurveEnabled: Boolean = true,
    val isIgnoringBatteryOptimizations: Boolean = false,
    val isAutostartLoggingServiceEnabled: Boolean = false,
    val selectedNotificationIconInfo: Int = NotificationIconInfoType.BATTERY_TEMPERATURE.ordinal
)