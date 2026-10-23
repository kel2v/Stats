package com.bytemanager.stats

import androidx.annotation.StringRes
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.bytemanager.stats.ui.pages.Battery
import com.bytemanager.stats.ui.pages.BatteryGraphs
import com.bytemanager.stats.ui.pages.PrivacyPolicy
import com.bytemanager.stats.ui.pages.Settings
import com.bytemanager.stats.ui.topbar.TopBar

enum class StatsAppScreen(@StringRes val title: Int) {
    BATTERY_DASHBOARD(title = R.string.battery_dashboard_title),
    Settings(title = R.string.settings_page_title),
    PrivacyPolicy(title = R.string.privacy_policy_page_title),
    BATTERY_GRAPHS(title = R.string.battery_graphs_page_title)
}
@Composable
fun StatsApp(
    navController: NavHostController = rememberNavController()
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = StatsAppScreen.valueOf(backStackEntry?.destination?.route ?: StatsAppScreen.BATTERY_DASHBOARD.name)

    Scaffold(
        topBar = {
            TopBar(
                currentScreen = currentScreen,
                canNavigateUp = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() },
                navigateSettings = { navController.navigate(StatsAppScreen.Settings.name) },
                navigatePrivacyPolicy = { navController.navigate(StatsAppScreen.PrivacyPolicy.name) },
            )
        },
        bottomBar = { }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = StatsAppScreen.BATTERY_DASHBOARD.name,
            modifier = Modifier.padding(innerPadding)
        ) {

            composable(
                route = StatsAppScreen.BATTERY_DASHBOARD.name,
                enterTransition = { fadeIn() },
                exitTransition = { fadeOut() },
                popEnterTransition = { fadeIn() },
                popExitTransition = { fadeOut() }
            ) {
                Battery(
                    onClickBatteryGraphsButton = {
                        navController.navigate(StatsAppScreen.BATTERY_GRAPHS.name)
                    }
                )
            }

            composable(
                route = StatsAppScreen.Settings.name,
                enterTransition = { fadeIn() },
                exitTransition = { fadeOut() },
                popEnterTransition = { fadeIn() },
                popExitTransition = { fadeOut() }
            ) {
                Settings()
            }

            composable(
                route = StatsAppScreen.PrivacyPolicy.name,
                enterTransition = { fadeIn() },
                exitTransition = { fadeOut() },
                popEnterTransition = { fadeIn() },
                popExitTransition = { fadeOut() }
            ) {
                PrivacyPolicy()
            }

            composable(
                route = StatsAppScreen.BATTERY_GRAPHS.name,
                enterTransition = { fadeIn() },
                exitTransition = { fadeOut() },
                popEnterTransition = { fadeIn() },
                popExitTransition = { fadeOut() }
            ) {
                BatteryGraphs()
            }
        }
    }
}