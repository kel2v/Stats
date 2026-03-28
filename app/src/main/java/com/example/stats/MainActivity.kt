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
import androidx.core.content.ContextCompat
import com.example.stats.services.StatsNotificationService
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity: ComponentActivity() {
    private lateinit var requestNotificationPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("PERMISSION DIALOG", "running onCreate")
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                Log.d("PERMISSION DIALOG", "User responded for permission request")
                if (isGranted) {
                    Log.d("PERMISSION DIALOG", "Permission granted")
                    val intent = Intent(this, StatsNotificationService::class.java)
                    ContextCompat.startForegroundService(this, intent)
                } else {
                    Log.d("PERMISSION DIALOG", "Permission NOT granted")
                }
            }
            requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            Log.d("PERMISSION DIALOG", "Old API, no permission request needed")
            val intent = Intent(this, StatsNotificationService::class.java)
            ContextCompat.startForegroundService(this, intent)
        }


        setContent {
            StatsApp()
        }
        Log.d("PERMISSION DIALOG", "exiting onCreate")
    }

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
    }
}