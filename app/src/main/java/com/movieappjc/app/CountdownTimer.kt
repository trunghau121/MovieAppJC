package com.movieappjc.app

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object CountdownTimer {
    private const val totalTimeMillis: Long = 600000L
    private const val intervalMillis: Long = 1000L
    private var timerJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)
    val oShowDialog  = mutableStateOf(false)
    val onTimerFinish = mutableStateOf(false)

    // Bắt đầu countdown
    fun start() {
        timerJob = scope.launch {
            var remainingTime = totalTimeMillis
            while (remainingTime > 0) {
                if (remainingTime == 60000L) {
                    Log.d("CountdownTimer", "Show dialog")
                    oShowDialog.value = true
                }
                delay(intervalMillis)         // Chờ đến lần tick tiếp theo
                remainingTime -= intervalMillis
                Log.d("CountdownTimer", "Time: $remainingTime")
            }
            Log.d("CountdownTimer", "Het session")
            onTimerFinish.value = true                     // Gọi khi hết giờ
        }
    }

    // Hủy countdown
    fun cancel() {
        timerJob?.cancel()
    }
}