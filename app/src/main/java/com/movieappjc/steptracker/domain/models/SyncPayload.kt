package com.movieappjc.steptracker.domain.models

// Data model for uploading step records to the server.
// `id` is a deterministic idempotency key so the server can ignore duplicates when an upload is retried.
// Times are epoch milliseconds.
data class SyncPayload(
    val id: String,
    val startTime: Long,
    val endTime: Long,
    val stepCount: Long
)

fun StepBlock.toSyncPayload(): SyncPayload = SyncPayload(
    id = "$startTime-$endTime-$count",
    startTime = startTime,
    endTime = endTime,
    stepCount = count
)
