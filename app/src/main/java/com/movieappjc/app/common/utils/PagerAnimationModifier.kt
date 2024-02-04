package com.movieappjc.app.common.utils

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.PagerState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import kotlin.math.absoluteValue

@OptIn(ExperimentalFoundationApi::class)
fun Modifier.pagerAnimation(
    pagerState: PagerState,
    maxOffset: Float,
    pageIndex: Int,
) = then(
    graphicsLayer {
        val offset = maxOffset * when (pageIndex) {
            pagerState.currentPage -> {
                pagerState.currentPageOffsetFraction.absoluteValue
            }

            pagerState.currentPage - 1 -> {
                1 + pagerState.currentPageOffsetFraction.coerceAtMost(0f)
            }

            pagerState.currentPage + 1 -> {
                1 - pagerState.currentPageOffsetFraction.coerceAtLeast(0f)
            }

            else -> {
                1f
            }
        }
        translationY = -offset
    },
)