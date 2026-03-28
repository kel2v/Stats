package com.example.stats.ui.pages

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.example.stats.models.BatteryViewModel
import com.example.stats.services.StatsNotificationService
import com.example.stats.ui.listitem.ListItem

@SuppressLint("UnrememberedMutableState")
@Composable
fun Battery(batteryViewModel: BatteryViewModel = hiltViewModel(checkNotNull(LocalViewModelStoreOwner.current) {
            "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
        })
) {
    val batteryState by batteryViewModel.batteryStateStateFlow.collectAsState()

    Column {
        ListItem(parameterName = "battery type", parameterValue = batteryState.technology?:"null")
        ListItem(parameterName = "Battery level", parameterValue = "${batteryState.level}%")
        ListItem(parameterName = "Battery voltage", parameterValue = "${batteryState.voltage} V")
        ListItem(parameterName = "Battery temperature", parameterValue = "${batteryState.temperature} Celsius")
        ListItem(parameterName = "Battery health", parameterValue = batteryState.health)
        ListItem(parameterName = "Charging status", parameterValue = batteryState.chargingStatus)

        Spacer(Modifier.height(40.dp))

        Button(
            onClick = { batteryViewModel.togglePersistenceNotification() },
            modifier = Modifier.height(60.dp).fillMaxWidth()
        ) {
            Text(if(StatsNotificationService.isRunning) "Persistence Notification is ON" else "Persistence Notification is OFF")
        }
    }

}