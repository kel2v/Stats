package com.bytemanager.stats.ui.pages

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.bytemanager.stats.R

@Composable
fun QuickSetup(
    viewModel: QuickSetupViewModel = hiltViewModel()
) {
    val uiState by viewModel.stateFlowOfQuickSetupUiState.collectAsState()

    val isNotificationsEnabled = uiState.isNotificationsEnabled
    val isDataLoggingEnabled = uiState.isDataLoggingEnabled
    val isIgnoringBatteryOptimizations = uiState.isIgnoringBatteryOptimizations
    val isAutostartLoggingServiceEnabled = uiState.isAutostartLoggingServiceEnabled

    Column (
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.85f),
            elevation = CardDefaults.elevatedCardElevation(30.dp),
            shape = RoundedCornerShape(15.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.quick_setup_description),
                        style = MaterialTheme.typography.titleSmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp)
                    )
                }

                Spacer(Modifier.height(10.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                        .weight(0.9f)
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.outline,
                            RoundedCornerShape(15.dp)
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Spacer(modifier = Modifier.height(10.dp))

                    Column(
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                            .weight(0.95f)
                    ) {
                        PreferenceGroup(
                            name = stringResource(R.string.features_preference_group),
                        ) {
                            TogglingPreference(
                                label = stringResource(R.string.data_logging_preference),
                                description = stringResource(R.string.data_logging_preference_description),
                                isChecked = isDataLoggingEnabled,
                                onCheckedChange = {
                                    viewModel.toggleDataLogging()
                                },
                            )

                            Spacer(Modifier.height(10.dp))

                            TogglingPreference(
                                label = stringResource(R.string.autostart_logging_on_boot_preference),
                                description = stringResource(R.string.autostart_logging_on_boot_preference_description),
                                isChecked = isAutostartLoggingServiceEnabled,
                                enabled = isDataLoggingEnabled,
                                onCheckedChange = {
                                    viewModel.toggleIsAutostartLoggingServiceChoice()
                                },
                            )
                        }

                        Spacer(Modifier.height(25.dp))

                        PreferenceGroup(
                            name = stringResource(R.string.permissions_preference_group),
                        ) {
                            TogglingPreference(
                                label = stringResource(R.string.notification_preference),
                                description = stringResource(R.string.notification_preference_description),
                                isChecked = isNotificationsEnabled,
                                onCheckedChange = {
                                    viewModel.openNotificationsSettings()
                                }
                            )

                            Spacer(Modifier.height(10.dp))

                            TogglingPreference(
                                label = stringResource(R.string.battery_optimizations_exemption_preference),
                                description = stringResource(R.string.battery_optimizations_exemption_description),
                                isChecked = isIgnoringBatteryOptimizations,
                                onCheckedChange = {
                                    viewModel.openBatteryOptimizationsSettings()
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                }

                Spacer(Modifier.height(10.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    TextButton(
                        onClick = {
                            viewModel.clickGettingStartedCardSkipButton()
                        },
                    ) {
                        Text(
                            text = stringResource(R.string.skip_button),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }

                    Button(
                        onClick = {
                            viewModel.clickQuickSetupDoneButton()
                        },
                        enabled = isDataLoggingEnabled && isNotificationsEnabled && isIgnoringBatteryOptimizations && isAutostartLoggingServiceEnabled
                    ) {
                        Text(
                            text = stringResource(R.string.done_button),
                        )
                    }
                }

                Spacer(Modifier.height(10.dp))
            }
        }
    }
}