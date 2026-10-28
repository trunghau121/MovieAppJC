package com.movieappjc.steptracker.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.movieappjc.steptracker.data.local.StepStoreData
import com.movieappjc.steptracker.domain.models.SyncPayload
import com.movieappjc.steptracker.domain.repository.StepRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

// Worker responsible for syncing local step data to the server when app opens
@HiltWorker
class AdvancedSyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val stepRepository: StepRepository,
    private val storeData: StepStoreData
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val currentTime = System.currentTimeMillis()
            val lastSync = if (storeData.lastSyncTime != -1L) storeData.lastSyncTime else (currentTime - 86_400_000L)

            val recentLocalData = stepRepository.getStepsBetween(lastSync, currentTime)
            val totalStepsToSync = recentLocalData.sumOf { it.count }

            if (totalStepsToSync > 0) {
                val payload = SyncPayload(lastSync.toString(), currentTime.toString(), totalStepsToSync)
                val isSuccess = stepRepository.uploadStepsToServer(listOf(payload))
                if (isSuccess) {
                    storeData.lastSyncTime = currentTime
                    // Delete old local records older than 7 days
                    stepRepository.clearOldLocalSteps(currentTime - 604_800_000L)
                    Result.success()
                } else {
                    Result.retry() // Retry later if network fails
                }
            } else {
                storeData.lastSyncTime = currentTime
                Result.success()
            }
        } catch (_: Exception) {
            Result.failure()
        }
    }
}
