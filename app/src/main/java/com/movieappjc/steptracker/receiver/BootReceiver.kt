package com.movieappjc.steptracker.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.movieappjc.steptracker.data.local.StepStoreData
import com.movieappjc.steptracker.service.BackgroundStepTrackerService

// Automatically restart the Step Tracker Service when the device reboots
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val storeData = StepStoreData(context)
            if (storeData.isTrackingEnabled) {
                val serviceIntent = Intent(context, BackgroundStepTrackerService::class.java)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(serviceIntent)
                } else {
                    context.startService(serviceIntent)
                }
            }
        }
    }
}