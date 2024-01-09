package com.movieappjc.common.screenutil

import android.content.Context
import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowInsets
import android.view.WindowManager
import com.core_app.BaseApplication

object ScreenUtil {
    private var screenWidth = 0
    private var screenHeight = 0
    fun getScreenHeight(): Int {
        val context = BaseApplication.getInstance().applicationContext
        if (screenHeight == 0 && context != null) {
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

    fun getScreenWidth(): Int {
        val context = BaseApplication.getInstance().applicationContext
        if (screenWidth == 0 && context != null) {
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