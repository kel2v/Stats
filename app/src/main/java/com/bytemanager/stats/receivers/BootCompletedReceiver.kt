package com.bytemanager.stats.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.bytemanager.stats.data.repository.DataStoreRepository
import com.bytemanager.stats.services.notification.StatsLoggingService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class BootCompletedReceiver: BroadcastReceiver() {
    @Inject
    lateinit var dataStoreRepository: DataStoreRepository

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("BootCompletedReceiver", "received intent: $intent")

        if(intent.action == Intent.ACTION_LOCKED_BOOT_COMPLETED) {
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.Default).launch {
                val settings = dataStoreRepository.flowOfSettings.first()

                Log.d("BootCompletedReceiver", "isAutostartLoggingServiceEnabled = ${settings.isAutostartLoggingServiceEnabled}")
                Log.d("BootCompletedReceiver", "isDataLoggingEnabled = ${settings.isDataLoggingEnabled}")

                if(settings.isDataLoggingEnabled && settings.isAutostartLoggingServiceEnabled) {
                    val serviceIntent = Intent(context, StatsLoggingService::class.java)
                    context.startForegroundService(serviceIntent)
                }

                pendingResult.finish()
            }
        }
    }
}