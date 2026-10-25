package com.movieappjc.steptracker.manager

import android.Manifest
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
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.StepsRecord
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.work.*
import com.movieappjc.steptracker.data.local.StepStoreData
import com.movieappjc.steptracker.service.BackgroundStepTrackerService
import com.movieappjc.steptracker.worker.AdvancedSyncWorker
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import androidx.core.net.toUri

class StepTrackingManager(private val activity: ComponentActivity) : DefaultLifecycleObserver {

    private val context: Context = activity.applicationContext
    private val storeData = StepStoreData(context) // Initialize StoreData
    private val healthPermissions = setOf(HealthPermission.getReadPermission(StepsRecord::class))

    // Handle physical activity permission result
    private val systemPermissionLauncher = activity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        if (permissions[Manifest.permission.ACTIVITY_RECOGNITION] == true) {
            checkAndRequestHealthConnectPermission()
        } else {
            handleFailure("Cần quyền vận động để đếm bước!")
        }
    }

    // Handle Health Connect permission result
    private val healthConnectPermissionLauncher = activity.registerForActivityResult(PermissionController.createRequestPermissionResultContract()) { granted ->
        if (!granted.containsAll(healthPermissions)) {
            Toast.makeText(context, "Chỉ chạy đếm bước qua cảm biến máy, không đồng bộ được đồng hồ.", Toast.LENGTH_LONG).show()
        }
        executeStartTracking()
    }

    init {
        activity.lifecycle.addObserver(this)
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        // Resume tracking if it was previously enabled
        if (storeData.isTrackingEnabled) {
            activity.lifecycleScope.launch {
                if (hasSystemPermissions() && isHealthConnectGranted()) executeStartTracking() else stopTracking()
            }
        }
    }

    /**
     * Checks whether the device contains a hardware step counter sensor.
     */
    fun isStepCounterSensorAvailable(): Boolean {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
        return sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null
    }

    /**
     * Shows direct system dialog ("Let app always run in background?") asking user to Allow.
     */
    fun requestIgnoreBatteryOptimizations() {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
        if (powerManager != null && !powerManager.isIgnoringBatteryOptimizations(context.packageName)) {
            try {
                // Direct system popup dialog ("Let app always run in background?")
                val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                    data = "package:${context.packageName}".toUri()
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            } catch (_: Exception) {
                try {
                    // Fallback to settings screen if direct dialog is unsupported on OEM
                    val fallbackIntent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(fallbackIntent)
                } catch (_: Exception) {}
            }
        }
    }

    fun startTracking() {
        if (storeData.isTrackingEnabled) return
        if (!isStepCounterSensorAvailable() && HealthConnectClient.getSdkStatus(context) == HealthConnectClient.SDK_UNAVAILABLE) {
            Toast.makeText(context, "Thiết bị không hỗ trợ cảm biến đếm bước chân.", Toast.LENGTH_LONG).show()
            return
        }
        storeData.isTrackingEnabled = true
        requestIgnoreBatteryOptimizations()
        if (hasSystemPermissions()) checkAndRequestHealthConnectPermission() else requestSystemPermissions()
    }

    fun stopTracking() {
        storeData.isTrackingEnabled = false
        context.stopService(Intent(context, BackgroundStepTrackerService::class.java))
        WorkManager.getInstance(context).cancelUniqueWork("StepSyncWork")
    }

    private fun checkAndRequestHealthConnectPermission() {
        activity.lifecycleScope.launch {
            if (HealthConnectClient.getSdkStatus(context) == HealthConnectClient.SDK_UNAVAILABLE || isHealthConnectGranted()) {
                executeStartTracking()
            } else {
                healthConnectPermissionLauncher.launch(healthPermissions)
            }
        }
    }

    private suspend fun isHealthConnectGranted(): Boolean = try {
        HealthConnectClient.getOrCreate(context).permissionController.getGrantedPermissions().containsAll(healthPermissions)
    } catch (_: Exception) { false }

    private fun hasSystemPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED
        } else true
    }

    private fun requestSystemPermissions() {
        val list = mutableListOf<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) list.add(Manifest.permission.ACTIVITY_RECOGNITION)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) list.add(Manifest.permission.POST_NOTIFICATIONS)

        if (list.isNotEmpty()) {
            systemPermissionLauncher.launch(list.toTypedArray())
        } else {
            checkAndRequestHealthConnectPermission()
        }
    }

    private fun executeStartTracking() {
        val serviceIntent = Intent(context, BackgroundStepTrackerService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) context.startForegroundService(serviceIntent) else context.startService(serviceIntent)

        // Schedule periodic sync worker every 15 minutes
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "StepSyncWork",
            ExistingPeriodicWorkPolicy.KEEP,
            PeriodicWorkRequestBuilder<AdvancedSyncWorker>(15, TimeUnit.MINUTES)
                .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
                .build()
        )
    }

    private fun handleFailure(msg: String) {
        storeData.isTrackingEnabled = false
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
    }
}