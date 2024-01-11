package com.movieappjc.common.screenutil

import android.content.Context
import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowInsets
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

object ScreenUtil {
    private var screenWidth = 0
    private var screenHeight = 0
    @Composable
    fun getScreenHeight(): Int {
        val context = LocalContext.current
        if (screenHeight == 0) {
            val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            screenHeight = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val metrics = wm.currentWindowMetrics
                val insets = metrics.windowInsets.getInsets(WindowInsets.Type.systemBars())
                metrics.bounds.height() - insets.bottom - insets.top
            } else {
                val displayMetrics = DisplayMetrics()

                @Suppress("DEPRECATION")
                val display = wm.defaultDisplay // deprecated in API 30
                @Suppress("DEPRECATION")
                display.getMetrics(displayMetrics) // deprecated in API 30

                displayMetrics.heightPixels
            }
        }
        return screenHeight
    }

    @Composable
    fun getScreenWidth(): Int {
        val context = LocalContext.current
        if (screenWidth == 0) {
            val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            screenWidth = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val metrics = wm.currentWindowMetrics
                val insets = metrics.windowInsets.getInsets(WindowInsets.Type.systemBars())
                metrics.bounds.width() - insets.left - insets.right

            } else {
                val displayMetrics = DisplayMetrics()

                @Suppress("DEPRECATION")
                val display = wm.defaultDisplay // deprecated in API 30
                @Suppress("DEPRECATION")
                display.getMetrics(displayMetrics) // deprecated in API 30
                displayMetrics.widthPixels
            }
        }
        return screenWidth
    }
}