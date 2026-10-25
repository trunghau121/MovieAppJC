package com.movieappjc.steptracker.domain.models

// Data model for uploading step records to the server
data class SyncPayload(val startTime: String, val endTime: String, val stepCount: Long)