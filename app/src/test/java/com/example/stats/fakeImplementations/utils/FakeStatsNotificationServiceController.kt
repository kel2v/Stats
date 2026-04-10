package com.example.stats.fakeImplementations.utils

import com.example.stats.interfaces.StatsNotificationServiceControllerInterface
import com.example.stats.services.StatsNotificationService

class FakeStatsNotificationServiceController: StatsNotificationServiceControllerInterface {
    override fun startForegroundService() {
        StatsNotificationService._isRunning.value = true
    }

    override fun stopService() {
        StatsNotificationService._isRunning.value = false
    }
}