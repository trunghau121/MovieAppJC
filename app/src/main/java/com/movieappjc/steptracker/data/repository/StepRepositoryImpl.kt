package com.movieappjc.steptracker.data.repository

import com.movieappjc.steptracker.data.local.LocalStepDao
import com.movieappjc.steptracker.data.local.LocalStepEntity
import com.movieappjc.steptracker.domain.models.StepBlock
import com.movieappjc.steptracker.domain.models.SyncPayload
import com.movieappjc.steptracker.domain.repository.StepRepository
import com.movieappjc.steptracker.util.StepConstants
import com.movieappjc.steptracker.util.TimeUtils
import javax.inject.Inject

class StepRepositoryImpl @Inject constructor(
    private val localStepDao: LocalStepDao
) : StepRepository {

    override suspend fun getLocalSteps(): List<StepBlock> =
        getStepsBetween(TimeUtils.startOfDayMillis(), TimeUtils.startOfNextDayMillis())

    override suspend fun getStepsBetween(start: Long, end: Long): List<StepBlock> =
        localStepDao.getStepsBetween(start, end, StepConstants.MAX_STEPS_PER_BLOCK)
            .map { StepBlock(it.startTime, it.endTime, it.count) }

    override suspend fun getTotalStepsBetween(start: Long, end: Long): Long =
        localStepDao.sumStepsBetween(start, end, StepConstants.MAX_STEPS_PER_BLOCK)

    override suspend fun getTodayTotalSteps(): Long =
        getTotalStepsBetween(TimeUtils.startOfDayMillis(), TimeUtils.startOfNextDayMillis())

    override suspend fun saveLocalSteps(stepBlock: StepBlock) {
        localStepDao.insertStepBlock(
            LocalStepEntity(
                startTime = stepBlock.startTime,
                endTime = stepBlock.endTime,
                count = stepBlock.count
            )
        )
    }

    override suspend fun clearOldLocalSteps(cutoff: Long) {
        localStepDao.deleteOldSteps(cutoff)
    }

    override suspend fun uploadStepsToServer(payloads: List<SyncPayload>): Boolean {
        // Mocking successful server upload.
        // A real implementation should throw IOException on network errors (the worker retries on it)
        // and return false when the server rejects the request.
        return true
    }
}
