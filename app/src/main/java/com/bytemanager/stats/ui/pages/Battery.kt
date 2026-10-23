package com.bytemanager.stats.ui.pages

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
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
import com.bytemanager.stats.ui.listitem.ListItem
import com.bytemanager.stats.ui.theme.AppTypography

@SuppressLint("UnrememberedMutableState")
@Composable
fun Battery(
    batteryViewModel: BatteryViewModel = hiltViewModel(),
    onClickBatteryGraphsButton: () -> Unit,
) {
    val batteryUiState by batteryViewModel.stateFlowOfBatteryUiState.collectAsState()

    val batteryState = batteryUiState.timestampedBatteryState
    val batteryTechnology = batteryUiState.batteryTechnology
    val isFahrenheit = batteryUiState.isFahrenheit

    Column(
        Modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 15.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(20.dp))

        ListItem(
            parameterName = stringResource(R.string.battery_type_label),
            parameterValue = batteryTechnology,
        )
        ListItem(
            parameterName = stringResource(R.string.battery_health_label),
            parameterValue = batteryState.batteryHealth?:"-",
        )
        ListItem(
            parameterName = stringResource(R.string.charging_status_label),
            parameterValue = batteryState.chargingStatus?:"-",
        )

        ListItem(
            parameterName = stringResource(R.string.battery_level_label),
            parameterValue = "${batteryState.level?:"-"}%",
        )
        ListItem(
            parameterName = stringResource(R.string.battery_voltage_label),
            parameterValue = "${batteryState.voltage?:"-"} V",
        )
        ListItem(
            parameterName = stringResource(R.string.battery_temperature_label),
            parameterValue = "${batteryState.temperature?:"-"} °${if(isFahrenheit) "F" else "C"}",
        )

        Spacer(Modifier.height(40.dp))

        Button(
            onClick = {
                onClickBatteryGraphsButton()
            }
        ) {
            Text(
                text = stringResource(R.string.battery_graphs_button),
                style = AppTypography.labelLarge
            )
        }

        Spacer(Modifier.height(40.dp))
    }
}