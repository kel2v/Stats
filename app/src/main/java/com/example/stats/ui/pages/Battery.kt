package com.example.stats.ui.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stats.ui.listitem.ListItem

@Composable
fun Battery(batteryViewModel: BatteryViewModel = viewModel()) {
    val batteryState by batteryViewModel.batteryStateStateFlow.collectAsState()

    Column {
        ListItem(parameterName = "battery type", parameterValue = batteryState.technology?:"null")
        ListItem(parameterName = "Battery level", parameterValue = "${batteryState.level}")
        ListItem(parameterName = "Battery voltage", parameterValue = "${batteryState.voltage/1000f} V")
        ListItem(parameterName = "Battery temperature", parameterValue = "${batteryState.temperature/10f} Celsius")
        ListItem(parameterName = "Battery health", parameterValue = "${batteryState.health}")
        ListItem(parameterName = "Charging status", parameterValue = "${batteryState.chargingStatus}")
    }

}