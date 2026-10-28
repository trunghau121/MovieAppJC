package com.movieappjc.steptracker.service

import android.os.SystemClock
import com.movieappjc.steptracker.domain.models.StepBlock
import com.movieappjc.steptracker.util.StepConstants
import com.movieappjc.steptracker.util.TimeUtils

/**
 * Thread-safe, in-memory state of the step counting session.
 *
 * Today's total = savedToday (already in DB) + inFlightSteps (being written) + pendingSteps (current batch).
 * A batch moves pending -> inFlight in [beginSave], then inFlight -> saved in [commit]
 * or back to pending in [rollback], so a failed DB write never loses steps.
 */
class StepAccumulator {

    class SaveTicket(val block: StepBlock, val sensorValue: Long)

    private var pendingSteps = 0L
    private var inFlightSteps = 0L
    private var savedToday = 0L
    private var lastSensorValue = -1L

    private var batchStartWall = System.currentTimeMillis()
    private var batchStartElapsed = SystemClock.elapsedRealtime()

    private var dayStart = TimeUtils.startOfDayMillis()
    private var dayEnd = TimeUtils.startOfNextDayMillis()

    @Synchronized fun totalToday(): Long = savedToday + inFlightSteps + pendingSteps

    @Synchronized fun isIdle(): Boolean = pendingSteps == 0L && inFlightSteps == 0L

    @Synchronized fun dayStartMillis(): Long = dayStart

    @Synchronized fun dayEndMillis(): Long = dayEnd

    /** True when [now] is outside the day this accumulator currently tracks (midnight passed or clock moved back). */
    @Synchronized fun isDifferentDay(now: Long): Boolean = now !in dayStart..<dayEnd

    @Synchronized fun lastSensorValue(): Long = lastSensorValue

    @Synchronized fun setSensorValue(value: Long) {
        lastSensorValue = value
    }

    @Synchronized fun restartBatchClock(now: Long) {
        if (pendingSteps == 0L) {
            batchStartWall = now
            batchStartElapsed = SystemClock.elapsedRealtime()
        }
    }

    /** Adds [steps] to the current batch. Returns true if the batch should be saved right away. */
    @Synchronized fun add(steps: Long, sensorValue: Long): Boolean {
        val nowElapsed = SystemClock.elapsedRealtime()
        if (pendingSteps == 0L) {
            // First step of a new batch: the batch starts now, not at the previous save
            batchStartWall = System.currentTimeMillis()
            batchStartElapsed = nowElapsed
        }
        pendingSteps += steps
        lastSensorValue = sensorValue
        return pendingSteps >= StepConstants.BATCH_STEP_THRESHOLD ||
                nowElapsed - batchStartElapsed > StepConstants.BATCH_MAX_DURATION_MS
    }

    /**
     * Adds steps recovered after the service was not running; the batch starts at [startAt].
     * The sensor baseline is NOT touched (use [setSensorValue]) so late callers can never move it backwards.
     */
    @Synchronized fun addRecovered(steps: Long, startAt: Long) {
        if (pendingSteps == 0L) {
            batchStartWall = startAt
            batchStartElapsed = SystemClock.elapsedRealtime()
        } else {
            batchStartWall = minOf(batchStartWall, startAt)
        }
        pendingSteps += steps
    }

    /** Moves the pending batch to "in flight" and returns it, or null if nothing is pending. */
    @Synchronized fun beginSave(closeAt: Long): SaveTicket? {
        if (pendingSteps == 0L) return null
        val start = batchStartWall
        val count = pendingSteps
        pendingSteps = 0L
        inFlightSteps += count
        batchStartWall = closeAt
        batchStartElapsed = SystemClock.elapsedRealtime()
        // maxOf: guards against the clock having moved backwards (endTime must never be < startTime)
        return SaveTicket(StepBlock(start, maxOf(closeAt, start), count), lastSensorValue)
    }

    @Synchronized fun commit(block: StepBlock) {
        inFlightSteps = (inFlightSteps - block.count).coerceAtLeast(0L)
        savedToday += block.count
    }

    @Synchronized fun rollback(block: StepBlock) {
        inFlightSteps = (inFlightSteps - block.count).coerceAtLeast(0L)
        pendingSteps += block.count
        batchStartWall = minOf(batchStartWall, block.startTime)
    }

    /** Starts tracking the day that contains [now], with [savedFromDb] steps already stored for it. */
    @Synchronized fun resetDay(now: Long, savedFromDb: Long) {
        dayStart = TimeUtils.startOfDayMillis(now)
        dayEnd = TimeUtils.startOfNextDayMillis(now)
        savedToday = savedFromDb
        if (pendingSteps == 0L) {
            batchStartWall = now
            batchStartElapsed = SystemClock.elapsedRealtime()
        }
    }
}
