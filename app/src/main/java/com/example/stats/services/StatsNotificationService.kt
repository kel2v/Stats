package com.example.stats.services

import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.example.stats.data.BatteryStateRepository
import com.example.stats.notification.StatsNotificationManager
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
    @Inject lateinit var statsNotificationManager: StatsNotificationManager
    @Inject @ApplicationContext lateinit var appContext: Context
    @Inject lateinit var batteryStateRepository: BatteryStateRepository
    private var scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("PERMISSION DIALOG", "Running onStartCommand")

//        val channel = NotificationChannel("Stats", "Battery info", NotificationManager.IMPORTANCE_DEFAULT).apply {
//            description = "battery temperature, battery level and battery voltage"
//        }
//
//        val notificationManager = appContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
//        notificationManager.createNotificationChannel(channel)
//
//        val intent = Intent(appContext, MainActivity::class.java)
//        val pendingIntent = PendingIntent.getActivity(
//            appContext,
//            0,
//            intent,
//            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//        )
//
//        var temp = batteryStateRepository.batteryStateStateFlow.value.temperature
//        var level = batteryStateRepository.batteryStateStateFlow.value.level
//        var voltage = batteryStateRepository.batteryStateStateFlow.value.voltage
//        var notification = NotificationCompat.Builder(applicationContext, "Stats")
//            .setSmallIcon(R.drawable.ic_launcher_foreground)
//            .setContentTitle("temp: $temp Celsius | level = $level%")
//            .setContentText("voltage: $voltage V")
//            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//            .setContentIntent(pendingIntent)
//            .setOngoing(true)
//            .setSilent(true)
//            .build()
//
//        startForeground(1, notification)
//
//        scope.launch {
//            batteryStateRepository.batteryStateFlow.collect {state ->
//                Log.d("PERMISSION DIALOG", "Collected new batteryState")
//                temp = state.temperature
//                level = state.level
//                voltage = state.voltage
//
//                Log.d("PERMISSION DIALOG", "temp = $temp, level = $level, voltage = $voltage")
//
//                notification = NotificationCompat.Builder(appContext, "Stats")
//                    .setSmallIcon(R.drawable.ic_launcher_foreground)
//                    .setContentTitle("temp: $temp Celsius | level = $level%")
//                    .setContentText("voltage: $voltage V")
//                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                    .setContentIntent(pendingIntent)
//                    .setOngoing(true)
//                    .setSilent(true)
//                    .build()
//
//                notificationManager.notify(1, notification)
//            }
//            while(isActive) {
//                notificationManager.notify(1, statsNotificationManager.buildStatsNotification())
//                delay(1000)
//            }
//        }

        statsNotificationManager.createStatsNotificationChannel()
        startForeground(1, statsNotificationManager.buildStatsNotification(batteryStateRepository.batteryStateStateFlow.value))

//        scope.launch {
//            val notificationManager = appContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
//            while(isActive) {
//                val notification = statsNotificationManager.buildStatsNotification()
//                notificationManager.notify(1, notification)
//                Log.d("PERMISSION DIALOG", "${notification.extras.getString(Notification.EXTRA_TITLE, "NA")}\n${notification.extras.getString(Notification.EXTRA_TEXT, "NA")}")
//                delay(1000)
//            }
//        }

        scope.launch {
            batteryStateRepository.batteryStateFlow.collect { batteryState ->
                val notificationManager = appContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                val notification = statsNotificationManager.buildStatsNotification(batteryState)
                notificationManager.notify(1, notification)
                Log.d("PERMISSION DIALOG", "${notification.extras.getString(Notification.EXTRA_TITLE, "NA")}\n${notification.extras.getString(Notification.EXTRA_TEXT, "NA")}")
            }
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