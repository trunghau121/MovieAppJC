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

class GenericDragDropState<T>(
    val listData: SnapshotStateList<T>,
    val getLayoutInfo: () -> DragDropLayoutInfo,
    private val scrollByLambda: suspend (Float) -> Float,
    private val canScrollBackwardLambda: () -> Boolean,
    private val canScrollForwardLambda: () -> Boolean,
    private val requestScrollToItemLambda: (index: Int, offset: Int) -> Unit,
    private val firstVisibleItemIndexLambda: () -> Int,
    private val firstVisibleItemScrollOffsetLambda: () -> Int,
    private val scope: CoroutineScope,
    private val ignoreIndices: IntRange = IntRange.EMPTY,
    private val onListChanged: (List<T>) -> Unit,
    private val dragDropPolicy: DragDropPolicy<T>,
    private val getDragDropContext: () -> DragDropContext = { DragDropContext() }
) {
    var draggedIndex by mutableStateOf<Int?>(null)
        private set

    var fingerOffset by mutableStateOf(Offset.Zero)
        private set

    var initialTouchOffset by mutableStateOf(Offset.Zero)
        private set

    var draggedItemSize by mutableStateOf(IntSize.Zero)
        private set

    var isReturningAnimation by mutableStateOf(false)
        private set

    var pendingSwapTargetIndex by mutableStateOf<Int?>(null)
        private set

    // Ghi nhớ ID của phần tử vừa thả tay để tiếp tục khóa ẩn UI dưới nền
    var lastDraggedId by mutableStateOf<String?>(null)
        private set

    private var autoScrollJob: Job? = null
    var dragStartAbsoluteOffset by mutableStateOf(Offset.Zero)
        private set

    fun onDragStart(offset: Offset) {
        if (isReturningAnimation || lastDraggedId != null) return

        val targetItem = findVisibleItemAtOffset(offset)
        if (targetItem != null && targetItem.index !in ignoreIndices) {
            val itemData = listData.getOrNull(targetItem.index)

            if (itemData != null && dragDropPolicy.isItemFixed(itemData, getDragDropContext())) {
                return
            }

            draggedIndex = targetItem.index
            fingerOffset = offset
            draggedItemSize = targetItem.size
            initialTouchOffset = Offset(
                x = offset.x - targetItem.offset.x,
                y = offset.y - targetItem.offset.y
            )

            dragStartAbsoluteOffset = Offset(targetItem.offset.x.toFloat(), targetItem.offset.y.toFloat())
            pendingSwapTargetIndex = null
            lastDraggedId = null
        }
    }

    fun onDrag(dragAmount: Offset) {
        if (isReturningAnimation) return
        val source = draggedIndex ?: return
        fingerOffset += dragAmount
        checkAndPerformSwap(source)
        checkForAutoScroll()
    }

    private fun checkAndPerformSwap(source: Int) {
        val targetItem = findVisibleItemAtOffset(fingerOffset)
        val target = targetItem?.index

        if (target != null && target != source) {
            if (target !in ignoreIndices && source !in ignoreIndices) {
                val targetItemData = listData.getOrNull(target)

                if (targetItemData != null) {
                    val context = getDragDropContext()
                    val isTargetRestricted = dragDropPolicy.shouldRestrictSwapUntilDrop(targetItemData, context)
                    val isTargetFixed = dragDropPolicy.isItemFixed(targetItemData, context)

                    val canSwapDuringDrag = !isTargetRestricted && !isTargetFixed

                    if (canSwapDuringDrag) {
                        val currentIndex = firstVisibleItemIndexLambda()
                        val currentOffset = firstVisibleItemScrollOffsetLambda()

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
    }

    private fun checkForAutoScroll() {
        val layoutInfo = getLayoutInfo()
        val containerHeight = layoutInfo.viewportSize.height.toFloat()
        val activationZone = 120f
        val fingerY = fingerOffset.y
        val baseMaxSpeed = 200f

        val scrollAmount = when {
            fingerY < activationZone -> {
                val depth = activationZone - fingerY
                val speedFactor = (depth / activationZone) * 1.5f
                -(baseMaxSpeed * speedFactor).coerceAtMost(200f)
            }
            fingerY > (containerHeight - activationZone) -> {
                val depth = fingerY - (containerHeight - activationZone)
                val speedFactor = (depth / activationZone) * 1.5f
                (baseMaxSpeed * speedFactor).coerceAtMost(200f)
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

                        draggedIndex?.let { currentSource ->
                            checkAndPerformSwap(currentSource)
                        }

                        delay(8)
                    }
                }
            }
        } else {
            stopAutoScroll()
        }
    }

    fun onDragEnd() {
        val source = draggedIndex
        if (source != null) {
            val targetItem = findVisibleItemAtOffset(fingerOffset)
            val target = targetItem?.index

            if (target != null && target != source) {
                if (target !in ignoreIndices && source !in ignoreIndices) {
                    val targetItemData = listData.getOrNull(target)
                    if (targetItemData != null) {
                        val context = getDragDropContext()
                        val isTargetRestricted = dragDropPolicy.shouldRestrictSwapUntilDrop(targetItemData, context)
                        val isTargetFixed = dragDropPolicy.isItemFixed(targetItemData, context)

                        if (isTargetRestricted && !isTargetFixed) {
                            pendingSwapTargetIndex = target
                        }
                    }
                }
            }
            isReturningAnimation = true
        } else {
            clearDragStateAfterAnimation(null)
        }
        stopAutoScroll()
    }

    // HÀM CHÍNH THỨC HOÁN ĐỔI DỮ LIỆU: Chờ bóng ma bay về chạm đích xong mới chạy
    fun clearDragStateAfterAnimation(itemId: String?) {
        val source = draggedIndex
        val target = pendingSwapTargetIndex

        if (source != null && target != null && source != target) {
            val currentIndex = firstVisibleItemIndexLambda()
            val currentOffset = firstVisibleItemScrollOffsetLambda()

            // 1. Khóa ID lại trước khi thực hiện tráo đổi thực tế công công
            lastDraggedId = itemId

            val temp = listData[source]
            listData[source] = listData[target]
            listData[target] = temp

            requestScrollToItemLambda(currentIndex, currentOffset)
            onListChanged(listData.toList())

            // 2. Chờ đúng 400ms bằng thời gian animateItem để item gốc di chuyển xong mới mở khóa hiển thị công khai
            scope.launch {
                delay(150)
                lastDraggedId = null
            }
        }

        draggedIndex = null
        pendingSwapTargetIndex = null
        fingerOffset = Offset.Zero
        initialTouchOffset = Offset.Zero
        draggedItemSize = IntSize.Zero
        isReturningAnimation = false
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

@Composable
fun <T> rememberGridDragDropState(
    listData: SnapshotStateList<T>,
    lazyGridState: LazyGridState,
    scope: CoroutineScope,
    dragDropPolicy: DragDropPolicy<T>,
    getDragDropContext: () -> DragDropContext = { DragDropContext() },
    ignoreIndices: IntRange = IntRange.EMPTY,
    onListChanged: (List<T>) -> Unit
): GenericDragDropState<T> {
    return remember(lazyGridState, scope, listData, ignoreIndices, dragDropPolicy) {
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
            dragDropPolicy = dragDropPolicy,
            getDragDropContext = getDragDropContext,
            onListChanged = onListChanged
        )
    }
}

@Composable
fun <T> rememberListDragDropState(
    listData: SnapshotStateList<T>,
    lazyListState: LazyListState,
    scope: CoroutineScope,
    dragDropPolicy: DragDropPolicy<T>,
    getDragDropContext: () -> DragDropContext = { DragDropContext() },
    ignoreIndices: IntRange = IntRange.EMPTY,
    onListChanged: (List<T>) -> Unit
): GenericDragDropState<T> {
    return remember(lazyListState, scope, listData, ignoreIndices, dragDropPolicy) {
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
            dragDropPolicy = dragDropPolicy,
            getDragDropContext = getDragDropContext,
            onListChanged = onListChanged
        )
    }
}