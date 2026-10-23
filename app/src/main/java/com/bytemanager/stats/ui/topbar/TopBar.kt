package com.bytemanager.stats.ui.topbar

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bytemanager.stats.R
import com.bytemanager.stats.StatsAppScreen
import com.bytemanager.stats.ui.theme.AppTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    topBarViewModel: TopBarViewModel = viewModel(),
    currentScreen: StatsAppScreen,
    canNavigateUp: Boolean,
    navigateUp: () -> Unit,
    navigateSettings: () -> Unit,
    navigatePrivacyPolicy: () -> Unit,
) {
    val uiState = topBarViewModel.uiState.collectAsState()

    CenterAlignedTopAppBar(
        navigationIcon = { if (canNavigateUp) { NavBackIcon(navigateUp) } },
        title = { PageTitle(stringResource(currentScreen.title)) },
        actions = {
            OptionsIcon(
                isOptionsMenuExpanded = uiState.value.isOptionsMenuExpanded,
                setExpandedFlag = { value: Boolean -> topBarViewModel.setIsOptionsMenuExpanded(value) },
                currentScreen = currentScreen,
                navigateSettings = navigateSettings,
                navigatePrivacyPolicy = navigatePrivacyPolicy,
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
    Text(
        text = title,
        style = AppTypography.headlineSmall
    )
}

@Composable
fun OptionsIcon(
    isOptionsMenuExpanded: Boolean,
    setExpandedFlag: (Boolean) -> Unit,
    currentScreen: StatsAppScreen,
    navigateSettings: () -> Unit,
    navigatePrivacyPolicy: () -> Unit,
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
                        text = {
                            Text(
                                text = stringResource(R.string.settings_option),
                                style = AppTypography.labelLarge
                            )
                        },
                        onClick = {
                            setExpandedFlag(false)
                            navigateSettings()
                        }
                    )
                }

                if (currentScreen != StatsAppScreen.PrivacyPolicy) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = stringResource(R.string.privacy_policy_option),
                                style = AppTypography.labelLarge
                            )
                        },
                        onClick = {
                            setExpandedFlag(false)
                            navigatePrivacyPolicy()
                        }
                    )
                }
            }
        }

    }
}