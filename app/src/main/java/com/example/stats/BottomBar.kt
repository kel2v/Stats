package com.example.stats

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp

@Composable
fun BottomBar() {
    BottomAppBar {
        Row {
            Battery()
            Network()
        }

    }
}

@Composable
fun RowScope.Battery() {
    TextButton(
        onClick = {},
        modifier = Modifier.weight(1f)
    ) {
        Text(
            text = stringResource(R.string.battery_menu),
            fontSize = 20.sp
        )
    }
}

@Composable
fun RowScope.Network() {
    TextButton(
        onClick = {},
        modifier = Modifier.weight(1f)
    ) {
        Text(
            text = stringResource(R.string.network_menu),
            fontSize = 20.sp
        )
    }
}