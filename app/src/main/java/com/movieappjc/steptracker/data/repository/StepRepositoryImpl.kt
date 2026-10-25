package com.movieappjc.steptracker.data.repository

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.changes.UpsertionChange
import androidx.health.connect.client.request.ChangesTokenRequest
import androidx.health.connect.client.records.StepsRecord
import com.movieappjc.steptracker.data.local.LocalStepDao
import com.movieappjc.steptracker.data.local.LocalStepEntity
import com.movieappjc.steptracker.data.local.StepStoreData
import com.movieappjc.steptracker.domain.models.StepBlock
import com.movieappjc.steptracker.domain.models.SyncPayload
import com.movieappjc.steptracker.domain.repository.StepRepository

class StepRepositoryImpl(
    private val context: Context,
    private val localStepDao: LocalStepDao,
    private val storeData: StepStoreData
) : StepRepository {

    private val healthConnectClient by lazy { HealthConnectClient.getOrCreate(context) }
    // Ignore phone sensor data from Health Connect to prevent duplicates
    private val systemPackage = "com.android.healthconnect.phone"

    override suspend fun getLocalSteps(start: Long, end: Long): List<StepBlock> {
        return localStepDao.getStepsBetween(start, end).map { StepBlock(it.startTime, it.endTime, it.count) }
    }

    override suspend fun saveLocalSteps(stepBlock: StepBlock) {
        localStepDao.insertStepBlock(LocalStepEntity(startTime = stepBlock.startTime, endTime = stepBlock.endTime, count = stepBlock.count))
    }

    override suspend fun clearOldLocalSteps(cutoff: Long) {
        localStepDao.deleteOldSteps(cutoff)
    }

    override suspend fun getInitialHealthConnectToken(): String {
        return healthConnectClient.getChangesToken(ChangesTokenRequest(recordTypes = setOf(StepsRecord::class)))
    }

    override suspend fun getHealthConnectChanges(): Pair<List<StepBlock>, String?> {
        // Retrieve the saved token from StoreData
        val savedToken = storeData.hcChangesToken ?: return Pair(emptyList(), null)
        val watchRecords = mutableListOf<StepBlock>()
        var currentToken = savedToken
        var hasMore = true

        while (hasMore) {
            val response = healthConnectClient.getChanges(currentToken)

            // Filter new/updated records, exclude phone sensor data to keep only watch data
            val validUpserts = response.changes
                .filterIsInstance<UpsertionChange>()
                .map { it.record }
                .filterIsInstance<StepsRecord>()
                .filter { !it.metadata.dataOrigin.packageName.contains(systemPackage) }

            // Convert Instant to Epoch Milliseconds
            watchRecords.addAll(validUpserts.map { StepBlock(it.startTime.toEpochMilli(), it.endTime.toEpochMilli(), it.count) })

            currentToken = response.nextChangesToken
            hasMore = response.hasMore
        }

        return Pair(watchRecords, currentToken)
    }

    override suspend fun uploadStepsToServer(payloads: List<SyncPayload>): Boolean {
        // Mocking successful server upload
        return true
    }
}