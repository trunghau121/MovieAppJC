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

    // Store Health Connect token for fetching incremental changes
    var hcChangesToken: String?
        get() = prefs.getString("hc_changes_token", null)
        set(value) = prefs.edit { putString("hc_changes_token", value) }

    // Store the last time data was synced to the server
    var lastSyncTime: Long
        get() = prefs.getLong("last_sync_time", -1L)
        set(value) = prefs.edit { putLong("last_sync_time", value) }
}