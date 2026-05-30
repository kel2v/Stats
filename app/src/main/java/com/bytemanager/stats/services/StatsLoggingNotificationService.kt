package com.bytemanager.stats.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.bytemanager.stats.interfaces.BatteryTempHistoryRepositoryInterface
import com.bytemanager.stats.notification.StatsLogger
import com.bytemanager.stats.notification.StatsNotificationManager
import com.bytemanager.stats.repository.BatteryStateRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class StatsLoggingNotificationService: Service() {
    companion object {
        private var _isRunning = MutableStateFlow(false)
        val isRunning = _isRunning.asStateFlow()
    }


    @Inject lateinit var batteryStateRepository: BatteryStateRepository
    @Inject lateinit var batteryTempHistoryRepository: BatteryTempHistoryRepositoryInterface
    @Inject lateinit var statsLogger: StatsLogger
    private var scope  = CoroutineScope(SupervisorJob() + Dispatchers.Default) // there's no chance of accessing scope after scope.cancel() in onDestroy() as OS destroy instance on onDestroy is called
    private var job: Job? = null


    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        _isRunning.value = true
        Log.d("DEBUGGING LOGS", "Service onCreate called")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) {
            Log.d("DEBUGGING LOGS", "Restarted by OS with null intent, stopping.")
            stopSelf()
            return START_NOT_STICKY
        }

        Log.d("DEBUGGING LOGS", "Running onStartCommand")

        if(job?.isActive == true) {
            Log.d("DEBUGGING LOGS", "StatLogger job is already running, skipping ...")
        } else {
            job = scope.launch {
                try {
                    statsLogger.startStatsLogger(::startForeground)
                } catch (e: Exception) {
                    Log.d("DEBUGGING LOGS", "Exception occurred: $e")
                } finally {
                    stopSelf(startId)
                }
            }

            Log.d("DEBUGGING LOGS", "No active StatLogger job found, starting StatLogger job.")
        }


        Log.d("DEBUGGING LOGS", "Exiting onStartCommand")
        return START_STICKY
    }

    override fun onDestroy() {
        Log.d("DEBUGGING LOGS", "Running StatsNotificationService.onDestroy")
        scope.cancel()
        StatsNotificationManager.closeStatsNotificationChannel(applicationContext)
        stopForeground(STOP_FOREGROUND_REMOVE)
        _isRunning.value = false
        super.onDestroy()
    }
}