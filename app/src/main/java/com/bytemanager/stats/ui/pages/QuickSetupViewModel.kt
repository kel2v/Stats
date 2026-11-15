package com.bytemanager.stats.ui.pages

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bytemanager.stats.data.repository.DataStoreRepository
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
class QuickSetupViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val dataStoreRepository: DataStoreRepository,
): ViewModel() {
    val stateFlowOfQuickSetupUiState: StateFlow<QuickSetupUiState> = dataStoreRepository.flowOfSettings.map { settings ->
        QuickSetupUiState(
            isNotificationsEnabled = settings.isNotificationsEnabled,
            isDataLoggingEnabled = settings.isDataLoggingEnabled,
            isIgnoringBatteryOptimizations = settings.isIgnoringBatteryOptimizations,
            isAutostartLoggingServiceEnabled = settings.isAutostartLoggingServiceEnabled,
            showQuickSetup = settings.showQuickSetup
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = QuickSetupUiState()
    )

    fun openNotificationsSettings() {
        viewModelScope.launch {
            val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                .putExtra(Settings.EXTRA_APP_PACKAGE, appContext.packageName)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            appContext.startActivity(intent)
        }
    }

    fun openBatteryOptimizationsSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            .setData("package:${appContext.packageName}".toUri())
            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        appContext.startActivity(intent)
    }

    fun toggleDataLogging() {
        viewModelScope.launch {
            dataStoreRepository.toggleDataLogging()

            if(stateFlowOfQuickSetupUiState.value.isDataLoggingEnabled) {
                StatsLoggingService.startForegroundService(appContext)
            } else {
                StatsLoggingService.stopService(appContext)
            }
        }
    }

    fun clickGettingStartedCardSkipButton() {
        viewModelScope.launch {
            dataStoreRepository.setShowGettingStarted(false)
        }
    }

    fun clickQuickSetupDoneButton() {
        viewModelScope.launch {
            dataStoreRepository.setShowGettingStarted(false)
        }
    }

    fun toggleIsAutostartLoggingServiceChoice() {
        viewModelScope.launch {
            dataStoreRepository.toggleIsAutostartLoggingServiceChoice()
        }
    }
}

data class QuickSetupUiState(
    val isNotificationsEnabled: Boolean = false,
    val isDataLoggingEnabled: Boolean = false,
    val isIgnoringBatteryOptimizations: Boolean = false,
    val isAutostartLoggingServiceEnabled: Boolean = false,
    val showQuickSetup: Boolean = false,
)