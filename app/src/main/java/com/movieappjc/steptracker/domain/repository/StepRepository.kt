package com.movieappjc.steptracker.domain.repository

import com.movieappjc.steptracker.domain.models.StepBlock
import com.movieappjc.steptracker.domain.models.SyncPayload

interface StepRepository {
    /** All blocks that ended today. */
    suspend fun getLocalSteps(): List<StepBlock>

    /** Blocks whose endTime is in [start, end). Each block belongs to exactly one window. */
    suspend fun getStepsBetween(start: Long, end: Long): List<StepBlock>

    /** Sum of steps of blocks whose endTime is in [start, end). */
    suspend fun getTotalStepsBetween(start: Long, end: Long): Long

    /** Sum of steps of all blocks that ended today. */
    suspend fun getTodayTotalSteps(): Long

    suspend fun saveLocalSteps(stepBlock: StepBlock)
    suspend fun clearOldLocalSteps(cutoff: Long)
    suspend fun uploadStepsToServer(payloads: List<SyncPayload>): Boolean
}
