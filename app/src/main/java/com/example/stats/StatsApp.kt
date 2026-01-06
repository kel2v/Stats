package com.example.stats

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun StatsApp() {
    Scaffold(
        topBar = { TopBar() },
        bottomBar = { BottomBar() }
    ) { innerPadding ->
        innerPadding
        Content(innerPadding)
    }
}


@Composable
@Preview(showBackground = true)
fun StatsAppPreview() {
    Scaffold(
        topBar = { TopBar() },
        bottomBar = { BottomBar() }
    ) { innerPadding ->
        Content(innerPadding)
    }
}