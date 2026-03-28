package com.example.stats.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.stats.MainActivity
import com.example.stats.R
import com.example.stats.data.BatteryStateRepository
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class StatsNotificationService: Service() {
    //val statsNotificationManager: StatsNotificationManager = StatsNotificationManager(applicationContext)
    @Inject @ApplicationContext lateinit var appContext: Context
    @Inject lateinit var batteryStateRepository: BatteryStateRepository
    private var scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("PERMISSION DIALOG", "Running onStartCommand")

        val channel = NotificationChannel("Stats", "Battery info", NotificationManager.IMPORTANCE_DEFAULT).apply {
            description = "battery temperature, battery level and battery voltage"
        }

        val notificationManager = appContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

        val intent = Intent(appContext, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            appContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        var temp = batteryStateRepository.batteryStateStateFlow.value.temperature
        var level = batteryStateRepository.batteryStateStateFlow.value.level
        var voltage = batteryStateRepository.batteryStateStateFlow.value.voltage
        var notification = NotificationCompat.Builder(applicationContext, "Stats")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("temp: $temp Celsius | level = $level%")
            .setContentText("voltage: $voltage V")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setSilent(true)
            .build()

        startForeground(1, notification)

        scope.launch {
            batteryStateRepository.batteryStateFlow.collect {state ->
                Log.d("PERMISSION DIALOG", "Collected new batteryState")
                temp = state.temperature
                level = state.level
                voltage = state.voltage

                Log.d("PERMISSION DIALOG", "temp = $temp, level = $level, voltage = $voltage")

                notification = NotificationCompat.Builder(appContext, "Stats")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle("temp: $temp Celsius | level = $level%")
                    .setContentText("voltage: $voltage V")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setOngoing(true)
                    .setSilent(true)
                    .build()

                notificationManager.notify(1, notification)
            }
//            while(isActive) {
//                notificationManager.notify(1, statsNotificationManager.buildStatsNotification())
//                delay(1000)
//            }
        }

        Log.d("PERMISSION DIALOG", "Exiting onStartCommand")
        return START_STICKY
    }

    override fun onDestroy() {
        Log.d("PERMISSION DIALOG", "Running StatsNotificationService.onDestroy")
        scope.cancel()
        stopForeground(STOP_FOREGROUND_REMOVE)
        super.onDestroy()
    }
}