package com.movieappjc.steptracker.domain.usecase

import com.movieappjc.steptracker.domain.models.StepBlock
import com.movieappjc.steptracker.domain.models.SyncPayload
import java.util.Calendar

class ProcessStepsUseCase {
    // Merge local and watch data, splitting them into 15-minute intervals
    fun execute(localRecords: List<StepBlock>, watchRecords: List<StepBlock>): List<SyncPayload> {
        val allTimes = localRecords.map { it.startTime } + watchRecords.map { it.startTime }
        if (allTimes.isEmpty()) return emptyList()

        // Truncate the earliest time to the nearest 15-minute boundary (0, 15, 30, 45)
        val minTime = allTimes.minOrNull()!!
        val calendar = Calendar.getInstance().apply { timeInMillis = minTime }

        val minutes = calendar.get(Calendar.MINUTE)
        val roundedMinutes = minutes - (minutes % 15)

        calendar.set(Calendar.MINUTE, roundedMinutes)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        val globalStart = calendar.timeInMillis
        val globalEnd = (localRecords.map { it.endTime } + watchRecords.map { it.endTime }).maxOrNull()!!

        val payloads = mutableListOf<SyncPayload>()
        var currentSlotStart = globalStart

        // Loop through data in 15-minute chunks (900,000 ms)
        while (currentSlotStart < globalEnd) {
            val currentSlotEnd = currentSlotStart + 900_000L

            val localSteps = localRecords.filter { it.startTime in currentSlotStart..<currentSlotEnd }.sumOf { it.count }
            val watchSteps = watchRecords.filter { it.startTime in currentSlotStart..<currentSlotEnd }.sumOf { it.count }

            // Always prioritize watch data over local phone data to avoid double counting
            val finalSteps = if (watchSteps > 0) watchSteps else localSteps

            if (finalSteps > 0) {
                payloads.add(SyncPayload(currentSlotStart.toString(), currentSlotEnd.toString(), finalSteps))
            }
            currentSlotStart = currentSlotEnd
        }
        return payloads
    }
}