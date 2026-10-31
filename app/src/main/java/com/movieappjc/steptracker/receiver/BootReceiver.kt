package com.movieappjc.steptracker.receiver

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import com.movieappjc.steptracker.data.local.StepStoreData
import com.movieappjc.steptracker.service.BackgroundStepTrackerService

// Restarts the Step Tracker Service after a reboot and after the app was updated
// (an update kills the running service).
// Manifest: add <action android:name="android.intent.action.MY_PACKAGE_REPLACED" /> to this receiver's intent-filter.
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action != Intent.ACTION_BOOT_COMPLETED && action != Intent.ACTION_MY_PACKAGE_REPLACED) return

        val storeData = StepStoreData(context)
        if (!storeData.isTrackingEnabled) return

        val hasPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACTIVITY_RECOGNITION) ==
                PackageManager.PERMISSION_GRANTED
        } else {
            true
        }

        if (!hasPermission) {
            storeData.isTrackingEnabled = false
            return
        }

        try {
            ContextCompat.startForegroundService(
                context,
                Intent(context, BackgroundStepTrackerService::class.java)
            )
        } catch (e: Exception) {
            Log.e("BootReceiver", "Unable to restart step tracking service", e)
        }
    }
}
