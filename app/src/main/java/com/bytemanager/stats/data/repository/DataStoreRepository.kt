package com.bytemanager.stats.data.repository

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataStoreRepository @Inject constructor(
    @ApplicationContext private val appContext: Context,
) {
    private val dataStore = DeviceContextDataStoreProvider.get(appContext)

    object Keys {
        val IS_DATA_LOGGING_ENABLED = booleanPreferencesKey("is_data_logging_enabled")
        val IS_NOTIFICATIONS_ENABLED = booleanPreferencesKey("is_notifications_enabled")
        val IS_FAHRENHEIT = booleanPreferencesKey("is_fahrenheit")
        val IS_MOVING_AVERAGE_FOR_VOLTAGE_GRAPH_ENABLED = booleanPreferencesKey("is_moving_average_for_voltage_graph_enabled")
        val IS_CUTMULL_ROM_CURVE_ENABLED = booleanPreferencesKey("is_cutmull_rom_curve_enabled")
        val IS_IGNORING_BATTERY_OPTIMIZATIONS = booleanPreferencesKey("is_ignoring_battery_optimizations")
        val IS_AUTOSTART_LOGGING_SERVICE_ENABLED = booleanPreferencesKey("is_autostart_logging_service_enabled")
        val SHOW_GETTING_STARTED = booleanPreferencesKey("show_getting_started")
        val NOTIFICATION_ICON_INFO_PREFERENCE = intPreferencesKey("notification_icon_info_preference")
    }

    val flowOfSettings = dataStore.data.map { preferences ->
        val e = Settings(
            isDataLoggingEnabled = preferences[Keys.IS_DATA_LOGGING_ENABLED] ?: false,
            isNotificationsEnabled = preferences[Keys.IS_NOTIFICATIONS_ENABLED] ?: false,
            isFahrenheit = preferences[Keys.IS_FAHRENHEIT] ?: false,
            isMovingAverageForVoltageGraphEnabled = preferences[Keys.IS_MOVING_AVERAGE_FOR_VOLTAGE_GRAPH_ENABLED] ?: true,
            isCutmullRomCurveEnabled = preferences[Keys.IS_CUTMULL_ROM_CURVE_ENABLED] ?: true,
            isIgnoringBatteryOptimizations = preferences[Keys.IS_IGNORING_BATTERY_OPTIMIZATIONS] ?: false,
            isAutostartLoggingServiceEnabled = preferences[Keys.IS_AUTOSTART_LOGGING_SERVICE_ENABLED] ?: false,
            showQuickSetup = preferences[Keys.SHOW_GETTING_STARTED] ?: true,
            notificationIconInfoPreference = preferences[Keys.NOTIFICATION_ICON_INFO_PREFERENCE] ?: NotificationIconInfoType.BATTERY_TEMPERATURE.ordinal
        )

        Log.d("DataStoreRepository", "emitted new Settings value: $e")

        e
    }

    fun getStateFlowOfSettings(scope: CoroutineScope): StateFlow<Settings> {
        return flowOfSettings.stateIn(
            scope = scope,
            started = SharingStarted.Eagerly,
            initialValue = Settings()
        )
    }

    suspend fun isDataLoggingEnabled(): Boolean {
        return dataStore.data.map {
            it[Keys.IS_DATA_LOGGING_ENABLED] ?: false
        }.first()
    }

    suspend fun setDataLogging(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[Keys.IS_DATA_LOGGING_ENABLED] = value
        }
    }

    suspend fun toggleDataLogging() {
        val currentValue = dataStore.data.map { preferences ->
            preferences[Keys.IS_DATA_LOGGING_ENABLED] ?: false
        }.first()

        dataStore.edit { current ->
            current[Keys.IS_DATA_LOGGING_ENABLED] = !currentValue
        }
    }





    suspend fun isNotificationsEnabled(): Boolean {
        return dataStore.data.map { preferences ->
            preferences[Keys.IS_NOTIFICATIONS_ENABLED] ?: false
        }.first()
    }

    suspend fun setIsNotificationsEnabled(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[Keys.IS_NOTIFICATIONS_ENABLED] = value
        }
    }

    suspend fun toggleNotificationEnabled() {
        val currentValue = dataStore.data.map { preferences ->
            preferences[Keys.IS_NOTIFICATIONS_ENABLED] ?: false
        }.first()

        dataStore.edit { current ->
            current[Keys.IS_NOTIFICATIONS_ENABLED] = !currentValue
        }
    }





    suspend fun toggleFahrenheitChoice() {
        val currentValue = dataStore.data.map { preferences ->
            preferences[Keys.IS_FAHRENHEIT] ?: false
        }.first()

        dataStore.edit { current ->
            current[Keys.IS_FAHRENHEIT] = !currentValue
        }
    }





    suspend fun toggleIsMovingAverageForVoltageGraphEnabledChoice() {
        val currentValue = dataStore.data.map { preferences ->
            preferences[Keys.IS_MOVING_AVERAGE_FOR_VOLTAGE_GRAPH_ENABLED] ?: true
        }.first()

        dataStore.edit { preferences ->
            preferences[Keys.IS_MOVING_AVERAGE_FOR_VOLTAGE_GRAPH_ENABLED] = !currentValue
        }
    }

    suspend fun toggleIsCutmullRomCurveEnabled() {
        val currentValue = dataStore.data.map { preferences ->
            preferences[Keys.IS_CUTMULL_ROM_CURVE_ENABLED] ?: true
        }.first()

        dataStore.edit { preferences ->
            preferences[Keys.IS_CUTMULL_ROM_CURVE_ENABLED] = !currentValue
        }
    }

    suspend fun isIgnoringBatteryOptimizations(): Boolean {
        return dataStore.data.map { preferences ->
            preferences[Keys.IS_IGNORING_BATTERY_OPTIMIZATIONS] ?: false
        }.first()
    }

    suspend fun setIsIgnoringBatteryOptimizations(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[Keys.IS_IGNORING_BATTERY_OPTIMIZATIONS] = value
        }
    }

    suspend fun isAutostartLoggingServiceChoice(): Boolean {
        return dataStore.data.map { preferences ->
            preferences[Keys.IS_AUTOSTART_LOGGING_SERVICE_ENABLED] ?: false
        }.first()
    }

    suspend fun toggleIsAutostartLoggingServiceChoice() {
        val initialValue = isAutostartLoggingServiceChoice()

        dataStore.edit { preferences ->
            preferences[Keys.IS_AUTOSTART_LOGGING_SERVICE_ENABLED] = !initialValue
        }
    }

    suspend fun setShowGettingStarted(value: Boolean) {
        dataStore.edit { preferences ->
            preferences[Keys.SHOW_GETTING_STARTED] = value
        }
    }

    suspend fun showGettingStarted(): Boolean {
        return dataStore.data.map { preferences ->
            preferences[Keys.SHOW_GETTING_STARTED] ?: true
        }.first()
    }

    suspend fun setNotificationIconInfoPreference(value: NotificationIconInfoType) {
        dataStore.edit { preferences ->
            preferences[Keys.NOTIFICATION_ICON_INFO_PREFERENCE] = value.ordinal
        }
    }

    suspend fun getNotificationIconInfoPreference(): Int {
        return dataStore.data.map { preferences ->
            preferences[Keys.NOTIFICATION_ICON_INFO_PREFERENCE] ?: NotificationIconInfoType.BATTERY_TEMPERATURE.ordinal
        }.first()
    }
}

