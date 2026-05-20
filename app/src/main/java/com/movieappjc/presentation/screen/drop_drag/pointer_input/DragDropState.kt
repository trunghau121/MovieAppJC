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
 * Manages the core state machine, geometric coordinates, and business logic validation
 * required to execute the custom drag-and-drop mechanics.
 *
 * @param T The generic data type of the items managed inside the drag-and-drop container.
 * @param getLayoutInfo Lambda that fetches the current layout geometry, viewport measurements,
 * and metadata of all currently visible items in the scrollable container.
 * @param scrollBy Suspended function invoked to programmatically scroll the container viewport
 * by a specific pixel delta (positive for forward/downward, negative for backward/upward).
 * @param canScrollBackward Lambda returning whether the container has scrollable content remaining
 * in the backward/upward direction (used to bound automated edge scrolling).
 * @param canScrollForward Lambda returning whether the container has scrollable content remaining
 * in the forward/downward direction (used to bound automated edge scrolling).
 * @param requestScrollToItem Callback function used to instantly snap the list's viewport position
 * to a specific item index and exact pixel offset padding.
 * @param firstVisibleItemIndex Lambda that queries and returns the positional data index of the first
 * item currently visible at the very top/edge of the layout viewport.
 * @param firstVisibleItemScrollOffset Lambda that returns the exact scroll offset pixel delta of the first visible
 * item relative to the start edge of the container viewport.
 * @param scope The coroutine lifecycle scope tied to the UI composition, used to safely
 * launch asynchronous automated scrolling tasks and delayed state cleanups.
 * @param ignoreIndices A collection of data indices that are immune to drag-and-drop actions
 * (e.g., permanent section section headers, banners, or divider items).
 * @param dragDropPolicy The structural business policy implementation defining validation rules for
 * permissions (canDrag, canAcceptDrop, canSwapOnHover).
 * @param getItemAt Lambda that retrieves the actual backing data item model from the database/list
 * collection matching the specified layout index.
 * @param onDragStart Optional callback event triggered immediately when an item is successfully locked
 * by a long press gesture, carrying the source index.
 * @param performSwap The mutation lambda responsible for swapping the positions of two elements
 * directly inside the reactive backing data collection (fromIndex, toIndex).
 * @param onDropEnd Callback triggered when the entire drag session completes or cancels.
 * Emits success indices (fromIndex, toIndex) or cancellation error codes (-1, -1).
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
    val getItemAt: (Int) -> T?,
    private val onDragStart: ((Int) -> Unit)? = null,
    private val performSwap: ((Int, Int) -> Unit)? = null,
    private val onDropEnd: ((Int, Int) -> Unit)? = null
) {
    // The current index of the item being dragged (null means no active drag session)
    var draggedIndex by mutableStateOf<Int?>(null)
        private set

    // The absolute continuous screen coordinates of the user's finger pointer
    var fingerOffset by mutableStateOf(Offset.Zero)
        private set

    // The inner distance offset between the top-left edge of the item card and the exact touch spot
    var initialTouchOffset by mutableStateOf(Offset.Zero)
        private set

    // Holds the pixel size dimensions (width, height) of the item captured at drag start
    var draggedItemSize by mutableStateOf(IntSize.Zero)
        private set

    // A safety flag to block user input interactions while the shadow is flying back to its final slot
    var isReturningAnimation by mutableStateOf(false)
        private set

    // Holds the index destination where the data swap will finalize upon releasing the item
    var pendingSwapTargetIndex by mutableStateOf<Int?>(null)
        private set

    // Anchor index marker telling DragShadow where to slide/animate to when the gesture ends
    var animationTargetIndex by mutableStateOf<Int?>(null)
        private set

    // Holds a reference to the data item model to hide layout placeholders and stop visual UI blinking
    var lastDraggedItem by mutableStateOf<T?>(null)
        private set

    // Asynchronous coroutine job wrapper managing continuous viewport scrolling at edge regions
    private var autoScrollJob: Job? = null

    // Scheduled delay routing task used to clean transient memory references safely
    private var resetAnimationJob: Job? = null

    // Holds the stationary start point coordinates of the selected item inside the parent container view
    var dragStartAbsoluteOffset by mutableStateOf(Offset.Zero)
        private set

    // Tracks historical vector offsets to throttle spatial recalculations until the finger drifts past a threshold
    private var lastCheckedFingerOffset = Offset.Zero

    // The current pixel velocity per second used during programmatic automated scroll cycles
    private var currentScrollSpeed = 0f

    // Timestamp monitor used to throttle rapid index shifts during high-speed auto scrolling
    private var lastSwapTime = 0L

    fun onDragStart(offset: Offset) {
        // Guard clause: Abort initialization if an animation is active or data is still cleaning up
        if (isReturningAnimation || lastDraggedItem != null) return

        // Find which visible item layout sits beneath the user's initial touch coordinate points
        val targetItem = findVisibleItemAtOffset(offset)

        // Ensure the item exists and is not declared immune (e.g., structural section titles or dividers)
        if (targetItem != null && !ignoreIndices.contains(targetItem.index)) {
            val itemData = getItemAt(targetItem.index)

            // Policy Rule: Consult business logic validation rules to check if this item is draggable
            if (itemData != null && !dragDropPolicy.canDrag(itemData, targetItem.index)) {
                return // Lock the item in place and abort the session
            }

            // Lock structural coordinates and index values into the state machine
            draggedIndex = targetItem.index
            fingerOffset = offset
            lastCheckedFingerOffset = offset // Synchronize drift checkpoint
            draggedItemSize = targetItem.size

            // Calculate the inner relative delta padding inside the individual item block
            initialTouchOffset = Offset(
                x = offset.x - targetItem.offset.x,
                y = offset.y - targetItem.offset.y
            )

            // Store original static bounds
            dragStartAbsoluteOffset = Offset(targetItem.offset.x.toFloat(), targetItem.offset.y.toFloat())
            pendingSwapTargetIndex = null
            animationTargetIndex = null
            lastDraggedItem = null

            // Notify outer architectural listeners that a drag operation successfully launched
            onDragStart?.invoke(targetItem.index)
        }
    }

    fun onDrag(dragAmount: Offset) {
        // Block processing if the UI is running a return translation routine
        if (isReturningAnimation) return
        val source = draggedIndex ?: return

        // Add the incremental finger drift vectors to update global touch positions
        fingerOffset += dragAmount

        // Performance Check: Only run complex geometric intersections if the finger moves beyond 10 pixels
        val distanceMoved = (fingerOffset - lastCheckedFingerOffset).getDistance()
        if (distanceMoved > 10f) {
            checkAndPerformSwap(source)
            lastCheckedFingerOffset = fingerOffset // Update checkpoint bounds
        }

        // Evaluate if the finger is hovering inside boundary scroll zones
        checkForAutoScroll()
    }

    private fun checkAndPerformSwap(source: Int) {
        // Query what layout item is currently hovering beneath the global finger tracking coords
        val targetItem = findVisibleItemAtOffset(fingerOffset)
        val target = targetItem?.index

        // Confirm we found a target slot, and it's completely different from the current position
        if (target != null && target != source) {
            if (target !in ignoreIndices && !ignoreIndices.contains(source)) {
                val targetItemData = getItemAt(target)

                if (targetItemData != null) {
                    // Policy Rule: Check if the hover target accepts live position swapping
                    val canSwapDuringDrag = dragDropPolicy.canSwapOnHover(targetItemData, target)

                    if (canSwapDuringDrag) {
                        val currentlyDraggingItem = getItemAt(source)

                        // Save current scroll view alignment metrics to prevent layout shifts during item swapping
                        val currentIndex = firstVisibleItemIndex()
                        val currentOffset = firstVisibleItemScrollOffset()

                        // 1. Invoke the data mutation lambda (swaps positions inside the database/list state)
                        performSwap?.invoke(source, target)

                        // 2. Safety check: Update internal tracking index only if backing collection swap succeeds
                        if (getItemAt(target) == currentlyDraggingItem) {
                            draggedIndex = target
                            // Snap lazy viewport metrics back to preserve stable view focus
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
        val activationZone = 120f // The hot-zone thickness (in pixels) at the top and bottom screen borders
        val fingerY = fingerOffset.y

        val maxSpeedPxPerSecond = 1000f

        // Calculate velocity linearly depending on how deeply the finger is pressed into the hot-zones
        val targetSpeed = when {
            fingerY in 0f..<activationZone -> {
                // Top zone: Scroll backwards (Negative value)
                -(1.0f - (fingerY / activationZone)) * maxSpeedPxPerSecond
            }
            fingerY > (containerHeight - activationZone) && fingerY <= containerHeight -> {
                // Bottom zone: Scroll forwards (Positive value)
                ((fingerY - (containerHeight - activationZone)) / activationZone) * maxSpeedPxPerSecond
            }
            else -> 0f // Neutral zone: Stop auto-scroll
        }

        currentScrollSpeed = targetSpeed

        if (currentScrollSpeed != 0f) {
            // Launch auto scroll loop routine if not already running
            if (autoScrollJob == null || autoScrollJob?.isActive == false) {
                autoScrollJob = scope.launch {
                    try {
                        var lastFrameTime = System.nanoTime()

                        while (true) {
                            // Break loop if limits are reached
                            if (currentScrollSpeed < 0f && !canScrollBackward()) break
                            if (currentScrollSpeed > 0f && !canScrollForward()) break

                            // V-SYNC SYNCHRONIZATION: Pause loop execution until the display hardware triggers its next frame refresh cycle
                            awaitFrame()

                            val currentFrameTime = System.nanoTime()
                            // Calculate precise delta time fraction (seconds elapsed since last frame)
                            val deltaTime = (currentFrameTime - lastFrameTime) / 1_000_000_000f
                            lastFrameTime = currentFrameTime

                            // Calculate pixel offset slice to execute for this frame
                            val scrollAmount = currentScrollSpeed * deltaTime

                            if (scrollAmount != 0f) {
                                // Execute programmatic scroll movement
                                scrollBy(scrollAmount)

                                // Performance Throttle: Limit dynamic index swapping to every 80ms during active auto-scroll
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

            // CASE 1: Finger released over an index distinct from where the item currently floats
            if (target != null && target != source) {
                if (!ignoreIndices.contains(target) && !ignoreIndices.contains(source)) {
                    val targetItemData = getItemAt(target)
                    if (targetItemData != null) {
                        // Policy Rule: Verify if the current landing target accepts a drop placement
                        val canTargetBeDroppedOn = dragDropPolicy.canAcceptDrop(targetItemData, target)

                        if (canTargetBeDroppedOn) {
                            pendingSwapTargetIndex = target
                            animationTargetIndex = target
                        } else {
                            // Policy failure: Send error codes (-1, -1) and route shadow back to source index
                            onDropEnd?.invoke(-1, -1)
                            pendingSwapTargetIndex = null
                            animationTargetIndex = source
                        }
                    }
                } else {
                    onDropEnd?.invoke(-1, -1)
                    animationTargetIndex = source
                }
                isReturningAnimation = true // Lock interaction flags and notify DragShadow to begin return animation
            }
            // CASE 2: Finger released on the exact same spot (or minor tremor offset variance)
            else if (target == source) {
                onDropEnd?.invoke(-1, -1)
                animationTargetIndex = source
                isReturningAnimation = true
            }
            // CASE 3: Dropped outside visible layout boundary boxes (target == null)
            else {
                onDropEnd?.invoke(-1, -1)
                pendingSwapTargetIndex = null
                animationTargetIndex = source // Target the visual anchor back to source
                isReturningAnimation = true
            }
        } else {
            clearDragStateAfterAnimation(null)
        }
        stopAutoScroll() // Clear auto scroll routines
    }

    fun clearDragStateAfterAnimation(item: T?) {
        val fromIndex = draggedIndex
        val toIndex = pendingSwapTargetIndex

        // 1. If an actual valid index translation took place (successful drop swap)
        if (fromIndex != null && toIndex != null && fromIndex != toIndex) {
            val currentIndex = firstVisibleItemIndex()
            val currentOffset = firstVisibleItemScrollOffset()

            lastDraggedItem = item
            // Emit success indices notification to outer viewmodel listeners
            onDropEnd?.invoke(fromIndex, toIndex)
            requestScrollToItem(currentIndex, currentOffset)

            // Schedule a brief delay to hide placeholders cleanly after lazy list animations settle
            resetAnimationJob?.cancel()
            resetAnimationJob = scope.launch {
                delay(150)
                lastDraggedItem = null // Restore structural layout visibility
            }
        }
        // 2. If gesture cancelled or item dropped back into its original slot without changing positions
        else {
            // Force error cancellation codes out to ViewModels to safely reset data transaction flows
            onDropEnd?.invoke(-1, -1)
            lastDraggedItem = null // Remove reference hooks instantly
        }

        // === STATE PIPELINE RESET ===
        // Clear all tracking vectors, unlock flags, and return container variables to defaults
        draggedIndex = null
        pendingSwapTargetIndex = null
        animationTargetIndex = null
        fingerOffset = Offset.Zero
        initialTouchOffset = Offset.Zero
        draggedItemSize = IntSize.Zero
        isReturningAnimation = false // CRITICAL: Frees input lock system to prevent app freezing bugs

        stopAutoScroll()
    }

    fun stopAutoScroll() {
        autoScrollJob?.cancel()
        autoScrollJob = null
    }

    private fun findVisibleItemAtOffset(screenOffset: Offset): DragDropItemInfo? {
        val visibleItems = getLayoutInfo().visibleItemsInfo
        // Find which layout bounds contain the finger pointer coordinates
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
            getItemAt = getItemAt,
            onDragStart = onDragStart,
            performSwap = performSwap,
            onDropEnd = onDropEnd
        )
    }
}