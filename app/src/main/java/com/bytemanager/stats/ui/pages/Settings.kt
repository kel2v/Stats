package com.bytemanager.stats.ui.pages

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.bytemanager.stats.R

@Composable
fun Settings(settingsViewModel: SettingsViewModel = hiltViewModel()) {
    val spacingBetweenPreferences = 25.dp

    val settingsUiState by settingsViewModel.stateFlowOfSettingsUiState.collectAsState()

    val isDataLoggingEnabled= settingsUiState.isDataLoggingEnabled
    val areNotificationsEnabled = settingsUiState.isNotificationsEnabled
    val isFahrenheit = settingsUiState.isFahrenheit
    val isMovingAverageForVoltageGraphEnabled = settingsUiState.isMovingAverageForVoltageGraphEnabled
    val isCutmullRomCurveEnabled = settingsUiState.isCutmullRomCurveEnabled
    val isIgnoringBatteryOptimizations = settingsUiState.isIgnoringBatteryOptimizations
    val isAutostartLoggingServiceEnabled = settingsUiState.isAutostartLoggingServiceEnabled

    Column (
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        PreferenceGroup(
            name = stringResource(R.string.units_preference_group),
        ) {
            TogglingPreference(
                label = stringResource(R.string.show_temperature_in_fahrenheits_preference),
                isChecked = isFahrenheit,
                enabled = true,
                onCheckedChange = {
                    settingsViewModel.toggleFahrenheitChoice()
                }
            )
        }

        PreferenceGroup(
            name = stringResource(R.string.graphs_preference_group)
        ) {
            TogglingPreference(
                label = stringResource(R.string.moving_average_preference),
                description = stringResource(R.string.moving_average_preference_description),
                isChecked = isMovingAverageForVoltageGraphEnabled,
                onCheckedChange = {
                    settingsViewModel.toggleMovingAverageForVoltageGraphChoice()
                }
            )

            Spacer(Modifier.height(spacingBetweenPreferences))

            TogglingPreference(
                label = stringResource(R.string.catmull_rom_curve_preference),
                description = stringResource(R.string.catmull_rom_curve_preference_description),
                isChecked = isCutmullRomCurveEnabled,
                onCheckedChange = {
                    settingsViewModel.toggleCutmullRomCurveChoice()
                }
            )
        }

        PreferenceGroup(
            name = stringResource(R.string.permissions_and_exemptions_preference_group)
        ) {
            TogglingPreference(
                label = stringResource(R.string.notification_preference),
                description = stringResource(R.string.notification_preference_description),
                isChecked = areNotificationsEnabled,
                onCheckedChange = {
                    settingsViewModel.toggleNotifications()
                }
            )

            Spacer(Modifier.height(spacingBetweenPreferences))

            TogglingPreference(
                label = stringResource(R.string.battery_optimizations_exemption_preference),
                description = stringResource(R.string.battery_optimizations_exemption_description),
                isChecked = isIgnoringBatteryOptimizations,
                onCheckedChange = {
                    settingsViewModel.openBatteryOptimizationsSettings()
                }
            )
        }

        PreferenceGroup (
            name = stringResource(R.string.data_logging_and_database_management_group),
        ) {
            TogglingPreference(
                label = stringResource(R.string.data_logging_preference),
                description = null,
                isChecked = isDataLoggingEnabled,
                enabled = true,
                onCheckedChange = {
                    settingsViewModel.toggleDataLogging()
                },
            )

            Spacer(Modifier.height(spacingBetweenPreferences))

            TogglingPreference(
                label = "Autostart logging after boot",
                description = "Starts the data logging foreground service automatically after the device boots up",
                isChecked = isAutostartLoggingServiceEnabled,
                enabled = isDataLoggingEnabled,
                onCheckedChange = {
                    settingsViewModel.toggleIsAutostartLoggingServiceChoice()
                },
            )

            Spacer(Modifier.height(spacingBetweenPreferences))

            Column(
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(15.dp)
                    )
                    .padding(20.dp)
                    .fillMaxWidth()
                ,
                horizontalAlignment = Alignment.Start
            ) {
                Column() {
                    Text(
                        text = "Database management",
                        style = MaterialTheme.typography.bodyLarge,
                    )

                    Spacer(Modifier.height(40.dp))

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth(0.7f)
                        ){
                            ImportForm()

                            Spacer(Modifier.height(10.dp))

                            ExportForm()

                            Spacer(Modifier.height(10.dp))

                            ResetForm()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TogglingPreference(
    modifier: Modifier = Modifier,
    label: String,
    description: String? = null,
    isChecked: Boolean,
    enabled: Boolean = true,
    onCheckedChange: () -> Unit,
) {
    Column(
        modifier = modifier
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(15.dp)
            )
            .padding(horizontal = 20.dp, vertical = 20.dp)
            .fillMaxWidth()

    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .fillMaxWidth()
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxWidth(0.7f),
            )

            Switch(
                checked = isChecked,
                onCheckedChange = { onCheckedChange() },
                enabled = enabled,
            )
        }

        if (description != null) {
            Spacer(Modifier.height(30.dp))

            Text(
                text = description,
                modifier = Modifier.padding(vertical = 10.dp),
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }

    Spacer(Modifier.height(10.dp))
}


@Composable
fun PreferenceGroup(
    name: String,
    block: @Composable () -> Unit
) {
    Card(
        shape = RoundedCornerShape(15.dp),
        border = BorderStroke(
            width = 2.dp,
            color = MaterialTheme.colorScheme.outline
        ),
        modifier = Modifier
            .fillMaxWidth(0.9f)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.titleSmall,
            )

            Spacer(Modifier.height(20.dp))

            block()
        }
    }

    Spacer(Modifier.height(20.dp))
}