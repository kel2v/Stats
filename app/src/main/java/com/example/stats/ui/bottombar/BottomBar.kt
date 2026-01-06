package com.example.stats.ui.bottombar

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.example.stats.R
import com.example.stats.StatsAppScreen

@Composable
fun BottomBar(
    navigateDashboard: () -> Unit,
    navigateBattery: () -> Unit,
    navigateNetwork: () -> Unit
) {
    BottomAppBar {
        Row {
            BottomButton(
                name = stringResource(R.string.dashboard_button),
                route = StatsAppScreen.Dashboard.name,
                fontSize = 20.sp,
                onClick = navigateDashboard
            )

            BottomButton(
                name = stringResource(R.string.battery_button),
                route = StatsAppScreen.Battery.name,
                fontSize = 20.sp,
                onClick = navigateBattery
            )

            BottomButton(
                name = stringResource(R.string.network_button),
                route = StatsAppScreen.Network.name,
                fontSize = 20.sp,
                onClick = navigateNetwork
            )
        }

    }
}

@Composable
fun RowScope.BottomButton(
    name: String,
    route: String,
    fontSize: TextUnit,
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.weight(1f)
    ) {
        Text(
            text = name,
            fontSize = fontSize
        )
    }
}