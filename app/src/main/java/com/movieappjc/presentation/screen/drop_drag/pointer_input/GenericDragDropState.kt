package com.movieappjc.presentation.screen.drop_drag.pointer_input

import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// === TỰ ĐỊNH NGHĨA INTERFACE CẦU NỐI ĐỂ THAY THẾ CHO CÁC CLASS ẨN CỦA GOOGLE ===
interface DragDropItemInfo {
    val index: Int
    val offset: IntOffset
    val size: IntSize
}

interface DragDropLayoutInfo {
    val viewportSize: IntSize
    val visibleItemsInfo: List<DragDropItemInfo>
}
// =========================================================================

class GenericDragDropState<T>(
    val listData: SnapshotStateList<T>,
    private val getLayoutInfo: () -> DragDropLayoutInfo, // Dùng interface tự chế
    private val scrollByLambda: suspend (Float) -> Float,
    private val canScrollBackwardLambda: () -> Boolean,
    private val canScrollForwardLambda: () -> Boolean,
    private val requestScrollToItemLambda: (index: Int, offset: Int) -> Unit,
    private val firstVisibleItemIndexLambda: () -> Int,
    private val firstVisibleItemScrollOffsetLambda: () -> Int,
    private val scope: CoroutineScope,
    private val ignoreIndices: IntRange = IntRange.EMPTY,
    private val onListChanged: (List<T>) -> Unit
) {
    var draggedIndex by mutableStateOf<Int?>(null)
        private set

    var fingerOffset by mutableStateOf(Offset.Zero)
        private set

    private var autoScrollJob: Job? = null

    fun onDragStart(offset: Offset) {
        val targetItem = findVisibleItemAtOffset(offset)
        if (targetItem != null && targetItem.index !in ignoreIndices) {
            draggedIndex = targetItem.index
            fingerOffset = offset
        }
    }

    fun onDrag(dragAmount: Offset) {
        if (draggedIndex == null) return
        fingerOffset += dragAmount
        checkForAutoScroll()
    }

    fun onDragEnd() {
        val source = draggedIndex

        if (source != null) {
            val targetItem = findVisibleItemAtOffset(fingerOffset)
            val target = targetItem?.index

            if (target != null && target != source) {
                if (target !in ignoreIndices && source !in ignoreIndices) {
                    val currentIndex = firstVisibleItemIndexLambda()
                    val currentOffset = firstVisibleItemScrollOffsetLambda()

                    val temp = listData[source]
                    listData[source] = listData[target]
                    listData[target] = temp

                    requestScrollToItemLambda(currentIndex, currentOffset)

                    onListChanged(listData.toList())
                }
            }
        }

        draggedIndex = null
        fingerOffset = Offset.Zero
        stopAutoScroll()
    }

    private fun checkForAutoScroll() {
        val layoutInfo = getLayoutInfo()
        val containerHeight = layoutInfo.viewportSize.height.toFloat()

        val activationZone = 120f
        val fingerY = fingerOffset.y
        val baseMaxSpeed = 150f

        val scrollAmount = when {
            fingerY < activationZone -> {
                val depth = activationZone - fingerY
                val speedFactor = (depth / activationZone) * 1.5f
                -(baseMaxSpeed * speedFactor).coerceAtMost(130f)
            }
            fingerY > (containerHeight - activationZone) -> {
                val depth = fingerY - (containerHeight - activationZone)
                val speedFactor = (depth / activationZone) * 1.5f
                (baseMaxSpeed * speedFactor).coerceAtMost(130f)
            }
            else -> 0f
        }

        if (scrollAmount != 0f) {
            if (autoScrollJob == null || autoScrollJob?.isActive == false) {
                autoScrollJob = scope.launch {
                    while (true) {
                        if (scrollAmount < 0f && !canScrollBackwardLambda()) break
                        if (scrollAmount > 0f && !canScrollForwardLambda()) break

                        scrollByLambda(scrollAmount)
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

    private fun findVisibleItemAtOffset(screenOffset: Offset): DragDropItemInfo? {
        val visibleItems = getLayoutInfo().visibleItemsInfo
        return visibleItems.find { item ->
            val top = item.offset.y
            val bottom = top + item.size.height

            // Kiểm tra trục Y trước (Cả Grid và Column đều cần)
            val isInsideY = screenOffset.y.toInt() in top..bottom

            if (item.size.width > 0) {
                // Nếu là Grid (có width > 0), kiểm tra nghiêm ngặt cả trục X
                val left = item.offset.x
                val right = left + item.size.width
                isInsideY && (screenOffset.x.toInt() in left..right)
            } else {
                // Nếu là Column (width = 0), chỉ cần đúng trục Y là húp luôn
                isInsideY
            }
        }
    }
}

// ================= HÀM KHỞI TẠO DÀNH CHO LAZYVERTICALGRID =================
@Composable
fun <T> rememberGridDragDropState(
    listData: SnapshotStateList<T>,
    lazyGridState: LazyGridState,
    scope: CoroutineScope,
    ignoreIndices: IntRange = IntRange.EMPTY,
    onListChanged: (List<T>) -> Unit
): GenericDragDropState<T> {
    return remember(lazyGridState, scope, listData, ignoreIndices) {
        GenericDragDropState(
            listData = listData,
            // Ánh xạ (Map) dữ liệu từ LazyGrid sang Interface dùng chung công khai
            getLayoutInfo = {
                object : DragDropLayoutInfo {
                    override val viewportSize: IntSize = lazyGridState.layoutInfo.viewportSize
                    override val visibleItemsInfo: List<DragDropItemInfo> = lazyGridState.layoutInfo.visibleItemsInfo.map { gridItem ->
                        object : DragDropItemInfo {
                            override val index: Int = gridItem.index
                            override val offset: IntOffset = gridItem.offset
                            override val size: IntSize = gridItem.size
                        }
                    }
                }
            },
            scrollByLambda = { delta -> lazyGridState.scrollBy(delta) },
            canScrollBackwardLambda = { lazyGridState.canScrollBackward },
            canScrollForwardLambda = { lazyGridState.canScrollForward },
            requestScrollToItemLambda = { index, offset -> lazyGridState.requestScrollToItem(index, offset) },
            firstVisibleItemIndexLambda = { lazyGridState.firstVisibleItemIndex },
            firstVisibleItemScrollOffsetLambda = { lazyGridState.firstVisibleItemScrollOffset },
            scope = scope,
            ignoreIndices = ignoreIndices,
            onListChanged = onListChanged
        )
    }
}

// ================= HÀM KHỞI TẠO DÀNH CHO LAZYCOLUMN =================
@Composable
fun <T> rememberListDragDropState(
    listData: SnapshotStateList<T>,
    lazyListState: LazyListState,
    scope: CoroutineScope,
    ignoreIndices: IntRange = IntRange.EMPTY,
    onListChanged: (List<T>) -> Unit
): GenericDragDropState<T> {
    return remember(lazyListState, scope, listData, ignoreIndices) {
        GenericDragDropState(
            listData = listData,
            // Ánh xạ (Map) dữ liệu từ LazyList sang Interface dùng chung công khai
            getLayoutInfo = {
                object : DragDropLayoutInfo {
                    override val viewportSize: IntSize = lazyListState.layoutInfo.viewportSize
                    override val visibleItemsInfo: List<DragDropItemInfo> = lazyListState.layoutInfo.visibleItemsInfo.map { listItem ->
                        object : DragDropItemInfo {
                            override val index: Int = listItem.index
                            // LazyColumn trả về y tương đối, x luôn = 0
                            override val offset: IntOffset = IntOffset(x = 0, y = listItem.offset)
                            override val size: IntSize = IntSize(width = 0, height = listItem.size)
                        }
                    }
                }
            },
            scrollByLambda = { delta -> lazyListState.scrollBy(delta) },
            canScrollBackwardLambda = { lazyListState.canScrollBackward },
            canScrollForwardLambda = { lazyListState.canScrollForward },
            requestScrollToItemLambda = { index, offset -> lazyListState.requestScrollToItem(index, offset) },
            firstVisibleItemIndexLambda = { lazyListState.firstVisibleItemIndex },
            firstVisibleItemScrollOffsetLambda = { lazyListState.firstVisibleItemScrollOffset },
            scope = scope,
            ignoreIndices = ignoreIndices,
            onListChanged = onListChanged
        )
    }
}