package com.example.stats.models

import com.example.stats.fakeImplementations.models.FakeBatteryViewModel
import com.example.stats.fakeImplementations.utils.FakeStatsNotificationServiceController
import com.example.stats.services.StatsLoggingNotificationService
import kotlin.test.Test
import kotlin.test.assertEquals

class BatteryViewModelTest {

    @Test
    fun togglePersistenceNotification_initiallyIsNotRunning_setIsRunningTrue() {
        val fakeBatteryViewModel = FakeBatteryViewModel()
        val fakeStatsNotificationServiceController = FakeStatsNotificationServiceController()
        StatsLoggingNotificationService._isRunning.value = false
        fakeBatteryViewModel.togglePersistenceNotification(fakeStatsNotificationServiceController)
        assertEquals(StatsLoggingNotificationService._isRunning.value, true)
    }

    @Test
    fun togglePersistenceNotification_initiallyIsRunning_setIsRunningFalse() {
        val fakeBatteryViewModel = FakeBatteryViewModel()
        val fakeStatsNotificationServiceController = FakeStatsNotificationServiceController()
        StatsLoggingNotificationService._isRunning.value = true
        fakeBatteryViewModel.togglePersistenceNotification(fakeStatsNotificationServiceController)
        assertEquals(StatsLoggingNotificationService._isRunning.value, false)
    }
}