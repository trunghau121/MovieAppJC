package com.movieappjc.steptracker.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.movieappjc.steptracker.data.local.StepStoreData
import com.movieappjc.steptracker.domain.models.toSyncPayload
import com.movieappjc.steptracker.domain.repository.StepRepository
import com.movieappjc.steptracker.util.StepConstants
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException
import java.io.IOException

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
            val now = System.currentTimeMillis()

            // The newest blocks may still be in flight, so the window stops a little before "now".
            // Windows are [lastSync, syncEnd) on block endTime, so consecutive syncs neither overlap nor leave gaps.
            val syncEnd = now - StepConstants.SYNC_SAFETY_MARGIN_MS
            val lastSync = storeData.lastSyncTime.takeIf { it != -1L }
                ?: (now - StepConstants.INITIAL_SYNC_WINDOW_MS)

            if (syncEnd > lastSync) {
                val blocks = stepRepository.getStepsBetween(lastSync, syncEnd)
                if (blocks.isNotEmpty()) {
                    // Per-block payloads keep the original granularity; ids make retries idempotent
                    for (chunk in blocks.map { it.toSyncPayload() }.chunked(StepConstants.SYNC_CHUNK_SIZE)) {
                        if (!stepRepository.uploadStepsToServer(chunk)) return retryOrFail()
                    }
                }
                storeData.lastSyncTime = syncEnd
            }

            // Everything older than the retention window is already synced (or there was nothing to sync)
            stepRepository.clearOldLocalSteps(now - StepConstants.LOCAL_RETENTION_MS)
            Result.success()
        } catch (e: CancellationException) {
            throw e // never swallow cancellation (e.g. ExistingWorkPolicy.REPLACE)
        } catch (e: IOException) {
            retryOrFail()
        } catch (e: Exception) {
            Log.e(TAG, "Sync failed", e)
            Result.failure()
        }
    }

    private fun retryOrFail(): Result =
        if (runAttemptCount < StepConstants.SYNC_MAX_ATTEMPTS) Result.retry() else Result.failure()

    private companion object {
        const val TAG = "AdvancedSyncWorker"
    }
}
