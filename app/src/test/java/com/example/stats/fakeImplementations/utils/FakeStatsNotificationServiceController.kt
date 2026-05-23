package com.example.stats.fakeImplementations.utils

import com.example.stats.interfaces.StatsNotificationServiceControllerInterface
import com.example.stats.services.StatsLoggingNotificationService

class FakeStatsNotificationServiceController: StatsNotificationServiceControllerInterface {
    override fun startForegroundService() {
        StatsLoggingNotificationService._isRunning.value = true
    }

    override fun stopService() {
        StatsLoggingNotificationService._isRunning.value = false
    }
}