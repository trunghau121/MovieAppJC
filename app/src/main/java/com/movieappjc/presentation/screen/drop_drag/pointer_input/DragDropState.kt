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

/**
 * Maintains the interactive state, coordinates, hardware-synchronized loops,
 * and constraint validations for the custom drag-and-drop mechanics.
 *
 * @param getLayoutInfo Lambda that fetches the current layout geometry, viewport measurements, and metadata of all currently visible items in the scrollable container.
 * @param scrollBy Suspended function invoked to scroll the container programmatically by a specific pixel delta (positive for forward/downward, negative for backward/upward).
 * @param canScrollBackward Lambda returning whether the container has scrollable content remaining in the backward direction.
 * @param canScrollForward Lambda returning whether the container has scrollable content remaining in the forward direction.
 * @param requestScrollToItem Command callback used to snap or scroll the lazy container immediately to a specific index and pixel offset.
 * @param firstVisibleItemIndex Lambda that returns the index of the first item currently visible at the top/start of the viewport.
 * @param firstVisibleItemScrollOffset Lambda that returns the precise pixel scroll offset of the first visible item relative to the viewport edge.
 * @param scope The asynchronous CoroutineScope used to manage concurrent lifecycle tasks, such as handling auto-scrolling loops.
 * @param ignoreIndices A collection of indices (e.g., sticky headers, section dividers) that must remain fixed and immune to drag-and-drop operations.
 * @param dragDropPolicy The business logic strategy handler that validates interaction constraints (e.g., whether an item can be dragged or swapped).
 * @param getDragDropContext Lambda that evaluates and updates structural data contexts (like user authorization or edit modes) during rule evaluations.
 * @param getItemAt Data look-up function that retrieves the underlying model item instance matching a specific layout index.
 * @param onDragStart Event callback triggered immediately when a valid long-press gesture selects and locks onto an item.
 * @param performSwap Structural mutation callback triggered to swap data item positions inside the source collection during an active hover.
 * @param onDropEnd Finalization callback triggered after the drag gesture releases and any corresponding return animations conclude.
 */
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
    // The layout position index of the item currently being actively dragged, or null if no gesture is occurring.
    var draggedIndex by mutableStateOf<Int?>(null)
        private set

    // The real-time absolute coordinate offset tracking the user's touch pointer relative to the layout container bounds.
    var fingerOffset by mutableStateOf(Offset.Zero)
        private set

    // The specific inner coordinate delta between the user's exact initial touch position and the top-left boundary of the item layout.
    var initialTouchOffset by mutableStateOf(Offset.Zero)
        private set

    // The structural dimension boundaries (width and height) captured from the item composition when the drag started.
    var draggedItemSize by mutableStateOf(IntSize.Zero)
        private set

    // A safety flag indicating whether the floating shadow is currently animating back to its settled destination slot after being released.
    var isReturningAnimation by mutableStateOf(false)
        private set

    // The designated destination index waiting to finalize its data swap once the drag gesture fully releases.
    var pendingSwapTargetIndex by mutableStateOf<Int?>(null)
        private set

    // Synchronization coordinate layout marker used to guide the DragShadow visually back to its target snapping point during structural updates.
    var animationTargetIndex by mutableStateOf<Int?>(null)
        private set

    // Retains a strong reference to the model data of the dropped item to hide original content placeholders and eliminate visual flashing during lazy layout updates.
    var lastDraggedItem by mutableStateOf<T?>(null)
        private set

    // The structural Coroutine task wrapper governing the active automated frame layout scrolling loop at high-speed hotzones.
    private var autoScrollJob: Job? = null

    // The scheduled delayed routine managing cleanup tasks to safely clear transient shadow references and restore component visibility.
    private var resetAnimationJob: Job? = null

    // The initial stationary starting pixel offset of the active item relative to the top-left edge of the parent parent parent container view.
    var dragStartAbsoluteOffset by mutableStateOf(Offset.Zero)
        private set

    // Tracking variable to record the previous position vector, allowing calculation of finger drift thresholds before re-triggering geometry calculations.
    private var lastCheckedFingerOffset = Offset.Zero

    // The evaluated rate of movement pixels per second applied directly to programmatic viewport scrolling during edge-detection activations.
    private var currentScrollSpeed = 0f

    // Timestamp counter used to throttle rapid index shifting logic sequences during high-velocity container scrolling.
    private var lastSwapTime = 0L

    fun onDragStart(offset: Offset) {
        if (isReturningAnimation || lastDraggedItem != null) return

        val targetItem = findVisibleItemAtOffset(offset)
        if (targetItem != null && !ignoreIndices.contains(targetItem.index)) {
            // Retrieve actual data through the lazy reader function
            val itemData = getItemAt(targetItem.index)

            // RULE 1: Verify if this specific item is allowed to be dragged
            if (itemData != null && !dragDropPolicy.canDrag(itemData, getDragDropContext())) {
                return // Abort the gesture and lock the item in place
            }

            draggedIndex = targetItem.index
            fingerOffset = offset
            lastCheckedFingerOffset = offset // Reset baseline offset tracker to prevent structural drift
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

        // OPTIMIZATION: Only parse spatial layout coordinates if the drag distance exceeds a small threshold
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
                        // Capture the item being dragged prior to the layout mutation
                        val currentlyDraggingItem = getItemAt(source)

                        val currentIndex = firstVisibleItemIndex()
                        val currentOffset = firstVisibleItemScrollOffset()

                        // 1. Request the backing mutable list/state collection to update positions
                        performSwap?.invoke(source, target)

                        // 2. SAFETY CHECK: Only update tracked index if the underlying data swapping succeeded
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

        // Ideal peak speed for optimal user experience
        val maxSpeedPxPerSecond = 1000f

        // Calculate velocity based on how deep the pointer is pressed into the upper/lower hot zones
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

                            // HARDWARE V-SYNC SYNCHRONIZATION: Wait for the next screen refresh cycle (60Hz / 120Hz)
                            awaitFrame()

                            val currentFrameTime = System.nanoTime()
                            val deltaTime = (currentFrameTime - lastFrameTime) / 1_000_000_000f
                            lastFrameTime = currentFrameTime

                            val scrollAmount = currentScrollSpeed * deltaTime

                            if (scrollAmount != 0f) {
                                // Scroll the backing container layout
                                scrollBy(scrollAmount)

                                // CORE OPTIMIZATION: Throttling dynamic swap routines during high-speed auto-scrolls
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

            // CASE 1: Gesture ends over a different, valid layout target
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
            // CASE 2: Gesture released on the exact original target spot (or slightly shifted due to finger tremor)
            else if (target == source) {
                onDropEnd?.invoke(-1, -1)
                animationTargetIndex = source
                isReturningAnimation = true
            }
            // CASE 3: Dragged out of bounds / empty space (target == null)
            else {
                onDropEnd?.invoke(-1, -1)
                pendingSwapTargetIndex = null

                // Force the visual anchor to fall back directly onto the original source item
                animationTargetIndex = source

                // Trigger return animations and delegate state handling over to DragShadow
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

            // Keep original placeholder element hidden briefly to prevent rapid visual layout flickering
            lastDraggedItem = item
            onDropEnd?.invoke(fromIndex, toIndex)
            requestScrollToItem(currentIndex, currentOffset)

            // Cancel any pending animations before launching a new routine to prevent thread leaks
            resetAnimationJob?.cancel()
            resetAnimationJob = scope.launch {
                delay(150)
                lastDraggedItem = null
            }
        }

        // === RESET PIPELINE: Restores list interactions and unhides underlying item layouts ===
        draggedIndex = null // Re-exposes the source item by clearing tracking flags (alpha sets to 1f)
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