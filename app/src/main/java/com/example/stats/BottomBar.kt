package com.example.stats

import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun BottomBar() {
    BottomAppBar() {
        Battery()
        Network()
    }
}

@Composable
fun Battery() {
    Text("Battery")
}

@Composable
fun Network() {
    Text("Network")
}