package com.example.stats

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() {
    TopAppBar(
        navigationIcon = { NavBackIcon() },
        title = { PageTitle() },
        actions = {
            PremiumIcon()
            OptionsIcon()
        }
    )
}

@Composable
fun NavBackIcon() {
    Text("NBI")
}

@Composable
fun PageTitle() {
    Text("Page Title")
}

@Composable
fun PremiumIcon() {
    Text("PI")
}

@Composable
fun OptionsIcon() {
    Text("OI")
}