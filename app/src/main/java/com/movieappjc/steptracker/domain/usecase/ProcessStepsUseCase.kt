package com.movieappjc.steptracker.domain.usecase

import com.movieappjc.steptracker.domain.models.StepBlock
import com.movieappjc.steptracker.domain.models.SyncPayload

class ProcessStepsUseCase {
    // Merge local and watch data, splitting them into 15-minute intervals
    fun execute(localRecords: List<StepBlock>, watchRecords: List<StepBlock>): List<SyncPayload> {
        if (localRecords.isEmpty() && watchRecords.isEmpty()) return emptyList()

        val slotSize = 900_000L // 15 minutes in milliseconds

        // Group records upfront by their 15-minute slot boundary to avoid O(N * M) list filtering
        val localGrouped = localRecords.groupBy { it.startTime - (it.startTime % slotSize) }
        val watchGrouped = watchRecords.groupBy { it.startTime - (it.startTime % slotSize) }

        val allSlotKeys = (localGrouped.keys + watchGrouped.keys).sorted()
        val payloads = mutableListOf<SyncPayload>()

        for (slotStart in allSlotKeys) {
            val slotEnd = slotStart + slotSize

            val watchSteps = watchGrouped[slotStart]?.sumOf { it.count } ?: 0L
            val localSteps = localGrouped[slotStart]?.sumOf { it.count } ?: 0L

            // Always prioritize watch data over local phone data to avoid double counting
            val finalSteps = if (watchSteps > 0) watchSteps else localSteps

            if (finalSteps > 0) {
                payloads.add(SyncPayload(slotStart.toString(), slotEnd.toString(), finalSteps))
            }
        }
        return payloads
    }
}