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
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GenericDragDropState<T>(
    val listData: SnapshotStateList<T>,
    val getLayoutInfo: () -> DragDropLayoutInfo,
    private val scrollBy: suspend (Float) -> Float,
    private val canScrollBackward: () -> Boolean,
    private val canScrollForward: () -> Boolean,
    private val requestScrollToItem: (index: Int, offset: Int) -> Unit,
    private val firstVisibleItemIndex: () -> Int,
    private val firstVisibleItemScrollOffset: () -> Int,
    private val scope: CoroutineScope,
    private val ignoreIndices: IntRange = IntRange.EMPTY,
    private val dragDropPolicy: DragDropPolicy<T>,
    private val getDragDropContext: () -> DragDropContext = { DragDropContext() },
    private val onListChanged: (List<T>) -> Unit,
    private val onDropEnd: (Int, Int) -> Unit
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

    // Ghi nhớ chính bản thể đối tượng vừa thả tay để giữ ẩn UI nền (Đã sửa lỗi Generic)
    var lastDraggedItem by mutableStateOf<T?>(null)
        private set

    private var autoScrollJob: Job? = null
    private var resetAnimationJob: Job? = null

    var dragStartAbsoluteOffset by mutableStateOf(Offset.Zero)
        private set

    private var lastCheckedFingerOffset = Offset.Zero

    fun onDragStart(offset: Offset) {
        if (isReturningAnimation || lastDraggedItem != null) return

        val targetItem = findVisibleItemAtOffset(offset)
        if (targetItem != null && targetItem.index !in ignoreIndices) {
            val itemData = listData.getOrNull(targetItem.index)

            if (itemData != null && dragDropPolicy.isItemFixed(itemData, getDragDropContext())) {
                return
            }

            draggedIndex = targetItem.index
            fingerOffset = offset
            lastCheckedFingerOffset = offset // Reset bộ đo khoảng cách chống trôi lệch dữ liệu
            draggedItemSize = targetItem.size
            initialTouchOffset = Offset(
                x = offset.x - targetItem.offset.x,
                y = offset.y - targetItem.offset.y
            )

            dragStartAbsoluteOffset = Offset(targetItem.offset.x.toFloat(), targetItem.offset.y.toFloat())
            pendingSwapTargetIndex = null
            lastDraggedItem = null
        }
    }

    fun onDrag(dragAmount: Offset) {
        if (isReturningAnimation) return
        val source = draggedIndex ?: return

        fingerOffset += dragAmount

        // TỐI ƯU: Chỉ tính toán quét Layout hình học nếu ngón tay dịch chuyển một khoảng đủ lớn
        val distanceMoved = (fingerOffset - lastCheckedFingerOffset).getDistance()
        if (distanceMoved > 8f) {
            checkAndPerformSwap(source)
            lastCheckedFingerOffset = fingerOffset
        }

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
                        val currentIndex = firstVisibleItemIndex()
                        val currentOffset = firstVisibleItemScrollOffset()

                        val temp = listData[source]
                        listData[source] = listData[target]
                        listData[target] = temp

                        draggedIndex = target

                        requestScrollToItem(currentIndex, currentOffset)
                        onListChanged(listData.toList())
                    }
                }
            }
        }
    }

    private var currentScrollSpeed = 0f

    private fun checkForAutoScroll() {
        val layoutInfo = getLayoutInfo()
        val containerHeight = layoutInfo.viewportSize.height.toFloat()
        val activationZone = 120f
        val fingerY = fingerOffset.y

        // Tốc độ cuộn tối đa (px/giây) - Bạn có thể tăng lên 1000f nếu thích cuộn cực nhanh
        val maxSpeedPxPerSecond = 1000f

        // Tính toán tỷ lệ tốc độ dựa theo độ sâu ngón tay đi vào vùng nhạy cảm
        val targetSpeed = when {
            fingerY in 0f..<activationZone -> {
                -(1.0f - (fingerY / activationZone)) * maxSpeedPxPerSecond
            }
            fingerY > (containerHeight - activationZone) && fingerY <= containerHeight -> {
                ((fingerY - (containerHeight - activationZone)) / activationZone) * maxSpeedPxPerSecond
            }
            else -> 0f
        }

        currentScrollSpeed = targetSpeed

        if (currentScrollSpeed != 0f) {
            // Nếu Job cuộn tự động chưa chạy, hãy kích hoạt nó
            if (autoScrollJob == null || autoScrollJob?.isActive == false) {
                autoScrollJob = scope.launch {
                    try {
                        // SỬ DỤNG KHỐI SCROLL LIÊN TỤC: Đưa LazyList vào trạng thái cuộn mượt vô cấp công nghiệp
                        // Giải phóng hoàn toàn chi phí block luồng của từng lệnh scrollBy lẻ tẻ
                        var lastFrameTime = System.nanoTime()

                        // Thực hiện cơ chế cuộn cấp độ animation cao nhất của Compose
                        scrollBy(0f) // Khởi tạo trạng thái scroll an toàn

                        // Chúng ta mượn hàm cuộn liên tục thông qua việc lặp hiệu năng cao với awaitFrame
                        while (true) {
                            if (currentScrollSpeed < 0f && !canScrollBackward()) break
                            if (currentScrollSpeed > 0f && !canScrollForward()) break

                            // Đồng bộ nhịp V-Sync chuẩn 60Hz/120Hz của phần cứng màn hình
                            awaitFrame()

                            val currentFrameTime = System.nanoTime()
                            val deltaTime = (currentFrameTime - lastFrameTime) / 1_000_000_000f
                            lastFrameTime = currentFrameTime

                            // Tính quãng đường di chuyển tuyến tính mượt mà dựa theo thời gian thực (Time-delta)
                            val scrollAmount = currentScrollSpeed * deltaTime

                            if (scrollAmount != 0f) {
                                scrollBy(scrollAmount)

                                // TỐI ƯU HOÁN ĐỔI: Sau khi cuộn, tự động kiểm tra xem có cần tráo đổi item dưới ngón tay không
                                draggedIndex?.let { currentSource ->
                                    checkAndPerformSwap(currentSource)
                                }
                            }
                        }
                    } finally {
                        // Đảm bảo dọn dẹp sạch sẽ trạng thái khi coroutine bị hủy
                        currentScrollSpeed = 0f
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

    fun clearDragStateAfterAnimation(item: T?) {
        val fromIndex = draggedIndex
        val toIndex = pendingSwapTargetIndex

        if (fromIndex != null && toIndex != null && fromIndex != toIndex) {
            val currentIndex = firstVisibleItemIndex()
            val currentOffset = firstVisibleItemScrollOffset()

            // Lưu giữ trạng thái ẩn phần tử cũ
            lastDraggedItem = item

            onDropEnd(fromIndex, toIndex)

            requestScrollToItem(currentIndex, currentOffset)
            onListChanged(listData.toList())

            // Hủy tác vụ reset cũ (nếu có) trước khi tạo hàng đợi mới, chống leak luồng
            resetAnimationJob?.cancel()
            resetAnimationJob = scope.launch {
                delay(150)
                lastDraggedItem = null
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

// === CÁC HÀM REMEMBER GIỮ NGUYÊN ===
@Composable
fun <T> rememberGridDragDropState(
    listData: SnapshotStateList<T>,
    lazyGridState: LazyGridState,
    scope: CoroutineScope,
    dragDropPolicy: DragDropPolicy<T>,
    getDragDropContext: () -> DragDropContext = { DragDropContext() },
    ignoreIndices: IntRange = IntRange.EMPTY,
    onListChanged: (List<T>) -> Unit,
    onDropEnd: (Int, Int) -> Unit
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
            scrollBy = { delta -> lazyGridState.scrollBy(delta) },
            canScrollBackward = { lazyGridState.canScrollBackward },
            canScrollForward = { lazyGridState.canScrollForward },
            requestScrollToItem = { index, offset -> lazyGridState.requestScrollToItem(index, offset) },
            firstVisibleItemIndex = { lazyGridState.firstVisibleItemIndex },
            firstVisibleItemScrollOffset = { lazyGridState.firstVisibleItemScrollOffset },
            scope = scope,
            ignoreIndices = ignoreIndices,
            dragDropPolicy = dragDropPolicy,
            getDragDropContext = getDragDropContext,
            onListChanged = onListChanged,
            onDropEnd = onDropEnd
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
    onListChanged: (List<T>) -> Unit,
    onDropEnd: (Int, Int) -> Unit
): GenericDragDropState<T> {
    return remember(lazyListState, scope, listData, ignoreIndices, dragDropPolicy) {
        GenericDragDropState(
            listData = listData,
            getLayoutInfo = {
                object : DragDropLayoutInfo {
                    override val viewportSize: IntSize = lazyListState.layoutInfo.viewportSize
                    override val visibleItemsInfo: List<DragDropItemInfo> =
                        lazyListState.layoutInfo.visibleItemsInfo.map { listItem ->
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
            scrollBy = { delta -> lazyListState.scrollBy(delta) },
            canScrollBackward = { lazyListState.canScrollBackward },
            canScrollForward = { lazyListState.canScrollForward },
            requestScrollToItem = { index, offset -> lazyListState.requestScrollToItem(index, offset) },
            firstVisibleItemIndex = { lazyListState.firstVisibleItemIndex },
            firstVisibleItemScrollOffset = { lazyListState.firstVisibleItemScrollOffset },
            scope = scope,
            ignoreIndices = ignoreIndices,
            dragDropPolicy = dragDropPolicy,
            getDragDropContext = getDragDropContext,
            onListChanged = onListChanged,
            onDropEnd = onDropEnd
        )
    }
}