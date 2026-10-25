package com.movieappjc.steptracker.worker

import android.content.Context
import android.os.Build
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.StepsRecord
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.movieappjc.steptracker.data.local.StepStoreData
import com.movieappjc.steptracker.domain.models.SyncPayload
import com.movieappjc.steptracker.domain.repository.StepRepository
import com.movieappjc.steptracker.domain.usecase.ProcessStepsUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

// Worker responsible for periodically syncing local and watch step data to the server
@HiltWorker
class AdvancedSyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val stepRepository: StepRepository,
    private val storeData: StepStoreData,
    private val processStepsUseCase: ProcessStepsUseCase
) : CoroutineWorker(appContext, workerParams) {
    private val readPermission = HealthPermission.getReadPermission(StepsRecord::class)

    override suspend fun doWork(): Result {
        return try {
            val payloadsToSend = mutableListOf<SyncPayload>()
            val currentTime = System.currentTimeMillis()

            // ==========================================
            // PART 1: Capture Watch Data (Including delayed syncs using Token)
            // ==========================================
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && isHealthConnectReadPermissionGranted()) {
                // Fetch newly inserted/updated records from Health Connect using Token API
                val (watchRecords, nextToken) = stepRepository.getHealthConnectChanges()

                if (nextToken == null) {
                    // Generate initial token if none exists
                    storeData.hcChangesToken = stepRepository.getInitialHealthConnectToken()
                } else if (watchRecords.isNotEmpty()) {
                    // Extract time range of these new records (which might belong to the past)
                    val minTime = watchRecords.minOf { it.startTime }
                    val maxTime = watchRecords.maxOf { it.endTime }

                    // Fetch phone's local data from the exact same historical time range
                    val historicalLocal = stepRepository.getLocalSteps(minTime, maxTime)

                    // Merge and prioritize watch data over phone data, split into 15m slots
                    payloadsToSend.addAll(processStepsUseCase.execute(historicalLocal, watchRecords))

                    // Save new token for future syncs
                    storeData.hcChangesToken = nextToken
                }
            }

            // ==========================================
            // PART 2: Fetch recent Phone Data (since last sync)
            // ==========================================
            val lastSyncTime = if (storeData.lastSyncTime != -1L) storeData.lastSyncTime else (currentTime - 900_000L)
            val recentLocalData = stepRepository.getLocalSteps(lastSyncTime, currentTime)

            if (recentLocalData.isNotEmpty()) {
                val recentPayloads = processStepsUseCase.execute(recentLocalData, emptyList())

                // Avoid duplicating slots already processed in Part 1 (O(1) lookup using Set)
                val existingStartTimes = payloadsToSend.mapTo(mutableSetOf()) { it.startTime }
                recentPayloads.forEach { newPayload ->
                    if (existingStartTimes.add(newPayload.startTime)) {
                        payloadsToSend.add(newPayload)
                    }
                }
            }

            // ==========================================
            // PART 3: Upload to Server and Clean up
            // ==========================================
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

    private suspend fun isHealthConnectReadPermissionGranted(): Boolean {
        return try {
            val status = HealthConnectClient.getSdkStatus(applicationContext)
            if (status == HealthConnectClient.SDK_AVAILABLE) {
                val client = HealthConnectClient.getOrCreate(applicationContext)
                client.permissionController.getGrantedPermissions().contains(readPermission)
            } else false
        } catch (_: Exception) { false }
    }
}