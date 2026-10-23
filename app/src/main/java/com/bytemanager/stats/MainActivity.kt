package com.bytemanager.stats

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
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
import androidx.lifecycle.lifecycleScope
import com.bytemanager.stats.data.repository.DataStoreRepository
import com.bytemanager.stats.services.notification.StatsLoggingNotificationService
import com.bytemanager.stats.ui.theme.StatsTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity: ComponentActivity() {
    @Inject
    lateinit var dataStoreRepository: DataStoreRepository
    private lateinit var requestNotificationPermissionLauncher: ActivityResultLauncher<String>

    @SuppressLint("BatteryLife")
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("MainActivity", "running onCreate")
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        // getting permission to post notification
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    Log.d("MainActivity", "Notification permission granted")
                    lifecycleScope.launch { dataStoreRepository.setNotificationPermissionObtained() }
                } else {
                    Log.d("MainActivity", "Notification permission NOT granted")
                    lifecycleScope.launch { dataStoreRepository.setNotificationPermissionNotObtained() }
                }
            }

            requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)

        } else {
            Log.d("MainActivity", "Old API level, no permission request needed")
            lifecycleScope.launch { dataStoreRepository.setNotificationPermissionObtained() }
        }

        // enabling logging if user chooses
        if(runBlocking { dataStoreRepository.isDataLoggingEnabled() }) {
            StatsLoggingNotificationService.startForegroundService(applicationContext)
        }



        // Requesting battery optimization exemption
        val powerManager = getSystemService(PowerManager::class.java)
        val exempt = powerManager.isIgnoringBatteryOptimizations(packageName)

        Log.d("MainActivity", "battery optimization exemption: ${if(exempt) "Exempted" else "Not exempted"}")

        if (!exempt) {
            AlertDialog.Builder(this)
                .setTitle("Battery Optimization")
                .setMessage("For better performance, please disable battery optimization (or set it to 'unrestricted') for this app in settings.")
                .setPositiveButton("Open Settings") { _, _ ->
                    val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
                    startActivity(intent)
                }
                .setNegativeButton("Later") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }


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
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onRestart() {
        super.onRestart()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}