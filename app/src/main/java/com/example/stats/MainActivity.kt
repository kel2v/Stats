package com.example.stats

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.stats.notification.createNotificationChannel
import com.example.stats.ui.pages.BatteryStateRepository

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

    fun checkNotificationPermission() {
        Log.d("PERMISSION DIALOG", "checking notification permission")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                Log.d("PERMISSION DIALOG", "device is newer, Permission already granted")
            } else {
                Log.d("PERMISSION DIALOG", "device is newer, Permission not yet granted")
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            Log.d("PERMISSION DIALOG", "Old device, no permission needed")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        BatteryStateRepository.init(this.applicationContext)

        checkNotificationPermission()
        createNotificationChannel(this.applicationContext)

        setContent {
            StatsApp()
        }
    }
}