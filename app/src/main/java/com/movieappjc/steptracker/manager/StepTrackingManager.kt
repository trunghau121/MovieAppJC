package com.movieappjc.steptracker.manager

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.work.*
import com.movieappjc.steptracker.data.local.StepStoreData
import com.movieappjc.steptracker.service.BackgroundStepTrackerService
import com.movieappjc.steptracker.util.AutoStartHelper
import com.movieappjc.steptracker.worker.AdvancedSyncWorker
import kotlinx.coroutines.launch

class StepTrackingManager(private val activity: ComponentActivity) : DefaultLifecycleObserver {

    private val context: Context = activity.applicationContext
    private val storeData = StepStoreData(context)

    // Launcher for Physical Activity permission
    private val activityRecognitionLauncher = activity.registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            checkAndRequestNotificationPermission()
        } else {
            handleFailure("Physical Activity permission is required to track steps!")
        }
    }

    // Launcher for Notification permission
    private val notificationPermissionLauncher = activity.registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ ->
        checkAndRequestBatteryOptimization()
    }

    init {
        activity.lifecycle.addObserver(this)
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        // Trigger sync when app opens
        triggerAppOpenSync()

        // Resume tracking if it was previously enabled
        if (storeData.isTrackingEnabled) {
            activity.lifecycleScope.launch {
                if (hasSystemPermissions()) executeStartTracking() else stopTracking()
            }
        }
    }

    /**
     * Triggers a one-time step data sync to the server when the app is opened.
     */
    fun triggerAppOpenSync() {
        val syncRequest = OneTimeWorkRequestBuilder<AdvancedSyncWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "StepAppOpenSyncWork",
            ExistingWorkPolicy.REPLACE,
            syncRequest
        )
    }

    /**
     * Checks whether the device contains a hardware step counter sensor.
     */
    fun isStepCounterSensorAvailable(): Boolean {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
        return sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null
    }

    fun startTracking() {
        if (storeData.isTrackingEnabled) return
        if (!isStepCounterSensorAvailable()) {
            Toast.makeText(context, "Thiết bị không hỗ trợ cảm biến đếm bước chân.", Toast.LENGTH_LONG).show()
            return
        }
        storeData.isTrackingEnabled = true
        checkAndRequestActivityRecognition()
    }

    private fun checkAndRequestActivityRecognition() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED
        ) {
            AlertDialog.Builder(activity)
                .setTitle("Physical Activity Permission Required")
                .setMessage("To accurately track your daily steps in real-time, this app requires physical activity recognition permission. Tap OK to grant this permission.")
                .setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                    activityRecognitionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                    handleFailure("Physical Activity permission is required to track steps!")
                }
                .setCancelable(false)
                .show()
        } else {
            checkAndRequestNotificationPermission()
        }
    }

    private fun checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            AlertDialog.Builder(activity)
                .setTitle("Notification Permission Required")
                .setMessage("To display your live step progress in real-time and maintain background tracking, please allow notifications.")
                .setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                    checkAndRequestBatteryOptimization()
                }
                .setCancelable(false)
                .show()
        } else {
            checkAndRequestBatteryOptimization()
        }
    }

    private fun checkAndRequestBatteryOptimization() {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
        if (powerManager != null && !powerManager.isIgnoringBatteryOptimizations(context.packageName)) {
            AlertDialog.Builder(activity)
                .setTitle("Background Battery Optimization")
                .setMessage("To ensure step tracking continues reliably when the app is in the background or your screen is turned off, please allow the app to ignore battery optimizations.")
                .setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                    requestIgnoreBatteryOptimizations()
                    executeStartTracking()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                    executeStartTracking()
                }
                .setCancelable(false)
                .show()
        } else {
            executeStartTracking()
        }
    }

    private fun requestIgnoreBatteryOptimizations() {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
        if (powerManager != null && !powerManager.isIgnoringBatteryOptimizations(context.packageName)) {
            try {
                val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                    data = "package:${context.packageName}".toUri()
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            } catch (_: Exception) {
                try {
                    val fallbackIntent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(fallbackIntent)
                } catch (_: Exception) {}
            }
        }
    }

    fun stopTracking() {
        storeData.isTrackingEnabled = false
        context.stopService(Intent(context, BackgroundStepTrackerService::class.java))
        WorkManager.getInstance(context).cancelUniqueWork("StepSyncWork")
        WorkManager.getInstance(context).cancelUniqueWork("StepAppOpenSyncWork")
    }

    private fun hasSystemPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED
        } else true
    }

    private fun executeStartTracking() {
        val serviceIntent = Intent(context, BackgroundStepTrackerService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) context.startForegroundService(serviceIntent) else context.startService(serviceIntent)

        AutoStartHelper.startPowerSaverIntent(activity)

        // Cancel legacy 15-minute periodic sync work if previously enqueued
        WorkManager.getInstance(context).cancelUniqueWork("StepSyncWork")
    }

    private fun handleFailure(msg: String) {
        storeData.isTrackingEnabled = false
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
    }
}
