package com.movieappjc.steptracker.domain.repository

import com.movieappjc.steptracker.domain.models.StepBlock
import com.movieappjc.steptracker.domain.models.SyncPayload

interface StepRepository {
    var currentBatchSteps: Long
    var startTimeOfBatch: Long

    suspend fun getLocalSteps(): List<StepBlock>
    suspend fun getStepsBetween(start: Long, end: Long): List<StepBlock>
    suspend fun saveLocalSteps(stepBlock: StepBlock)
    suspend fun clearOldLocalSteps(cutoff: Long)
    suspend fun uploadStepsToServer(payloads: List<SyncPayload>): Boolean
}
