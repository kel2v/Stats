package com.example.stats.utils

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.example.stats.interfaces.StatsNotificationServiceControllerInterface
import com.example.stats.services.StatsNotificationService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StatsNotificationServiceController @Inject constructor(@ApplicationContext private val appContext: Context): StatsNotificationServiceControllerInterface {
    private val intent = Intent(appContext, StatsNotificationService::class.java)

    override fun startForegroundService() {
        ContextCompat.startForegroundService(appContext,intent)
    }

    override fun stopService() {
        appContext.stopService(intent)
    }
}