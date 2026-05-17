package com.movieappjc.presentation.screen.test

import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.lazy.grid.LazyGridState
import com.movieappjc.presentation.screen.drop_drag.DragDropScroller
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GridDragDropScroller(
    private val gridState: LazyGridState,
    private val scope: CoroutineScope,
    private val thresholdPx: Float,
    private val maxSpeed: Float = 55f
) : DragDropScroller {
    private var scrollJob: Job? = null

    override fun handleDragPosition(pointerAbsoluteY: Float, containerTopPx: Float, containerBottomPx: Float) {
        if (containerBottomPx <= containerTopPx) { stop(); return }

        val topActivationZone = containerTopPx + thresholdPx
        val bottomActivationZone = containerBottomPx - thresholdPx

        val scrollAmount = when {
            pointerAbsoluteY <= topActivationZone -> {
                val depth = topActivationZone - pointerAbsoluteY
                val ratio = (depth / thresholdPx).coerceIn(0.1f, 3.5f)
                -maxSpeed * ratio
            }
            pointerAbsoluteY >= bottomActivationZone -> {
                val depth = pointerAbsoluteY - bottomActivationZone
                val ratio = (depth / thresholdPx).coerceIn(0.1f, 3.5f)
                maxSpeed * ratio
            }
            else -> 0f
        }

        if (scrollAmount != 0f) {
            if (scrollJob == null || scrollJob?.isActive == false) {
                scrollJob = scope.launch {
                    while (true) {
                        if (scrollAmount < 0f && !gridState.canScrollBackward) { stop(); break }
                        if (scrollAmount > 0f && !gridState.canScrollForward) { stop(); break }
                        gridState.scrollBy(scrollAmount)
                        delay(5)
                    }
                }
            }
        } else {
            stop()
        }
    }

    override fun findTargetIndex(dropTargetX: Float, dropTargetY: Float, containerLeftPx: Float, containerTopPx: Float): Int? {
        val layoutInfo = gridState.layoutInfo
        val relativeX = dropTargetX - containerLeftPx
        val relativeY = dropTargetY - containerTopPx + layoutInfo.beforeContentPadding

        val visibleItems = layoutInfo.visibleItemsInfo
        for (itemInfo in visibleItems) {
            val itemLeft = itemInfo.offset.x.toFloat()
            val itemRight = itemLeft + itemInfo.size.width
            val itemTop = itemInfo.offset.y.toFloat()
            val itemBottom = itemTop + itemInfo.size.height

            if (relativeX in itemLeft..itemRight && relativeY in itemTop..itemBottom) {
                return itemInfo.index
            }
        }
        return null
    }

    // ĐÓNG BĂNG VỊ TRÍ CUỘN CỦA GRID
    override fun requestKeepPosition() {
        val firstIndex = gridState.firstVisibleItemIndex
        val firstOffset = gridState.firstVisibleItemScrollOffset
        scope.launch {
            // Khóa chặt khung nhìn tại ô hiển thị đầu tiên và tọa độ offset hiện tại
            gridState.scrollToItem(firstIndex, firstOffset)
        }
    }

    override fun stop() {
        scrollJob?.cancel()
        scrollJob = null
    }
}