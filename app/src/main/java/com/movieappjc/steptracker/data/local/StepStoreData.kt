package com.movieappjc.steptracker.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class StepStoreData(context: Context) {
    // Single SharedPreferences file for the entire app
    private val prefs: SharedPreferences = context.getSharedPreferences("app_store_prefs", Context.MODE_PRIVATE)

    // Store previous total steps from the sensor
    var previousTotalSteps: Long
        get() = prefs.getLong("PREV_STEPS", -1L)
        set(value) = prefs.edit { putLong("PREV_STEPS", value) }

    // Check if the step tracking service is running
    var isTrackingEnabled: Boolean
        get() = prefs.getBoolean("KEY_TRACKING_ENABLED", false)
        set(value) = prefs.edit { putBoolean("KEY_TRACKING_ENABLED", value) }

    // Store the last time data was synced to the server
    var lastSyncTime: Long
        get() = prefs.getLong("last_sync_time", -1L)
        set(value) = prefs.edit { putLong("last_sync_time", value) }

    // Store today's total steps for real-time widget updates
    var todaySteps: Long
        get() = prefs.getLong("today_steps", 0L)
        set(value) = prefs.edit(commit = true) { putLong("today_steps", value) }
}