data class Settings(
    val isDataLoggingEnabled: Boolean = false,
    val isNotificationsEnabled: Boolean = false,
    val isFahrenheit: Boolean = false,
    val isMovingAverageForVoltageGraphEnabled: Boolean = true,
    val isCutmullRomCurveEnabled: Boolean = true,
    val isIgnoringBatteryOptimizations: Boolean = false,
    val isAutostartLoggingServiceEnabled: Boolean = false,
    val showQuickSetup: Boolean = false,
    val notificationIconInfoPreference: Int = NotificationIconInfoType.BATTERY_TEMPERATURE.ordinal,
)

enum class NotificationIconInfoType {
    // DO NOT CHANGE THIS ORDER !!!

    APP_ICON,
    BATTERY_TEMPERATURE,
    THROTTLING_STATUS,
}

object DeviceContextDataStoreProvider {
    @Volatile
    private var instance: DataStore<Preferences>? = null

    fun get(context: Context): DataStore<Preferences> {
        return instance ?: synchronized(this) {
            instance ?: buildDataStore(context.applicationContext).also { instance = it }
        }
    }

    private fun buildDataStore(context: Context): DataStore<Preferences> {
        Log.d("UnifiedDataStoreProvider", "building DataStore")
        return PreferenceDataStoreFactory.create(
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
            produceFile = {
                val deviceContext = context.applicationContext.createDeviceProtectedStorageContext()
                File(deviceContext.filesDir, "datastore/direct_boot_prefs.preferences_pb")
            }
        )
    }
}