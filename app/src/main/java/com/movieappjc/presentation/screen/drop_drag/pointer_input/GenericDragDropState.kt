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

// === INTERFACE CẦU NỐI CHUNG ===
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
    private val getLayoutInfo: () -> DragDropLayoutInfo,
    private val scrollByLambda: suspend (Float) -> Float,
    private val canScrollBackwardLambda: () -> Boolean,
    private val canScrollForwardLambda: () -> Boolean,
    private val requestScrollToItemLambda: (index: Int, offset: Int) -> Unit,
    private val firstVisibleItemIndexLambda: () -> Int,
    private val firstVisibleItemScrollOffsetLambda: () -> Int,
    private val scope: CoroutineScope,
    private val ignoreIndices: IntRange = IntRange.EMPTY,
    private val onListChanged: (List<T>) -> Unit,
    private val isItemLocked: (item: T) -> Boolean = { false }
) {
    var draggedIndex by mutableStateOf<Int?>(null)
        private set

    var fingerOffset by mutableStateOf(Offset.Zero)
        private set

    var initialTouchOffset by mutableStateOf(Offset.Zero)
        private set

    var draggedItemSize by mutableStateOf(IntSize.Zero)
        private set

    private var autoScrollJob: Job? = null

    fun onDragStart(offset: Offset) {
        val targetItem = findVisibleItemAtOffset(offset)
        if (targetItem != null && targetItem.index !in ignoreIndices) {
            draggedIndex = targetItem.index
            fingerOffset = offset
            draggedItemSize = targetItem.size

            initialTouchOffset = Offset(
                x = offset.x - targetItem.offset.x,
                y = offset.y - targetItem.offset.y
            )
        }
    }

    // LOGIC KHI KÉO: TUYỆT ĐỐI KHÔNG CHO HOÁN ĐỔI TỰ ĐỘNG NẾU Ô ĐÍCH BỊ KHÓA (LOCKED)
    fun onDrag(dragAmount: Offset) {
        val source = draggedIndex ?: return
        fingerOffset += dragAmount

        val targetItem = findVisibleItemAtOffset(fingerOffset)
        val target = targetItem?.index

        if (target != null && target != source) {
            if (target !in ignoreIndices && source !in ignoreIndices) {

                val targetItemData = listData.getOrNull(target)

                if (targetItemData != null) {
                    val isTargetLocked = isItemLocked(targetItemData)

                    // LUẬT TỐI CAO LÚC KÉO: Nếu ô đích bên dưới là ô Locked -> canSwap = false
                    // Bất kể ô bạn đang cầm trên tay là ô thường hay ô Locked đi chăng nữa!
                    val canSwapDuringDrag = !isTargetLocked

                    if (canSwapDuringDrag) {
                        val currentIndex = firstVisibleItemIndexLambda()
                        val currentOffset = firstVisibleItemScrollOffsetLambda()

                        // Chỉ tự động hoán đổi liên tục giữa các ô tự do với nhau
                        val temp = listData[source]
                        listData[source] = listData[target]
                        listData[target] = temp

                        draggedIndex = target

                        requestScrollToItemLambda(currentIndex, currentOffset)
                        onListChanged(listData.toList())
                    }
                }
            }
        }
        checkForAutoScroll()
    }

    // LOGIC KHI DROP (BUÔNG TAY): Thời điểm duy nhất cho phép ép hoán đổi với ô Locked
    fun onDragEnd() {
        val source = draggedIndex
        if (source != null) {
            val targetItem = findVisibleItemAtOffset(fingerOffset)
            val target = targetItem?.index

            if (target != null && target != source) {
                if (target !in ignoreIndices && source !in ignoreIndices) {

                    val targetItemData = listData.getOrNull(target)

                    if (targetItemData != null) {
                        // Khi lướt qua thì bị chặn hoàn toàn, nhưng khi THẢ TAY NGAY TRÊN ĐẦU ô khóa:
                        // Cho phép thực hiện hoán đổi đúng 1 lần duy nhất tại đây.
                        if (isItemLocked(targetItemData)) {

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
            }
        }

        // Reset trạng thái
        draggedIndex = null
        fingerOffset = Offset.Zero
        initialTouchOffset = Offset.Zero
        draggedItemSize = IntSize.Zero
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
                -(baseMaxSpeed * speedFactor).coerceAtMost(150f)
            }
            fingerY > (containerHeight - activationZone) -> {
                val depth = fingerY - (containerHeight - activationZone)
                val speedFactor = (depth / activationZone) * 1.5f
                (baseMaxSpeed * speedFactor).coerceAtMost(150f)
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
            val isInsideY = screenOffset.y.toInt() in top..bottom

            if (item.size.width > 0) {
                val left = item.offset.x
                val right = left + item.size.width
                isInsideY && (screenOffset.x.toInt() in left..right)
            } else {
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
    isItemLocked: (T) -> Boolean = { false },
    onListChanged: (List<T>) -> Unit
): GenericDragDropState<T> {
    return remember(lazyGridState, scope, listData, ignoreIndices) {
        GenericDragDropState(
            listData = listData,
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
            isItemLocked = isItemLocked,
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
    isItemLocked: (T) -> Boolean = { false },
    onListChanged: (List<T>) -> Unit
): GenericDragDropState<T> {
    return remember(lazyListState, scope, listData, ignoreIndices) {
        GenericDragDropState(
            listData = listData,
            getLayoutInfo = {
                object : DragDropLayoutInfo {
                    override val viewportSize: IntSize = lazyListState.layoutInfo.viewportSize
                    override val visibleItemsInfo: List<DragDropItemInfo> = lazyListState.layoutInfo.visibleItemsInfo.map { listItem ->
                        object : DragDropItemInfo {
                            override val index: Int = listItem.index
                            override val offset: IntOffset = IntOffset(x = 0, y = listItem.offset)
                            override val size: IntSize = IntSize(
                                width = lazyListState.layoutInfo.viewportSize.width,
                                height = listItem.size
                            )
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
            isItemLocked = isItemLocked,
            onListChanged = onListChanged
        )
    }
}