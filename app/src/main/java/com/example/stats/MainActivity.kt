package com.example.stats

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import com.example.stats.data.BatteryStateRepository
import com.example.stats.notification.StatsNotificationManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var requestNotificationPermissionLauncher: ActivityResultLauncher<String>
    @Inject
    lateinit var statsNotificationManager: StatsNotificationManager


    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("PERMISSION DIALOG", "running onCreate")
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        BatteryStateRepository.init(this.applicationContext)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                Log.d("PERMISSION DIALOG", "User responded for permission request")
                if (isGranted) {
                    Log.d("PERMISSION DIALOG", "Permission granted")
                    statsNotificationManager.createStatsNotificationChannel()
                    statsNotificationManager.startStatsNotificationBroadcast()
                } else {
                    Log.d("PERMISSION DIALOG", "Permission NOT granted")
                }
            }

            requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            Log.d("PERMISSION DIALOG", "Old API, no permission request needed")
            statsNotificationManager.createStatsNotificationChannel()
            statsNotificationManager.startStatsNotificationBroadcast()
        }


        setContent {
            StatsApp()
        }
        Log.d("PERMISSION DIALOG", "exiting onCreate")
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onResume() {
        Log.d("PERMISSION DIALOG", "running onResume")
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        Log.d("PERMISSION DIALOG", "running onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("PERMISSION DIALOG", "running onStop")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d("PERMISSION DIALOG", "running onRestart")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("PERMISSION DIALOG", "running onDestroy")
        statsNotificationManager.destroyStatsNotificationBroadcast()
    }
}