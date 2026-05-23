package com.example.stats

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.stats.services.StatsLoggingNotificationService
import com.example.stats.ui.theme.StatsTheme
import com.example.stats.worker.SyncWorker
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class MainActivity: ComponentActivity() {
    private lateinit var requestNotificationPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("DEBUGGING LOGS", "running onCreate")
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                Log.d("DEBUGGING LOGS", "User responded for permission request")
                if (isGranted) {
                    Log.d("DEBUGGING LOGS", "Permission granted")
                    if(!StatsLoggingNotificationService.isRunning.value) {
                        val intent = Intent(this, StatsLoggingNotificationService::class.java)
                        ContextCompat.startForegroundService(this, intent)
                    }
                } else {
                    Log.d("DEBUGGING LOGS", "Permission NOT granted")
                }
            }
            requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            Log.d("DEBUGGING LOGS", "Old API, no permission request needed")
            if(!StatsLoggingNotificationService.isRunning.value) {
                val intent = Intent(this, StatsLoggingNotificationService::class.java)
                ContextCompat.startForegroundService(this, intent)
            }
        }

        val periodicRequest = PeriodicWorkRequestBuilder<SyncWorker>(repeatInterval = 15, repeatIntervalTimeUnit = TimeUnit.MINUTES)
            .addTag("Sync")
            .build()

        val workManager = WorkManager.getInstance(this.applicationContext)
        workManager.enqueueUniquePeriodicWork(
            "battery temp logging",
            ExistingPeriodicWorkPolicy.KEEP,
            periodicRequest
        )

        setContent {
            StatsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    StatsApp()
                }
            }
        }
        Log.d("DEBUGGING LOGS", "exiting onCreate")
    }

    override fun onResume() {
        Log.d("DEBUGGING LOGS", "running onResume")
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        Log.d("DEBUGGING LOGS", "running onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("DEBUGGING LOGS", "running onStop")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d("DEBUGGING LOGS", "running onRestart")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("DEBUGGING LOGS", "running onDestroy")
    }
}