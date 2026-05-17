package com.movieappjc.presentation.screen.aa

import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.lazy.grid.LazyGridItemInfo
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.geometry.Offset
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PointerDragDropState(
    val listData: SnapshotStateList<String>,
    private val lazyGridState: LazyGridState,
    private val scope: CoroutineScope
) {
    var draggedIndex by mutableStateOf<Int?>(null)
        private set

    var fingerOffset by mutableStateOf(Offset.Zero)
        private set

    private var autoScrollJob: Job? = null

    fun onDragStart(offset: Offset) {
        val targetItem = findVisibleItemAtOffset(offset)
        if (targetItem != null && targetItem.index !in 9..12) {
            draggedIndex = targetItem.index
            fingerOffset = offset
        }
    }

    fun onDrag(dragAmount: Offset) {
        if (draggedIndex == null) return
        fingerOffset += dragAmount
        checkForAutoScroll()
    }

    // ĐƯỢC KÍCH HOẠT KHI THẢ TAY (DROP)
    fun onDragEnd() {
        val source = draggedIndex

        if (source != null) {
            val targetItem = findVisibleItemAtOffset(fingerOffset)
            val target = targetItem?.index

            if (target != null && target != source) {
                if (target !in 9..12 && source !in 9..12) {

                    // === SỬA LỖI NHẢY SCROLL TẠI ĐÂY ===
                    // 1. Chụp lại vị trí cuộn hiện tại của màn hình (Index ô đầu tiên đang hiện và số pixel lệch)
                    val currentIndex = lazyGridState.firstVisibleItemIndex
                    val currentOffset = lazyGridState.firstVisibleItemScrollOffset

                    // 2. Tiến hành hoán đổi vị trí dữ liệu mảng
                    val temp = listData[source]
                    listData[source] = listData[target]
                    listData[target] = temp

                    // 3. Khóa vị trí: Ép màn hình phải đứng im re tại tọa độ vừa chụp,
                    // chặn không cho hệ thống tự động scroll theo item neo cũ.
                    lazyGridState.requestScrollToItem(
                        index = currentIndex,
                        scrollOffset = currentOffset
                    )
                    // ===================================
                }
            }
        }

        draggedIndex = null
        fingerOffset = Offset.Zero
        stopAutoScroll()
    }

    private fun checkForAutoScroll() {
        val layoutInfo = lazyGridState.layoutInfo
        val containerHeight = layoutInfo.viewportSize.height.toFloat()

        val activationZone = 150f
        val fingerY = fingerOffset.y

        val scrollAmount = when {
            fingerY < activationZone -> {
                val depth = activationZone - fingerY
                val speedFactor = (depth / activationZone) * 1.5f
                -(110f * speedFactor).coerceAtMost(130f)
            }
            fingerY > (containerHeight - activationZone) -> {
                val depth = fingerY - (containerHeight - activationZone)
                val speedFactor = (depth / activationZone) * 1.5f
                (110f * speedFactor).coerceAtMost(130f)
            }
            else -> 0f
        }

        if (scrollAmount != 0f) {
            if (autoScrollJob == null || autoScrollJob?.isActive == false) {
                autoScrollJob = scope.launch {
                    while (true) {
                        if (scrollAmount < 0f && !lazyGridState.canScrollBackward) break
                        if (scrollAmount > 0f && !lazyGridState.canScrollForward) break

                        lazyGridState.scrollBy(scrollAmount)
                        delay(8)
                    }
                }
            }
        } else {
            stopAutoScroll()
        }
    }

    private fun stopAutoScroll() {
        autoScrollJob?.cancel()
        autoScrollJob = null
    }

    private fun findVisibleItemAtOffset(screenOffset: Offset): LazyGridItemInfo? {
        val layoutInfo = lazyGridState.layoutInfo
        val visibleItems = layoutInfo.visibleItemsInfo

        return visibleItems.find { item ->
            val left = item.offset.x
            val right = left + item.size.width
            val top = item.offset.y
            val bottom = top + item.size.height

            screenOffset.x.toInt() in left..right && screenOffset.y.toInt() in top..bottom
        }
    }
}