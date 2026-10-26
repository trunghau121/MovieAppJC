package com.movieappjc.steptracker.data.repository

import com.movieappjc.steptracker.data.local.LocalStepDao
import com.movieappjc.steptracker.data.local.LocalStepEntity
import com.movieappjc.steptracker.domain.models.StepBlock
import com.movieappjc.steptracker.domain.models.SyncPayload
import com.movieappjc.steptracker.domain.repository.StepRepository
import javax.inject.Inject

class StepRepositoryImpl @Inject constructor(
    private val localStepDao: LocalStepDao
) : StepRepository {

    override suspend fun getLocalSteps(start: Long, end: Long): List<StepBlock> {
        return localStepDao.getStepsBetween(start, end).map { StepBlock(it.startTime, it.endTime, it.count) }
    }

    override suspend fun saveLocalSteps(stepBlock: StepBlock) {
        localStepDao.insertStepBlock(LocalStepEntity(startTime = stepBlock.startTime, endTime = stepBlock.endTime, count = stepBlock.count))
    }

    override suspend fun clearOldLocalSteps(cutoff: Long) {
        localStepDao.deleteOldSteps(cutoff)
    }

    override suspend fun uploadStepsToServer(payloads: List<SyncPayload>): Boolean {
        // Mocking successful server upload
        return true
    }
}
