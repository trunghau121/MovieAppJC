package com.movieappjc.steptracker.manager

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.movieappjc.R
import com.movieappjc.steptracker.data.local.StepStoreData
import com.movieappjc.steptracker.service.BackgroundStepTrackerService
import com.movieappjc.steptracker.ui.StepPermissionDialogState
import com.movieappjc.steptracker.util.AutoStartHelper
import com.movieappjc.steptracker.worker.AdvancedSyncWorker

/**
 * Must be created before the activity is STARTED (e.g. as a property or in onCreate),
 * because it registers ActivityResult launchers.
 */
@SuppressLint("LogNotTimber")
class StepTrackingManager(
    private val activity: ComponentActivity,
    private val storeData: StepStoreData = StepStoreData(activity.applicationContext)
) : DefaultLifecycleObserver {

    private val context: Context = activity.applicationContext

    var dialogState by mutableStateOf<StepPermissionDialogState?>(null)
        private set

    // True while the permission / battery dialogs of startTracking() are being shown
    private var isStartFlowRunning = false
    private var isWaitingForAppSettings = false

    // Launcher for Physical Activity permission
    private val activityRecognitionLauncher = activity.registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            checkAndRequestNotificationPermission()
        } else {
            handleFailure(R.string.step_permission_activity_required)
        }
    }

    // Launcher for Notification permission
    private val notificationPermissionLauncher = activity.registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            finishStartFlow()
        } else {
            handleFailure(R.string.step_permission_notification_required)
        }
    }

    init {
        activity.lifecycle.addObserver(this)
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        // Trigger sync when app opens
        triggerAppOpenSync()

        // Resume tracking if it was previously enabled
        if (storeData.isTrackingEnabled && !isStartFlowRunning) {
            if (hasSystemPermissions()) {
                executeStartTracking()
            } else {
                isStartFlowRunning = false
                storeData.isTrackingEnabled = false
                startTracking()
            }
        }
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        if (isWaitingForAppSettings) {
            isWaitingForAppSettings = false
            if (!hasSystemPermissions()) {
                isStartFlowRunning = false
                startTracking()
            } else {
                finishStartFlow()
            }
        }
    }

    /**
     * Triggers a one-time step data sync to the server when the app is opened.
     * KEEP: reopening the app must not cancel a sync that is already running.
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
            ExistingWorkPolicy.KEEP,
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

    /**
     * Starts the permission flow. Tracking is only marked as enabled (and the service started)
     * once the flow has finished, see [finishStartFlow].
     */
    fun startTracking() {
        if (storeData.isTrackingEnabled || isStartFlowRunning) return
        if (!isStepCounterSensorAvailable()) {
            Toast.makeText(context, R.string.step_sensor_unavailable, Toast.LENGTH_LONG).show()
            return
        }
        isStartFlowRunning = true
        checkAndRequestActivityRecognition()
    }

    private fun checkAndRequestActivityRecognition() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val showRationale = ActivityCompat.shouldShowRequestPermissionRationale(
                activity, Manifest.permission.ACTIVITY_RECOGNITION
            )
            if (!showRationale) {
                showSettingsDialog(
                    title = R.string.step_permission_activity_title,
                    message = R.string.step_permission_settings_message,
                    onCancel = { handleFailure(R.string.step_permission_activity_required) }
                )
            } else {
                showDialog(
                    title = R.string.step_permission_activity_title,
                    message = R.string.step_permission_activity_message,
                    onOk = { activityRecognitionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION) },
                    onCancel = { handleFailure(R.string.step_permission_activity_required) }
                )
            }
        } else {
            checkAndRequestNotificationPermission()
        }
    }

    private fun checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val showRationale = ActivityCompat.shouldShowRequestPermissionRationale(
                activity, Manifest.permission.POST_NOTIFICATIONS
            )
            if (!showRationale) {
                showSettingsDialog(
                    title = R.string.step_permission_notification_title,
                    message = R.string.step_permission_notification_settings_message,
                    onCancel = { handleFailure(R.string.step_permission_notification_required) }
                )
            } else {
                showDialog(
                    title = R.string.step_permission_notification_title,
                    message = R.string.step_permission_notification_message,
                    onOk = { notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS) },
                    onCancel = { handleFailure(R.string.step_permission_notification_required) }
                )
            }
        } else {
            finishStartFlow()
        }
    }

    // Last step of the start flow: enable tracking, start the service, offer the OEM autostart screen once
    private fun finishStartFlow() {
        isStartFlowRunning = false
        if (!hasSystemPermissions()) {
            handleFailure(R.string.step_permission_activity_required)
            return
        }
        storeData.isTrackingEnabled = true
        executeStartTracking()
        AutoStartHelper.startPowerSaverIntent(activity)
    }

    private fun hasSystemPermissions(): Boolean {
        val hasActivityPermission = Build.VERSION.SDK_INT < Build.VERSION_CODES.Q
                || ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACTIVITY_RECOGNITION
        ) == PackageManager.PERMISSION_GRANTED
        val hasNotificationPermission = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
                || ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
        return hasActivityPermission && hasNotificationPermission
    }

    private fun executeStartTracking() {
        if (!hasSystemPermissions()) {
            storeData.isTrackingEnabled = false
            return
        }
        try {
            ContextCompat.startForegroundService(
                context,
                Intent(context, BackgroundStepTrackerService::class.java)
            )
        } catch (e: Exception) {
            Log.e(TAG, "Unable to start the step tracking service", e)
            return
        }

        // Cancel legacy 15-minute periodic sync work if previously enqueued
        WorkManager.getInstance(context).cancelUniqueWork("StepSyncWork")
    }

    private fun showDialog(
        @StringRes title: Int,
        @StringRes message: Int,
        onOk: () -> Unit,
        onCancel: () -> Unit
    ) {
        if (activity.isFinishing || activity.isDestroyed) {
            onCancel()
            return
        }
        dialogState = StepPermissionDialogState(
            titleRes = title,
            messageRes = message,
            buttonTextRes = R.string.step_permission_allow_access,
            onConfirm = {
                dialogState = null
                onOk()
            },
            onDismiss = {
                dialogState = null
                onCancel()
            }
        )
    }

    private fun showSettingsDialog(
        @StringRes title: Int,
        @StringRes message: Int,
        onCancel: () -> Unit
    ) {
        if (activity.isFinishing || activity.isDestroyed) {
            onCancel()
            return
        }
        dialogState = StepPermissionDialogState(
            titleRes = title,
            messageRes = message,
            buttonTextRes = R.string.step_permission_open_settings,
            onConfirm = {
                dialogState = null
                openAppSettings()
                isStartFlowRunning = false
            },
            onDismiss = {
                dialogState = null
                onCancel()
            }
        )
    }

    private fun openAppSettings() {
        try {
            isWaitingForAppSettings = true
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", activity.packageName, null)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            activity.startActivity(intent)
        } catch (e: Exception) {
            isWaitingForAppSettings = false
            Log.e(TAG, "Unable to open app settings", e)
        }
    }

    private fun handleFailure(@StringRes messageRes: Int) {
        isStartFlowRunning = false
        storeData.isTrackingEnabled = false
        Toast.makeText(context, messageRes, Toast.LENGTH_LONG).show()
    }

    private companion object {
        const val TAG = "StepTrackingManager"
    }
}
