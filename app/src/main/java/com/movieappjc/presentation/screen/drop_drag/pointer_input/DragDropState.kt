package com.movieappjc.presentation.screen.drop_drag.pointer_input

import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DragDropState<T>(
    val getLayoutInfo: () -> DragDropLayoutInfo,
    private val scrollBy: suspend (Float) -> Float,
    private val canScrollBackward: () -> Boolean,
    private val canScrollForward: () -> Boolean,
    private val requestScrollToItem: (index: Int, offset: Int) -> Unit,
    private val firstVisibleItemIndex: () -> Int,
    private val firstVisibleItemScrollOffset: () -> Int,
    private val scope: CoroutineScope,
    private val ignoreIndices: Set<Int> = emptySet(),
    private val dragDropPolicy: DragDropPolicy<T>,
    private val getDragDropContext: () -> DragDropContext = { DragDropContext() },
    val getItemAt: (Int) -> T?,
    private val onDragStart: ((Int) -> Unit)? = null,
    private val performSwap: ((Int, Int) -> Unit)? = null,
    private val onDropEnd: ((Int, Int) -> Unit)? = null
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

    // Biến đồng bộ vị trí đích để bóng ma DragShadow biết chính xác nơi cần bay về
    var animationTargetIndex by mutableStateOf<Int?>(null)
        private set

    // Ghi nhớ chính bản thể đối tượng vừa thả tay để giữ ẩn UI nền công khai ra ngoài cho Modifier đọc
    var lastDraggedItem by mutableStateOf<T?>(null)
        private set

    private var autoScrollJob: Job? = null
    private var resetAnimationJob: Job? = null

    var dragStartAbsoluteOffset by mutableStateOf(Offset.Zero)
        private set

    private var lastCheckedFingerOffset = Offset.Zero
    private var currentScrollSpeed = 0f
    private var lastSwapTime = 0L // Dùng để giới hạn tần suất Swap khi đang cuộn tự động

    fun onDragStart(offset: Offset) {
        if (isReturningAnimation || lastDraggedItem != null) return

        val targetItem = findVisibleItemAtOffset(offset)
        if (targetItem != null && !ignoreIndices.contains(targetItem.index)) {
            // Lấy data thông qua hàm đọc gián tiếp
            val itemData = getItemAt(targetItem.index)

            // LUẬT 1: Kiểm tra xem bản thân ô này có ĐƯỢC PHÉP NẮM KÉO đi hay không
            if (itemData != null && !dragDropPolicy.canDrag(itemData, getDragDropContext())) {
                return // Trả về false -> Bẻ gãy luôn hành động bấm giữ, khóa cứng vị trí
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
            animationTargetIndex = null
            lastDraggedItem = null

            onDragStart?.invoke(targetItem.index)
        }
    }

    fun onDrag(dragAmount: Offset) {
        if (isReturningAnimation) return
        val source = draggedIndex ?: return

        fingerOffset += dragAmount

        // TỐI ƯU: Chỉ tính toán quét Layout hình học nếu ngón tay dịch chuyển một khoảng đủ lớn
        val distanceMoved = (fingerOffset - lastCheckedFingerOffset).getDistance()
        if (distanceMoved > 10f) {
            checkAndPerformSwap(source)
            lastCheckedFingerOffset = fingerOffset
        }

        checkForAutoScroll()
    }

    private fun checkAndPerformSwap(source: Int) {
        val targetItem = findVisibleItemAtOffset(fingerOffset)
        val target = targetItem?.index

        if (target != null && target != source) {
            if (target !in ignoreIndices && !ignoreIndices.contains(source)) {
                val targetItemData = getItemAt(target)

                if (targetItemData != null) {
                    val context = getDragDropContext()
                    val canSwapDuringDrag = dragDropPolicy.canSwapOnHover(targetItemData, context)

                    if (canSwapDuringDrag) {
                        // Lấy ra đối tượng thực tế đang bị kéo trước khi hoán đổi
                        val currentlyDraggingItem = getItemAt(source)

                        val currentIndex = firstVisibleItemIndex()
                        val currentOffset = firstVisibleItemScrollOffset()

                        // 1. Yêu cầu phía Composable tiến hành hoán đổi data thật
                        performSwap?.invoke(source, target)

                        // 2. KIỂM TRA BẢO VỆ: Chỉ cập nhật chỉ mục nếu Data thực sự đã đổi chỗ
                        if (getItemAt(target) == currentlyDraggingItem) {
                            draggedIndex = target
                            requestScrollToItem(currentIndex, currentOffset)
                        }
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

        // Tốc độ tối đa lý tưởng cho trải nghiệm người dùng
        val maxSpeedPxPerSecond = 1000f

        // Tính toán tốc độ mục tiêu dựa trên độ sâu ngón tay đi vào vùng nhạy cảm
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
            if (autoScrollJob == null || autoScrollJob?.isActive == false) {
                autoScrollJob = scope.launch {
                    try {
                        var lastFrameTime = System.nanoTime()

                        while (true) {
                            if (currentScrollSpeed < 0f && !canScrollBackward()) break
                            if (currentScrollSpeed > 0f && !canScrollForward()) break

                            // ĐỒNG BỘ V-SYNC PHẦN CỨNG: Chờ frame tiếp theo từ màn hình (60Hz / 120Hz)
                            awaitFrame()

                            val currentFrameTime = System.nanoTime()
                            val deltaTime = (currentFrameTime - lastFrameTime) / 1_000_000_000f
                            lastFrameTime = currentFrameTime

                            val scrollAmount = currentScrollSpeed * deltaTime

                            if (scrollAmount != 0f) {
                                // Cuộn danh sách nền trước
                                scrollBy(scrollAmount)

                                // TỐI ƯU CỐT LÕI: Giới hạn tần suất hoán đổi động khi đang tự động cuộn
                                val currentTime = System.currentTimeMillis()
                                if (currentTime - lastSwapTime > 80L) {
                                    draggedIndex?.let { currentSource ->
                                        checkAndPerformSwap(currentSource)
                                    }
                                    lastSwapTime = currentTime
                                }
                            }
                        }
                    } finally {
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

            // TRƯỜNG HỢP 1: Có kéo và lướt qua một ô hợp lệ khác
            if (target != null && target != source) {
                if (!ignoreIndices.contains(target) && !ignoreIndices.contains(source)) {
                    val targetItemData = getItemAt(target)
                    if (targetItemData != null) {
                        val context = getDragDropContext()
                        val canTargetBeDroppedOn = dragDropPolicy.canAcceptDrop(targetItemData, context)

                        if (canTargetBeDroppedOn) {
                            pendingSwapTargetIndex = target
                            animationTargetIndex = target
                        } else {
                            onDropEnd?.invoke(-1, -1)
                            pendingSwapTargetIndex = null
                            animationTargetIndex = source
                        }
                    }
                } else {
                    onDropEnd?.invoke(-1, -1)
                    animationTargetIndex = source
                }
                isReturningAnimation = true
            }
            // TRƯỜNG HỢP 2: Kéo rồi thả lại đúng vị trí cũ (hoặc lệch vài pixel do rung tay)
            else if (target == source) {
                onDropEnd?.invoke(-1, -1)
                animationTargetIndex = source
                isReturningAnimation = true
            }
            // === TRƯỜNG HỢP 3: KÉO RA NGOÀI BIÊN MÀN HÌNH RỒI THẢ TAY (target == null) ===
            else {
                onDropEnd?.invoke(-1, -1)
                pendingSwapTargetIndex = null

                // CHỐT HẠ: Chỉ định điểm đích bay về chính là ô nguồn ban đầu
                animationTargetIndex = source

                // BẬT CỜ hoạt họa để bàn giao quyền điều khiển sang cho DragShadow xử lý mượt mà
                isReturningAnimation = true
            }
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

            // Lưu giữ trạng thái ẩn phần tử cũ để tránh nhấp nháy UI nền
            lastDraggedItem = item
            onDropEnd?.invoke(fromIndex, toIndex)
            requestScrollToItem(currentIndex, currentOffset)

            // Hủy tác vụ reset cũ (nếu có) trước khi tạo hàng đợi mới, chống leak luồng
            resetAnimationJob?.cancel()
            resetAnimationJob = scope.launch {
                delay(150)
                lastDraggedItem = null
            }
        }

        // === ĐOẠN ĐẢM BẢO KHÔNG BỊ ĐƠ LIST VÀ HIỆN LẠI ITEM GỐC ===
        draggedIndex = null              // Trả về null để item gốc hiển thị lại ngay (alpha = 1f)
        pendingSwapTargetIndex = null
        animationTargetIndex = null
        fingerOffset = Offset.Zero
        initialTouchOffset = Offset.Zero
        draggedItemSize = IntSize.Zero
        isReturningAnimation = false

        stopAutoScroll()
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
    lazyGridState: LazyGridState,
    scope: CoroutineScope,
    dragDropPolicy: DragDropPolicy<T>,
    getDragDropContext: () -> DragDropContext = { DragDropContext() },
    ignoreIndices: Set<Int> = emptySet(),
    getItemAt: (Int) -> T?,
    onDragStart: ((Int) -> Unit)? = null,
    performSwap: ((Int, Int) -> Unit)? = null,
    onDropEnd: ((Int, Int) -> Unit)? = null
): DragDropState<T> {
    return remember(lazyGridState, scope, ignoreIndices, dragDropPolicy) {
        DragDropState(
            getLayoutInfo = {
                val beforePaddingX = lazyGridState.layoutInfo.beforeContentPadding
                object : DragDropLayoutInfo {
                    override val viewportSize: IntSize = lazyGridState.layoutInfo.viewportSize
                    override val visibleItemsInfo: List<DragDropItemInfo> = lazyGridState.layoutInfo.visibleItemsInfo.map { gridItem ->
                        object : DragDropItemInfo {
                            override val index: Int = gridItem.index
                            override val offset: IntOffset = IntOffset(
                                x = gridItem.offset.x + beforePaddingX,
                                y = gridItem.offset.y
                            )
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
            getItemAt = getItemAt,
            onDragStart = onDragStart,
            performSwap = performSwap,
            onDropEnd = onDropEnd
        )
    }
}

@Composable
fun <T> rememberListDragDropState(
    lazyListState: LazyListState,
    scope: CoroutineScope,
    dragDropPolicy: DragDropPolicy<T>,
    getDragDropContext: () -> DragDropContext = { DragDropContext() },
    ignoreIndices: Set<Int> = emptySet(),
    getItemAt: (Int) -> T?,
    onDragStart: ((Int) -> Unit)? = null,
    performSwap: ((Int, Int) -> Unit)? = null,
    onDropEnd: ((Int, Int) -> Unit)? = null
): DragDropState<T> {
    return remember(lazyListState, scope, ignoreIndices, dragDropPolicy) {
        DragDropState(
            getLayoutInfo = {
                val paddingTopOffset = lazyListState.layoutInfo.beforeContentPadding
                object : DragDropLayoutInfo {
                    override val viewportSize: IntSize = lazyListState.layoutInfo.viewportSize
                    override val visibleItemsInfo: List<DragDropItemInfo> =
                        lazyListState.layoutInfo.visibleItemsInfo.map { listItem ->
                            object : DragDropItemInfo {
                                override val index: Int = listItem.index
                                override val offset: IntOffset = IntOffset(
                                    x = 0,
                                    y = listItem.offset + paddingTopOffset
                                )
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
            getItemAt = getItemAt,
            onDragStart = onDragStart,
            performSwap = performSwap,
            onDropEnd = onDropEnd
        )
    }
}