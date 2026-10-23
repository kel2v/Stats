package com.bytemanager.stats.data.repository

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.bytemanager.stats.ApplicationCoroutineScope
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataStoreRepository @Inject constructor(
    @ApplicationContext private val appContext: Context,
    @ApplicationCoroutineScope private val appCoroutineScope: CoroutineScope
) {
    private val Context.dataStore by preferencesDataStore(name = "app_settings")

    object Keys {
        val IS_NOTIFICATION_PERMISSION_OBTAINED = booleanPreferencesKey("is_notification_permission_obtained")
        val IS_DATA_LOGGING_ENABLED = booleanPreferencesKey("is_data_logging_enabled")
        val IS_DATA_LOGGING_TASK_RUNNING = booleanPreferencesKey("is_data_logging_task_running")
        val IS_NOTIFICATION_ENABLED = booleanPreferencesKey("is_notification_enabled")
        val ERROR_LOG_FILE_URI = stringPreferencesKey("error_log_file_uri")
        val IS_FAHRENHEIT = booleanPreferencesKey("is_fahrenheit")
        val IS_MOVING_AVERAGE_FOR_VOLTAGE_GRAPH_ENABLED = booleanPreferencesKey("is_moving_average_for_voltage_graph_enabled")
        val IS_CUTMULL_ROM_CURVE_ENABLED = booleanPreferencesKey(("is_cutmull_rom_curve_enabled"))
    }

    val stateFlowOfSettings: StateFlow<Settings> = appContext.dataStore.data.map { preferences ->
        Log.d("DataStoreRepository", "emitted new Settings value")
        Settings(
            isDataLoggingEnabled = preferences[Keys.IS_DATA_LOGGING_ENABLED] ?: false,
            isNotificationPermissionObtained = preferences[Keys.IS_NOTIFICATION_PERMISSION_OBTAINED] ?: false,
            isNotificationEnabled = preferences[Keys.IS_NOTIFICATION_ENABLED] ?: false,
            isFahrenheit = preferences[Keys.IS_FAHRENHEIT] ?: false,
            isMovingAverageForVoltageGraphEnabled = preferences[Keys.IS_MOVING_AVERAGE_FOR_VOLTAGE_GRAPH_ENABLED] ?: true,
            isCutmullRomCurveEnabled = preferences[Keys.IS_CUTMULL_ROM_CURVE_ENABLED] ?: true,
        )
    }.stateIn(
            scope = appCoroutineScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Settings()
    )

    suspend fun isDataLoggingEnabled(): Boolean {
        return appContext.dataStore.data.map {
            it[Keys.IS_DATA_LOGGING_ENABLED] ?: false
        }.first()
    }

    suspend fun toggleDataLogging() {
        val currentValue = appContext.dataStore.data.map { preferences ->
            preferences[Keys.IS_DATA_LOGGING_ENABLED] ?: false
        }.first()

        appContext.dataStore.edit { current ->
            current[Keys.IS_DATA_LOGGING_ENABLED] = !currentValue
        }
    }





    suspend fun isNotificationPermissionObtained(): Boolean {
        return appContext.dataStore.data.map { preferences ->
            preferences[Keys.IS_NOTIFICATION_PERMISSION_OBTAINED] ?: false
        }.first()
    }

    suspend fun setNotificationPermissionObtained() {
        appContext.dataStore.edit { current ->
            current[Keys.IS_NOTIFICATION_PERMISSION_OBTAINED] = true
        }
    }

    suspend fun setNotificationPermissionNotObtained() {
        appContext.dataStore.edit { current ->
            current[Keys.IS_NOTIFICATION_PERMISSION_OBTAINED] = false
        }
    }




    suspend fun isNotificationEnabled(): Boolean {
        return appContext.dataStore.data.map { preferences ->
            preferences[Keys.IS_NOTIFICATION_ENABLED] ?: false
        }.first()
    }

    suspend fun toggleNotificationEnabled() {
        val currentValue = appContext.dataStore.data.map { preferences ->
            preferences[Keys.IS_NOTIFICATION_ENABLED] ?: false
        }.first()

        appContext.dataStore.edit { current ->
            current[Keys.IS_NOTIFICATION_ENABLED] = !currentValue
        }
    }





    suspend fun setErrorLogFileUri(uri: String) {
        appContext.dataStore.edit { preferences ->
            preferences[Keys.ERROR_LOG_FILE_URI] = uri
        }
    }

    fun getErrorLogFileUri(): Flow<String?> {
        val result = appContext.dataStore.data.map { preferences ->
            preferences[Keys.ERROR_LOG_FILE_URI]
        }

        return result
    }





    suspend fun toggleFahrenheitChoice() {
        val currentValue = appContext.dataStore.data.map { preferences ->
            preferences[Keys.IS_FAHRENHEIT] ?: false
        }.first()

        appContext.dataStore.edit { current ->
            current[Keys.IS_FAHRENHEIT] = !currentValue
        }
    }




    suspend fun setIsDataLoggingServiceRunning(value: Boolean) {
        appContext.dataStore.edit { preferences ->
            preferences[Keys.IS_DATA_LOGGING_TASK_RUNNING] = value
        }
    }

    suspend fun getIsDataLoggingServiceRunning(): Boolean? {
        return appContext.dataStore.data.map { preferences ->
            preferences[Keys.IS_DATA_LOGGING_TASK_RUNNING]
        }.first()
    }





    suspend fun toggleIsMovingAverageForVoltageGraphEnabledChoice() {
        val currentValue = appContext.dataStore.data.map { preferences ->
            preferences[Keys.IS_MOVING_AVERAGE_FOR_VOLTAGE_GRAPH_ENABLED] ?: true
        }.first()

        appContext.dataStore.edit { preferences ->
            preferences[Keys.IS_MOVING_AVERAGE_FOR_VOLTAGE_GRAPH_ENABLED] = !currentValue
        }
    }

    suspend fun toggleIsCutmullRomCurveEnabled() {
        val currentValue = appContext.dataStore.data.map { preferences ->
            preferences[Keys.IS_CUTMULL_ROM_CURVE_ENABLED] ?: true
        }.first()

        appContext.dataStore.edit { preferences ->
            preferences[Keys.IS_CUTMULL_ROM_CURVE_ENABLED] = !currentValue
        }
    }
}

data class Settings(
    val isDataLoggingEnabled: Boolean = false,
    val isNotificationPermissionObtained: Boolean = false,
    val isNotificationEnabled: Boolean = false,
    val isFahrenheit: Boolean = false,
    val isDataLoggingTaskRunning: Boolean = false,
    val isMovingAverageForVoltageGraphEnabled: Boolean = true,
    val isCutmullRomCurveEnabled: Boolean = true,
)