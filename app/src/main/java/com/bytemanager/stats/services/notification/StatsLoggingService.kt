package com.bytemanager.stats.services.notification

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.content.ContextCompat
import com.bytemanager.stats.data.repository.BatteryStateRepository
import com.bytemanager.stats.utils.ErrorHandler
import com.bytemanager.stats.utils.StatsLogger
import com.bytemanager.stats.utils.StatsNotificationManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@AndroidEntryPoint
class StatsLoggingService: Service() {
    private var job: Job? = null
    @Inject
    lateinit var statsLogger: StatsLogger

    @Inject
    lateinit var errorHandler: ErrorHandler

    private val mutex = Mutex()

    @Inject
    lateinit var batteryStateRepository: BatteryStateRepository

    private lateinit var scope: CoroutineScope

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
        Log.d("StatsLoggingNotificationService", "running onCreate")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        StatsNotificationManager.createStatsNotificationChannel(applicationContext)
        val notification = StatsNotificationManager.buildStatsNotification(
            applicationContext,
            batteryStateRepository.stateFlowOfTimestampedBatteryState.value,
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(
                1,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            )
        } else {
            startForeground(1, notification)
        }



        scope.launch {
            Log.d("StatsLoggingNotificationService", "Running onStartCommand")

            try {
                mutex.withLock {
                    if(job != null) {
                        Log.d(
                            "StatsLoggingNotificationService",
                            "Job is already running, exiting onStartCommand ..."
                        )

                        return@withLock
                    }

                    val loggingJob = scope.launch {
                        try {
                            statsLogger.startStatsLogger()
                            stopSelf()

                        } catch (e: Exception) {
                            handleException(e, startId, job)
                        }
                    }

                    job = loggingJob

                    job?.invokeOnCompletion {
                        if(job == loggingJob) {
                            job = null
                        }
                    }
                }

            } catch (e: Exception) {
                handleException(e, startId, job)
            }
        }

        return START_STICKY
    }

    override fun onDestroy() {
        Log.d("StatsLoggingNotificationService", "Running onDestroy")

        job?.cancel()
        job = null
        scope.cancel()

        StatsNotificationManager.closeStatsNotificationChannel(applicationContext)
        stopForeground(STOP_FOREGROUND_REMOVE)
        super.onDestroy()
    }

    companion object {
        fun startForegroundService(appContext: Context) {
            val intent = Intent(appContext, StatsLoggingService::class.java)
            ContextCompat.startForegroundService(appContext, intent)
        }

        fun stopService(appContext: Context) {
            val intent = Intent(appContext, StatsLoggingService::class.java)
            appContext.stopService(intent)
        }
    }


    suspend fun handleException(e: Exception, startId: Int, job: Job?) {
        Log.e("StatsLoggingNotificationService", "caught exception", e)

        if(e is CancellationException) {
            throw e

        } else {
            errorHandler.catchAndReturnExceptionCodeSuspending {
                val errorMessage = errorHandler.createErrorMessage(
                    function = "StatsLoggingNotificationService::onStartCommand",
                    error = e,
                    message = "startId = $startId, job = $job"
                )

                errorHandler.logError(errorMessage)
            }

            stopSelf()
        }
    }
}