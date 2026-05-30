package com.example.stats.utils

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.stats.interfaces.StatsNotificationServiceControllerInterface
import com.example.stats.services.StatsLoggingNotificationService

class StatsNotificationServiceController(private val appContext: Context): StatsNotificationServiceControllerInterface {
    private val intent = Intent(appContext, StatsLoggingNotificationService::class.java)

    override fun startForegroundService() {
        Log.d("DEBUGGING LOGS", "starting `StatsLoggingNotificationService`.")
        ContextCompat.startForegroundService(appContext, intent)
    }

    override fun stopService() {
        Log.d("DEBUGGING LOGS", "stopping `StatsLoggingNotificationService`.")
        appContext.stopService(intent)
    }

    fun isRunning(): Boolean {
        return StatsLoggingNotificationService.isRunning.value
    }
}