package com.movieappjc.steptracker.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.movieappjc.steptracker.util.TimeUtils

class StepStoreData(context: Context) {
    // Single SharedPreferences file for the entire app
    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences("app_store_prefs", Context.MODE_PRIVATE)

    // Last sensor value that is already covered by a block saved in the DB (-1 = no baseline).
    // Only written when a block is persisted, so steps lost to a process kill can be recovered.
    var previousTotalSteps: Long
        get() = prefs.getLong("PREV_STEPS", -1L)
        set(value) = prefs.edit { putLong("PREV_STEPS", value) }

    // Wall-clock end time of the last block saved to the DB
    var lastSaveTime: Long
        get() = prefs.getLong("last_save_time", 0L)
        set(value) = prefs.edit { putLong("last_save_time", value) }

    // Check if the step tracking service is running
    var isTrackingEnabled: Boolean
        get() = prefs.getBoolean("KEY_TRACKING_ENABLED", false)
        set(value) = prefs.edit { putBoolean("KEY_TRACKING_ENABLED", value) }

    // Store the last time data was synced to the server
    var lastSyncTime: Long
        get() = prefs.getLong("last_sync_time", -1L)
        set(value) = prefs.edit { putLong("last_sync_time", value) }

    // Today's total steps (fallback for the widget). Stored together with the day it belongs to,
    // so a stale value from yesterday reads as 0.
    var todaySteps: Long
        get() = if (prefs.getLong("today_steps_day", 0L) == TimeUtils.startOfDayMillis()) {
            prefs.getLong("today_steps", 0L)
        } else {
            0L
        }
        set(value) = prefs.edit {
            putLong("today_steps", value)
            putLong("today_steps_day", TimeUtils.startOfDayMillis())
        }

    // Skip protected app / autostart check dialog
    var skipProtectedAppCheck: Boolean
        get() = prefs.getBoolean("skipProtectedAppCheck", false)
        set(value) = prefs.edit { putBoolean("skipProtectedAppCheck", value) }
}
