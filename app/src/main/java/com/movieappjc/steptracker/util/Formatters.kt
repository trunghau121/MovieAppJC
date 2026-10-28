package com.movieappjc.steptracker.util

import java.util.Locale

// Utility object for text and number formatting
object Formatters {
    // Formats a number with comma separators (e.g., 10,000)
    fun steps(steps: Long): String = String.format(Locale.getDefault(), "%,d", steps)
}
