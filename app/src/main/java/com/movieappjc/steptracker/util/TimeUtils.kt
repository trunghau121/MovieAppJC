package com.movieappjc.steptracker.util

import java.util.Calendar

object TimeUtils {
    fun startOfDayMillis(time: Long = System.currentTimeMillis()): Long {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = time
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }

    // DST-safe start of the following day
    fun startOfNextDayMillis(time: Long = System.currentTimeMillis()): Long {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = startOfDayMillis(time)
            add(Calendar.DAY_OF_YEAR, 1)
        }
        return calendar.timeInMillis
    }
}
