package com.example.stats

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import com.example.stats.data.BatteryStateRepository
import com.example.stats.notification.StatsNotificationManager

class MainActivity : ComponentActivity() {
    private val requestNotificationPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            Log.d("PERMISSION DIALOG", "Permission Granted")
            // Further coding may be needed
        } else {
            Log.d("PERMISSION DIALOG", "Permission Rejected")
            // Further coding may be needed
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        BatteryStateRepository.init(this.applicationContext)
        StatsNotificationManager.init(this.applicationContext, requestNotificationPermissionLauncher)

        setContent {
            StatsApp()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        StatsNotificationManager.destroyNotificationBroadcast()
    }
}