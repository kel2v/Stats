package com.example.stats

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
    IconButton(
        onClick = {}
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Navigate back to previous page"
        )
    }
}

@Composable
fun PageTitle() {
    Text("Page Title")
}

@Composable
fun PremiumIcon() {
    IconButton(
        onClick = {}
    ) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = "Upgrade to Premium"
        )
    }
}

@Composable
fun OptionsIcon() {
    IconButton(
        onClick = {}
    ) {
        Icon(
            imageVector = Icons.Default.Menu,
            contentDescription = "Menu"
        )
    }
}