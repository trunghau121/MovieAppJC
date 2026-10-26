package com.movieappjc.steptracker.domain.usecase

import com.movieappjc.steptracker.domain.models.StepBlock
import com.movieappjc.steptracker.domain.models.SyncPayload

class ProcessStepsUseCase {
    // Splits local step data into 15-minute intervals for server sync
    fun execute(localRecords: List<StepBlock>): List<SyncPayload> {
        if (localRecords.isEmpty()) return emptyList()

        val slotSize = 900_000L // 15 minutes in milliseconds

        // Group records upfront by their 15-minute slot boundary
        val localGrouped = localRecords.groupBy { it.startTime - (it.startTime % slotSize) }

        val payloads = mutableListOf<SyncPayload>()

        for ((slotStart, blocks) in localGrouped.entries.sortedBy { it.key }) {
            val slotEnd = slotStart + slotSize
            val totalSteps = blocks.sumOf { it.count }

            if (totalSteps > 0) {
                payloads.add(SyncPayload(slotStart.toString(), slotEnd.toString(), totalSteps))
            }
        }
        return payloads
    }
}
