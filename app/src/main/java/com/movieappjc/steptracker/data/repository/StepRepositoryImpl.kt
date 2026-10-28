package com.movieappjc.steptracker.data.repository

import com.movieappjc.steptracker.data.local.LocalStepDao
import com.movieappjc.steptracker.data.local.LocalStepEntity
import com.movieappjc.steptracker.domain.models.StepBlock
import com.movieappjc.steptracker.domain.models.SyncPayload
import com.movieappjc.steptracker.domain.repository.StepRepository
import java.util.Calendar
import javax.inject.Inject

class StepRepositoryImpl @Inject constructor(
    private val localStepDao: LocalStepDao
) : StepRepository {

    @Volatile
    override var currentBatchSteps: Long = 0L

    @Volatile
    override var startTimeOfBatch: Long = System.currentTimeMillis()

    override suspend fun getLocalSteps(): List<StepBlock> {
        val startOfDay = getStartOfDayMillis()
        val now = System.currentTimeMillis()
        return getStepsBetween(startOfDay, now)
    }

    override suspend fun getStepsBetween(start: Long, end: Long): List<StepBlock> {
        return localStepDao.getStepsBetween(start, end).map { StepBlock(it.startTime, it.endTime, it.count) }
    }

    private fun getStartOfDayMillis(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
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
