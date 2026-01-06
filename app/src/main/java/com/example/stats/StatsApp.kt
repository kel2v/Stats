package com.example.stats

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun StatsApp(innerPadding: PaddingValues) {
    Scaffold(
        topBar = { TopBar() },
        bottomBar = { BottomBar() }
    ) { innerPadding ->
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