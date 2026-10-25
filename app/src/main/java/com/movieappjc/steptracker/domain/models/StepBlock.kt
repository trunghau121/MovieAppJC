package com.movieappjc.steptracker.domain.models

// Domain model representing a block of recorded steps
data class StepBlock(val startTime: Long, val endTime: Long, val count: Long)