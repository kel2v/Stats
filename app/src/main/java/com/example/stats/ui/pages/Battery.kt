package com.example.stats.ui.pages

import android.os.BatteryManager
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stats.models.BatteryViewModel
import com.example.stats.ui.listitem.ListItem

@Composable
fun Battery(batteryViewModel: BatteryViewModel = viewModel()) {
    val batteryState by batteryViewModel.batteryStateStateFlow.collectAsState()

    val batteryHealth = when (batteryState.health) {
        BatteryManager.BATTERY_HEALTH_COLD -> "Cold"
        BatteryManager.BATTERY_HEALTH_DEAD -> "Dead"
        BatteryManager.BATTERY_HEALTH_GOOD -> "Good"
        BatteryManager.BATTERY_HEALTH_OVERHEAT -> "Overheat"
        BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "Overvoltage"
        BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> "Unspecified failure"
        else -> "Unknown"
    }

    val chargingStatus = when(batteryState.chargingStatus) {
        BatteryManager.BATTERY_STATUS_FULL -> "Full"
        BatteryManager.BATTERY_STATUS_CHARGING -> "Charging"
        BatteryManager.BATTERY_STATUS_DISCHARGING -> "Discharging"
        BatteryManager.BATTERY_STATUS_NOT_CHARGING -> "Not charging"
        else -> "Unknown"
    }

    Column {
        ListItem(parameterName = "battery type", parameterValue = batteryState.technology?:"null")
        ListItem(parameterName = "Battery level", parameterValue = "${batteryState.level}%")
        ListItem(parameterName = "Battery voltage", parameterValue = "${batteryState.voltage/1000f} V")
        ListItem(parameterName = "Battery temperature", parameterValue = "${batteryState.temperature/10f} Celsius")
        ListItem(parameterName = "Battery health", parameterValue = batteryHealth)
        ListItem(parameterName = "Charging status", parameterValue = chargingStatus)
    }

}