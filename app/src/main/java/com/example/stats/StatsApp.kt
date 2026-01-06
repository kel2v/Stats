package com.example.stats

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.stats.ui.bottombar.BottomBar
import com.example.stats.ui.bottombar.pages.Battery
import com.example.stats.ui.bottombar.pages.Network
import com.example.stats.ui.bottombar.pages.Dashboard
import com.example.stats.ui.topbar.pages.Licenses
import com.example.stats.ui.topbar.pages.Premium
import com.example.stats.ui.topbar.pages.PrivacyPolicy
import com.example.stats.ui.topbar.pages.Settings
import com.example.stats.ui.topbar.TopBar

enum class StatsAppScreen(@StringRes val title: Int) {
    Dashboard(title = R.string.dashboard_page_title),
    Battery(title = R.string.battery_page_title),
    Network(title = R.string.network_page_title),
    Premium(title = R.string.premium_page_title),
    Settings(title = R.string.settings_page_title),
    PrivacyPolicy(title = R.string.privacy_policy_page_title),
    Licenses(title = R.string.licenses_page_title)
}
@Composable
fun StatsApp(
    navController: NavHostController = rememberNavController()
) {

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = StatsAppScreen.valueOf(backStackEntry?.destination?.route ?: StatsAppScreen.Dashboard.name)

    Scaffold(
        topBar = {
            TopBar(
                canNavigateBack = navController.previousBackStackEntry != null,
                currentScreen = currentScreen,
                navigateUp = { navController.navigateUp() },
                navigatePremium = { navController.navigate(StatsAppScreen.Premium.name) },
                navigateSettings = { navController.navigate(StatsAppScreen.Settings.name) },
                navigatePrivacyPolicy = { navController.navigate(StatsAppScreen.PrivacyPolicy.name) },
                navigateLicenses = { navController.navigate(StatsAppScreen.Licenses.name) }
            )
        },
        bottomBar = {
            BottomBar(
                navigateBattery = {
                    navController.navigate(StatsAppScreen.Battery.name) {
                        launchSingleTop = true
                        popUpTo(0)
                        restoreState = true
                    }
                },
                navigateNetwork = {
                    navController.navigate(StatsAppScreen.Network.name) {
                        launchSingleTop = true
                        popUpTo(0)
                        restoreState = true
                    }
                },
                navigateDashboard = {
                    navController.navigate(StatsAppScreen.Dashboard.name) {
                        launchSingleTop = true
                        popUpTo(0)
                        restoreState = true
                    }
                }
            )
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = StatsAppScreen.Dashboard.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = StatsAppScreen.Dashboard.name) {
                Dashboard()
            }

            composable(route = StatsAppScreen.Battery.name) {
                Battery()
            }

            composable(route = StatsAppScreen.Network.name) {
                Network()
            }

            composable(route = StatsAppScreen.Premium.name) {
                Premium()
            }

            composable(route = StatsAppScreen.Settings.name) {
                Settings()
            }

            composable(route = StatsAppScreen.PrivacyPolicy.name) {
                PrivacyPolicy()
            }

            composable(route = StatsAppScreen.Licenses.name) {
                Licenses()
            }
        }
    }
}


@Composable
@Preview(showBackground = true)
fun StatsAppPreview(
    navController: NavHostController = rememberNavController()
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = StatsAppScreen.valueOf(backStackEntry?.destination?.route ?: StatsAppScreen.Dashboard.name)

    Scaffold(
        topBar = {
            TopBar(
                canNavigateBack = navController.previousBackStackEntry != null,
                currentScreen = currentScreen,
                navigateUp = { navController.navigateUp() },
                navigatePremium = { navController.navigate(StatsAppScreen.Premium.name) },
                navigateSettings = { navController.navigate(StatsAppScreen.Settings.name) },
                navigatePrivacyPolicy = { navController.navigate(StatsAppScreen.PrivacyPolicy.name) },
                navigateLicenses = { navController.navigate(StatsAppScreen.Licenses.name) }
            )
        },
        bottomBar = {
            BottomBar(
                navigateBattery = { navController.navigate(StatsAppScreen.Battery.name) },
                navigateNetwork = { navController.navigate(StatsAppScreen.Network.name) },
                navigateDashboard = { navController.navigate(StatsAppScreen.Dashboard.name) }
            )
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = StatsAppScreen.Dashboard.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = StatsAppScreen.Dashboard.name) {
                Dashboard()
            }

            composable(route = StatsAppScreen.Battery.name) {
                Battery()
            }

            composable(route = StatsAppScreen.Network.name) {
                Network()
            }

            composable(route = StatsAppScreen.Premium.name) {
                Premium()
            }

            composable(route = StatsAppScreen.Settings.name) {
                Settings()
            }

            composable(route = StatsAppScreen.PrivacyPolicy.name) {
                PrivacyPolicy()
            }

            composable(route = StatsAppScreen.Licenses.name) {
                Licenses()
            }
        }
    }
}