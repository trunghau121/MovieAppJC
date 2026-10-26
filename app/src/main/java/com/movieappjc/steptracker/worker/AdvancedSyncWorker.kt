package com.movieappjc.steptracker.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.movieappjc.steptracker.data.local.StepStoreData
import com.movieappjc.steptracker.domain.repository.StepRepository
import com.movieappjc.steptracker.domain.usecase.ProcessStepsUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

// Worker responsible for periodically syncing local step data to the server
@HiltWorker
class AdvancedSyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val stepRepository: StepRepository,
    private val storeData: StepStoreData,
    private val processStepsUseCase: ProcessStepsUseCase
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val currentTime = System.currentTimeMillis()

            // Fetch recent Phone Data (since last sync)
            val lastSyncTime = if (storeData.lastSyncTime != -1L) storeData.lastSyncTime else (currentTime - 900_000L)
            val recentLocalData = stepRepository.getLocalSteps(lastSyncTime, currentTime)

            val payloadsToSend = if (recentLocalData.isNotEmpty()) {
                processStepsUseCase.execute(recentLocalData)
            } else {
                emptyList()
            }

            // Upload to Server and Clean up
            if (payloadsToSend.isNotEmpty()) {
                val isSuccess = stepRepository.uploadStepsToServer(payloadsToSend)
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
