package com.example.stats

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() {
    CenterAlignedTopAppBar(
        navigationIcon = { NavBackIcon() },
        title = { PageTitle() },
        actions = {
            PremiumIcon()
            OptionsIcon(
                onSettings = {},
                onPrivacyPolicy = {},
                onLicenses = {}
            )
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
            contentDescription = stringResource(R.string.nav_back_icon)
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
            contentDescription = stringResource(R.string.premium_icon)
        )
    }
}

@Composable
fun OptionsIcon(
    onSettings: () -> Unit,
    onPrivacyPolicy: () -> Unit,
    onLicenses: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    IconButton(
        onClick = {
            expanded = !expanded
        }
    ) {


        Box {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = stringResource(R.string.menu_icon)
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = {expanded = false}
            ) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.settings_option)) },
                    onClick = {
                        expanded = false
                        onSettings()
                    }
                )

                DropdownMenuItem(
                    text = { Text(stringResource(R.string.privacy_policy_option)) },
                    onClick = {
                        expanded = false
                        onPrivacyPolicy()
                    }
                )

                DropdownMenuItem(
                    text = { Text(stringResource(R.string.licenses_option)) },
                    onClick = {
                        expanded = false
                        onLicenses()
                    }
                )
            }
        }

    }
}



@Composable
@Preview(showBackground = true)
fun OptionsIconExpandedPreview() {
    IconButton(
        onClick = {}
    ) {
        var expanded by remember { mutableStateOf(true) }

        Box(
            modifier = Modifier
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = stringResource(R.string.menu_icon)
            )

            DropdownMenu(
                expanded = true,
                onDismissRequest = {expanded = false},
                modifier = Modifier.fillMaxSize()
            ) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.settings_option)) },
                    onClick = {
                        expanded = false
                    }
                )

                DropdownMenuItem(
                    text = { Text(stringResource(R.string.privacy_policy_option)) },
                    onClick = {
                        expanded = false
                    }
                )

                DropdownMenuItem(
                    text = { Text(stringResource(R.string.licenses_option)) },
                    onClick = {
                        expanded = false
                    }
                )
            }
        }

    }
}

@Composable
@Preview(showBackground = true)
fun OptionsIconNotExpandedPreview() {

    var expanded by remember { mutableStateOf(true) }

    IconButton(
        onClick = {}
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = stringResource(R.string.menu_icon)
            )

            DropdownMenu(
                expanded = false,
                onDismissRequest = {expanded = false},
                modifier = Modifier.fillMaxSize()
            ) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.settings_option)) },
                    onClick = {
                        expanded = false
                    }
                )

                DropdownMenuItem(
                    text = { Text(stringResource(R.string.privacy_policy_option)) },
                    onClick = {
                        expanded = false
                    }
                )

                DropdownMenuItem(
                    text = { Text(stringResource(R.string.licenses_option)) },
                    onClick = {
                        expanded = false
                    }
                )
            }
        }

    }
}