package com.movieappjc.app.common.screenutil

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.systemBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Stable
object ScreenUtil {
    private var screenWidth = 0.dp
    private var screenHeight = 0.dp
    private var statusBarHeight = 0.dp
    @Composable
    fun getScreenHeight(): Dp {
        if (screenHeight == 0.dp) {
            val configuration = LocalConfiguration.current
            screenHeight = configuration.screenHeightDp.dp
        }
        return screenHeight
    }

    @Composable
    fun getScreenWidth(): Dp {
        if (screenWidth == 0.dp) {
            val configuration = LocalConfiguration.current
            screenWidth = configuration.screenWidthDp.dp
        }
        return screenWidth
    }

    @Composable
    fun getStatusBarHeight(): Dp {
        if (statusBarHeight == 0.dp) {
            val systemBarsPadding = WindowInsets.systemBars.asPaddingValues()
            statusBarHeight = systemBarsPadding.calculateTopPadding()
        }
        return statusBarHeight
    }
}