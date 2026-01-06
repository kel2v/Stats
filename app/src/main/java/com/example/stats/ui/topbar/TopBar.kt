package com.example.stats.ui.topbar

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stats.R
import com.example.stats.StatsAppScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    topBarViewModel: TopBarViewModel = viewModel(),
    currentScreen: StatsAppScreen,
    canNavigateUp: Boolean,
    navigateUp: () -> Unit,
    navigatePremium: () -> Unit,
    navigateSettings: () -> Unit,
    navigatePrivacyPolicy: () -> Unit,
    navigateLicenses: () -> Unit
) {
    val uiState = topBarViewModel.uiState.collectAsState()

    CenterAlignedTopAppBar(
        navigationIcon = { if (canNavigateUp) { NavBackIcon(navigateUp) } },
        title = { PageTitle(stringResource(currentScreen.title)) },
        actions = {
            if (currentScreen != StatsAppScreen.Premium) {
                PremiumIcon(
                    onClick = navigatePremium
                )
            }
            OptionsIcon(
                isOptionsMenuExpanded = uiState.value.isOptionsMenuExpanded,
                setExpandedFlag = { value: Boolean -> topBarViewModel.setIsOptionsMenuExpanded(value) },
                currentScreen = currentScreen,
                navigateSettings = navigateSettings,
                navigatePrivacyPolicy = navigatePrivacyPolicy,
                navigateLicenses = navigateLicenses
            )
        }
    )
}

@Composable
fun NavBackIcon(navigateUp: () -> Unit) {
    IconButton(
        onClick = navigateUp
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = stringResource(R.string.nav_back_icon)
        )
    }
}

@Composable
fun PageTitle(title: String) {
    Text(title)
}

@Composable
fun PremiumIcon(onClick: () -> Unit) {
    IconButton(
        onClick = onClick
    ) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = stringResource(R.string.premium_icon)
        )
    }
}

@Composable
fun OptionsIcon(
    isOptionsMenuExpanded: Boolean,
    setExpandedFlag: (Boolean) -> Unit,
    currentScreen: StatsAppScreen,
    navigateSettings: () -> Unit,
    navigatePrivacyPolicy: () -> Unit,
    navigateLicenses: () -> Unit
) {
    IconButton(
        onClick = {
            setExpandedFlag(!isOptionsMenuExpanded)
        }
    ) {


        Box {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = stringResource(R.string.menu_icon)
            )

            DropdownMenu(
                expanded = isOptionsMenuExpanded,
                onDismissRequest = { setExpandedFlag(false) }
            ) {
                if (currentScreen != StatsAppScreen.Settings) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.settings_option)) },
                        onClick = {
                            setExpandedFlag(false)
                            navigateSettings()
                        }
                    )
                }

                if (currentScreen != StatsAppScreen.PrivacyPolicy) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.privacy_policy_option)) },
                        onClick = {
                            setExpandedFlag(false)
                            navigatePrivacyPolicy()
                        }
                    )
                }

                if (currentScreen != StatsAppScreen.Licenses) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.licenses_option)) },
                        onClick = {
                            setExpandedFlag(false)
                            navigateLicenses()
                        }
                    )
                }
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
            modifier = Modifier.Companion
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = stringResource(R.string.menu_icon)
            )

            DropdownMenu(
                expanded = true,
                onDismissRequest = { expanded = false },
                modifier = Modifier.Companion.fillMaxSize()
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
            modifier = Modifier.Companion.fillMaxSize()
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = stringResource(R.string.menu_icon)
            )

            DropdownMenu(
                expanded = false,
                onDismissRequest = { expanded = false },
                modifier = Modifier.Companion.fillMaxSize()
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