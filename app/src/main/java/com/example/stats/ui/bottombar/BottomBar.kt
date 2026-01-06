package com.example.stats.ui.bottombar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.example.stats.R
import com.example.stats.StatsAppScreen

@Composable
fun BottomBar(
    currentScreen: StatsAppScreen,
    navigateDashboard: () -> Unit,
    navigateBattery: () -> Unit,
    navigateNetwork: () -> Unit
) {
    BottomAppBar {
        Row {
            BottomButton(
                name = stringResource(R.string.dashboard_button),
                fontSize = 20.sp,
                onClick = navigateDashboard,
                fontWeight = if(currentScreen == StatsAppScreen.Dashboard) FontWeight.ExtraBold else FontWeight.Light
            )

            BottomButton(
                name = stringResource(R.string.battery_button),
                fontSize = 20.sp,
                onClick = navigateBattery,
                fontWeight = if(currentScreen == StatsAppScreen.Battery) FontWeight.ExtraBold else FontWeight.Light
            )

            BottomButton(
                name = stringResource(R.string.network_button),
                fontSize = 20.sp,
                onClick = navigateNetwork,
                fontWeight = if(currentScreen == StatsAppScreen.Network) FontWeight.ExtraBold else FontWeight.Light
            )
        }

    }
}

@Composable
fun RowScope.BottomButton(
    name: String,
    fontSize: TextUnit,
    fontWeight: FontWeight,
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.weight(1f)
    ) {
        Text(
            text = name,
            fontSize = fontSize,
            fontWeight = fontWeight
        )
    }
}