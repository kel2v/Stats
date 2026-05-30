package com.bytemanager.stats

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
import com.bytemanager.stats.ui.pages.Battery
import com.bytemanager.stats.ui.pages.Licenses
import com.bytemanager.stats.ui.pages.Premium
import com.bytemanager.stats.ui.pages.PrivacyPolicy
import com.bytemanager.stats.ui.pages.Settings
import com.bytemanager.stats.ui.topbar.TopBar

enum class StatsAppScreen(@StringRes val title: Int) {
    Battery(title = R.string.battery_page_title),
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
    val currentScreen = StatsAppScreen.valueOf(backStackEntry?.destination?.route ?: StatsAppScreen.Battery.name)

    Scaffold(
        topBar = {
            TopBar(
                currentScreen = currentScreen,
                canNavigateUp = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() },
                navigatePremium = { navController.navigate(StatsAppScreen.Premium.name) },
                navigateSettings = { navController.navigate(StatsAppScreen.Settings.name) },
                navigatePrivacyPolicy = { navController.navigate(StatsAppScreen.PrivacyPolicy.name) },
                navigateLicenses = { navController.navigate(StatsAppScreen.Licenses.name) }
            )
        },
        bottomBar = { }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = StatsAppScreen.Battery.name,
            modifier = Modifier.padding(innerPadding)
        ) {

            composable(route = StatsAppScreen.Battery.name) {
                Battery()
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
    val currentScreen = StatsAppScreen.valueOf(backStackEntry?.destination?.route ?: StatsAppScreen.Battery.name)

    Scaffold(
        topBar = {
            TopBar(
                canNavigateUp = navController.previousBackStackEntry != null,
                currentScreen = currentScreen,
                navigateUp = { navController.navigateUp() },
                navigatePremium = { navController.navigate(StatsAppScreen.Premium.name) },
                navigateSettings = { navController.navigate(StatsAppScreen.Settings.name) },
                navigatePrivacyPolicy = { navController.navigate(StatsAppScreen.PrivacyPolicy.name) },
                navigateLicenses = { navController.navigate(StatsAppScreen.Licenses.name) }
            )
        },
        bottomBar = {
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = StatsAppScreen.Battery.name,
            modifier = Modifier.padding(innerPadding)
        ) {

            composable(route = StatsAppScreen.Battery.name) {
                Battery()
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