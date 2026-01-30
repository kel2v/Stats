package com.example.stats.ui.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stats.ui.listitem.ListItem

@Composable
fun Battery(batteryViewModel: BatteryViewModel = viewModel()) {
    val currentNow by batteryViewModel.currentNowStateFlow.collectAsState()
    val batteryState by batteryViewModel.batteryStateStateFlow.collectAsState()
    Column {

        val list = listOf(
            listOf(
                "Battery type",
                batteryState.technology?:"null"
            ),

            listOf(
                "Battery level",
                batteryState.level?.toString() + "%"
            ),

            listOf(
                "Battery voltage",
                batteryState.voltage?.toString() + " V"
            ),

            listOf(
                "Battery current (now)",
                currentNow.toString() + "mA"
            ),

            listOf(
                "Battery temperature",
                batteryState.temperature?.toString() + " Celsius"
            ),

            listOf(
                "Battery health",
                batteryState.health?:"null"
            ),

            listOf(
                "Charging status",
                batteryState.chargingStatus?:"null"
            )
        )

        list.forEach {
            ListItem(
                modifier = Modifier,
                parameterName = it[0],
                parameterValue = it[1]
            )
        }

//        Text("Battery type = ${batteryState.technology}")
//        Text("Battery level = ${batteryState.level}%")
//        Text("Battery voltage = ${batteryState.voltage} V")
//        Text("Battery current (now) = $currentNow mA")
//        Text("Battery temperature = ${batteryState.temperature} Celsius")
//        Text("Battery health = ${batteryState.health}")
//        Text("Charging status = ${batteryState.chargingStatus}")
    }

}