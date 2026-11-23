package com.bytemanager.stats.services.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.bytemanager.stats.MainActivity
import com.bytemanager.stats.R
import com.bytemanager.stats.utils.BatteryInfoUpdatesLogger
import com.bytemanager.stats.utils.BatteryInfoUpdatesNotifier
import com.bytemanager.stats.utils.ErrorHandler
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
    private val FGS_ACTIVITY_CHANNEL_ID = "fgs_activity"
    private var job: Job? = null
    private val mutex = Mutex()
    private lateinit var scope: CoroutineScope

    @Inject
    lateinit var batteryInfoUpdatesLogger: BatteryInfoUpdatesLogger
    @Inject
    lateinit var batteryInfoUpdatesNotifier: BatteryInfoUpdatesNotifier
    @Inject
    lateinit var errorHandler: ErrorHandler

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
        Log.d("StatsLoggingNotificationService", "running onCreate")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createForegroundServiceActivityNotificationChannel()

        val notification = foregroundServiceActivityStatusNotificationBuilder().build()
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
                            launch { batteryInfoUpdatesLogger.startLogging() }
                            launch { batteryInfoUpdatesNotifier.startNotifying() }

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

        batteryInfoUpdatesNotifier.closeBatteryInfoNotificationChannel()
        deleteForegroundServiceActivityNotificationChannel()

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


    private suspend fun handleException(e: Exception, startId: Int, job: Job?) {
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

    private fun createForegroundServiceActivityNotificationChannel() {
        val channel = NotificationChannel(
            FGS_ACTIVITY_CHANNEL_ID,
            "FGS Activity Updates",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Delivers battery info logging foreground service's activity updates, such as if the service is started, or stopped, etc"
        }

        val notificationManager = applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

        Log.d("StatsLoggingService", "'FGS Activity Updates' notification channel created")
    }

    private fun deleteForegroundServiceActivityNotificationChannel() {
        val notificationManager = applicationContext.getSystemService(NotificationManager::class.java)
        notificationManager.deleteNotificationChannel(FGS_ACTIVITY_CHANNEL_ID)

        Log.d("StatsLoggingService", "'FGS Activity Updates' notification channel deleted")

    }

    private fun foregroundServiceActivityStatusNotificationBuilder(): NotificationCompat.Builder {
        val contentTitle = "'Battery Info Logging' is running"
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            Intent(applicationContext, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(applicationContext, FGS_ACTIVITY_CHANNEL_ID)
            .setSmallIcon(R.drawable.status_bar_icon)
            .setContentTitle(contentTitle)
            .setOngoing(true)
            .setGroup("Stats")
            .setContentIntent(pendingIntent)
            .setOnlyAlertOnce(true)

        return notificationBuilder
    }
}