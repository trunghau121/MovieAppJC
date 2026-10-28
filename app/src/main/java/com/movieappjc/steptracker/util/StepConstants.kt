package com.movieappjc.steptracker.util

// Central place for all tunable numbers used by the step tracker
object StepConstants {
    const val DAILY_GOAL = 10_000L
    const val NOTIFICATION_ID = 1

    // Batching of sensor deltas into DB blocks
    const val BATCH_STEP_THRESHOLD = 30L
    const val BATCH_MAX_DURATION_MS = 10_000L

    // Sanity limits
    const val MAX_STEPS_PER_SENSOR_EVENT = 500L   // larger single jumps are treated as sensor glitches
    const val MAX_STEPS_PER_BLOCK = 50_000L       // read-side filter (must be >= MAX_RECOVERY_STEPS)
    const val MAX_RECOVERY_STEPS = 30_000L        // max steps recovered after the service was dead

    // Hardware FIFO batching for the step counter (microseconds)
    const val SENSOR_MAX_REPORT_LATENCY_US = 30_000_000

    // Widget refresh throttle
    const val WIDGET_UPDATE_INTERVAL_MS = 2_000L

    // How often the runtime permission is re-checked from the sensor callback
    const val PERMISSION_RECHECK_INTERVAL_MS = 60_000L

    // Sync
    const val SYNC_SAFETY_MARGIN_MS = 30_000L     // never sync the most recent seconds (blocks may still be in flight)
    const val INITIAL_SYNC_WINDOW_MS = 86_400_000L
    const val LOCAL_RETENTION_MS = 604_800_000L
    const val SYNC_CHUNK_SIZE = 500
    const val SYNC_MAX_ATTEMPTS = 5
}
