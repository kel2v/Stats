package com.example.stats.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.stats.MainActivity
import com.example.stats.R
import com.example.stats.data.BatteryState
import com.example.stats.data.BatteryStateRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

object StatsNotificationManager {
    lateinit var appContext: Context

    fun createNotificationChannel(context: Context) {
        val channelId = "Stats"
        val channelName = "Battery info"
        val channelDescription = "battery parameters updates"
        val importance = NotificationManager.IMPORTANCE_DEFAULT

        val channel = NotificationChannel(channelId, channelName, importance).apply {
            description = channelDescription
        }

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    fun showLocalNotification(context: Context, batteryState: BatteryState) {
        val channelId = "Stats"

        // Optional: tap notification to open MainActivity
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("temp: ${batteryState.temperature/10f} Celsius | level = ${batteryState.level}%")
            .setContentText("voltage: ${batteryState.voltage/1000f} V")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()

        with(NotificationManagerCompat.from(context)) {
            if(ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                notify(1001, notification)
                Log.d("NOTIFICATION", "temp: ${batteryState.temperature/10f} Celsius | level = ${batteryState.level}%\nvoltage: ${batteryState.voltage/1000f} V")
            }
        }
    }

    fun init(context: Context, requestNotificationPermissionLauncher: ActivityResultLauncher<String>) {
        appContext = context.applicationContext

        createNotificationChannel(appContext)

        Log.d("PERMISSION DIALOG", "checking notification permission")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(appContext, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                Log.d("PERMISSION DIALOG", "device is newer, Permission already granted")
                startNotificationBroadcast()
            } else {
                Log.d("PERMISSION DIALOG", "device is newer, Permission not yet granted")
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)

                if (ContextCompat.checkSelfPermission(appContext, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                    Log.d("PERMISSION DIALOG", "device is newer, Permission now granted")
                    startNotificationBroadcast()
                } else {
                    Log.d("PERMISSION DIALOG", "device is newer, Permission denied")
                }
            }
        } else {
            Log.d("PERMISSION DIALOG", "Old device, no permission needed")
            startNotificationBroadcast()
        }
    }

    val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    lateinit var callbackCoroutine: Job

    fun startNotificationBroadcast() {
        callbackCoroutine = scope.launch {
            BatteryStateRepository.batteryStateFlow.collect { state ->
                showLocalNotification(appContext, state)
            }
        }
    }

    fun destroyNotificationBroadcast() {
        callbackCoroutine.cancel()
    }
}