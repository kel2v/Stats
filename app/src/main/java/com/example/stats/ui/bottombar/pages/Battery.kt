package com.example.stats.ui.bottombar.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun Battery(batteryViewModel: BatteryViewModel = viewModel()) {
    val currentNow by batteryViewModel.currentNowStateFlow.collectAsState()
    val batteryState by batteryViewModel.batteryStateStateFlow.collectAsState()
    Column {
        Text("Battery Page")
        Text("Battery type = ${batteryState.technology}")
        Text("Battery level = ${batteryState.level}%")
        Text("Battery voltage = ${batteryState.voltage} V")
        Text("Battery current (now) = $currentNow mA")
        Text("Battery temperature = ${batteryState.temperature} Celsius")
        Text("Battery health = ${batteryState.health}")
        Text("Charging status = ${batteryState.chargingStatus}")
    }

}