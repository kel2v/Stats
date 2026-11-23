package com.bytemanager.stats.ui.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.bytemanager.stats.R
import com.bytemanager.stats.data.repository.NotificationIconInfoType

@Composable
fun Settings(settingsViewModel: SettingsViewModel = hiltViewModel()) {
    val spacingBetweenPreferences = @Composable { Spacer(Modifier.height(15.dp)) }
    val spacingBetweenPreferenceGroups = @Composable { Spacer(Modifier.height(25.dp)) }

    val settingsUiState by settingsViewModel.stateFlowOfSettingsUiState.collectAsState()

    val isDataLoggingEnabled= settingsUiState.isDataLoggingEnabled
    val areNotificationsEnabled = settingsUiState.isNotificationsEnabled
    val isFahrenheit = settingsUiState.isFahrenheit
    val isMovingAverageForVoltageGraphEnabled = settingsUiState.isMovingAverageForVoltageGraphEnabled
    val isCutmullRomCurveEnabled = settingsUiState.isCutmullRomCurveEnabled
    val isIgnoringBatteryOptimizations = settingsUiState.isIgnoringBatteryOptimizations
    val isAutostartLoggingServiceEnabled = settingsUiState.isAutostartLoggingServiceEnabled
    val selectedNotificationIconInfo = settingsUiState.selectedNotificationIconInfo

    val notificationIconChoices = listOf(
        RadioGroupChoice(
            label = "App icon",
            choiceNumber = NotificationIconInfoType.APP_ICON.ordinal,
            onClick = {
                settingsViewModel.setNotificationIconInfoPreference(NotificationIconInfoType.APP_ICON)
            }
        ),

        RadioGroupChoice(
            label = "Battery temperature",
            choiceNumber = NotificationIconInfoType.BATTERY_TEMPERATURE.ordinal,
            onClick = {
                settingsViewModel.setNotificationIconInfoPreference(NotificationIconInfoType.BATTERY_TEMPERATURE)
            }
        ),

        RadioGroupChoice(
            label = "Thermal throttling status",
            choiceNumber = NotificationIconInfoType.THROTTLING_STATUS.ordinal,
            onClick = {
                settingsViewModel.setNotificationIconInfoPreference(NotificationIconInfoType.THROTTLING_STATUS)
            }
        )
    )

    Column (
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        spacingBetweenPreferenceGroups()

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

        spacingBetweenPreferenceGroups()

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

            spacingBetweenPreferences()

            TogglingPreference(
                label = stringResource(R.string.catmull_rom_curve_preference),
                description = stringResource(R.string.catmull_rom_curve_preference_description),
                isChecked = isCutmullRomCurveEnabled,
                onCheckedChange = {
                    settingsViewModel.toggleCutmullRomCurveChoice()
                }
            )
        }

        spacingBetweenPreferenceGroups()

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

            spacingBetweenPreferences()

            TogglingPreference(
                label = stringResource(R.string.battery_optimizations_exemption_preference),
                description = stringResource(R.string.battery_optimizations_exemption_description),
                isChecked = isIgnoringBatteryOptimizations,
                onCheckedChange = {
                    settingsViewModel.openBatteryOptimizationsSettings()
                }
            )
        }

        spacingBetweenPreferenceGroups()

        PreferenceGroup (
            name = stringResource(R.string.data_logging_and_database_management_group),
        ) {
            TogglingPreference(
                label = stringResource(R.string.data_logging_preference),
                description = stringResource(R.string.data_logging_preference_description),
                isChecked = isDataLoggingEnabled,
                enabled = true,
                onCheckedChange = {
                    settingsViewModel.toggleDataLogging()
                },
            )

            spacingBetweenPreferences()

            TogglingPreference(
                label = stringResource(R.string.notify_battery_info_updates_preference),
                description = stringResource(R.string.notify_battery_info_updates_preference_description),
                isChecked = settingsUiState.shouldNotifyBatteryInfoUpdates,
                enabled = isDataLoggingEnabled,
                onCheckedChange = {
                    settingsViewModel.toggleNotifyBatteryInfoUpdates()
                }
            )

            spacingBetweenPreferences()

            TogglingPreference(
                label = stringResource(R.string.autostart_logging_on_boot_preference),
                description = stringResource(R.string.autostart_logging_on_boot_preference_description),
                isChecked = isAutostartLoggingServiceEnabled,
                enabled = isDataLoggingEnabled,
                onCheckedChange = {
                    settingsViewModel.toggleIsAutostartLoggingServiceChoice()
                },
            )

            spacingBetweenPreferences()

            OutlinedCard (
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(15.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {
                    Text(
                        text = stringResource(R.string.manage_database_preference),
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

        spacingBetweenPreferenceGroups()

        PreferenceGroup(
            name = "Miscellaneous"
        ) {
            RadioButtonPreference(
                label = stringResource(R.string.notification_icon_shows_preference),
                description = stringResource(R.string.notification_icon_shows_preference_description),
                choices = notificationIconChoices,
                selectedChoice = notificationIconChoices.find {
                    it.choiceNumber == selectedNotificationIconInfo
                }!!
            )
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
    OutlinedCard (
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(15.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
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
                Spacer(Modifier.height(15.dp))

                Text(
                    text = description,
                    modifier = Modifier.padding(vertical = 10.dp),
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }

    Spacer(Modifier.height(10.dp))
}

data class RadioGroupChoice(
    val label: String,
    val choiceNumber: Int,
    val onClick: () -> Unit,
)


@Composable
fun RadioButtonPreference(
    modifier: Modifier = Modifier,
    label: String,
    description: String? = null,
    choices: List<RadioGroupChoice>,
    selectedChoice: RadioGroupChoice,
) {
    OutlinedCard (
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(15.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .selectableGroup()
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
            )

            Spacer(Modifier.height(20.dp))

            choices.forEachIndexed { index, choice ->
                Row(
                    modifier = Modifier
                        .selectable(
                            selected = choice.choiceNumber == selectedChoice.choiceNumber,
                            onClick = choice.onClick,
                            role = Role.RadioButton
                        ),

                    verticalAlignment = Alignment.CenterVertically,

                ) {
                    RadioButton(
                        selected = choice.choiceNumber == selectedChoice.choiceNumber,
                        onClick = null
                    )

                    Spacer(Modifier.width(15.dp))

                    Text(
                        text = choice.label,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }

                if(index != choices.lastIndex)
                    Spacer(Modifier.height(15.dp))
            }

            if (description != null) {
                Spacer(Modifier.height(20.dp))

                Text(
                    text = description,
                    modifier = Modifier.padding(vertical = 10.dp),
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}

@Composable
fun PreferenceGroup(
    name: String,
    block: @Composable () -> Unit
) {
    Card (
        shape = RoundedCornerShape(15.dp),
        modifier = Modifier
            .fillMaxWidth(0.9f),
        elevation = CardDefaults.elevatedCardElevation(5.dp)
    ) {
        Column(
            modifier = Modifier.padding(15.dp)
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.titleSmall,
            )

            Spacer(Modifier.height(15.dp))

            block()
        }
    }
}