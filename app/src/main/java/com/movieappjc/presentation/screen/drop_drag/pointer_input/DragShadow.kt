package com.movieappjc.presentation.screen.drop_drag.pointer_input

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.IntSize
import kotlin.math.sqrt

@Composable
fun <T> DragShadow(
    dragDropState: DragDropState<T>,
    getItemAt: (Int) -> T?,
    itemContent: @Composable (item: T) -> Unit
) {
    // Internal cache states maintaining shadow component visuals isolated from global list updates
    var activeDraggedIndex by remember { mutableStateOf<Int?>(null) }
    var activeItem by remember { mutableStateOf<T?>(null) }
    var activeItemSize by remember { mutableStateOf(IntSize.Zero) }
    var lastValidStartOffset by remember { mutableStateOf(Offset.Zero) }

    // Animatable state tracking real-time layout rendering offsets
    val shadowOffset = remember { Animatable(Offset.Zero, Offset.VectorConverter) }
    // Calculates finger touch vector subtracted by relative point constraints
    val currentFingerOffset = dragDropState.fingerOffset - dragDropState.initialTouchOffset

    LaunchedEffect(dragDropState.draggedIndex, dragDropState.isReturningAnimation) {
        val globalIndex = dragDropState.draggedIndex

        // PIPELINE 1: User initializes a valid long press gesture and starts dragging
        if (globalIndex != null && !dragDropState.isReturningAnimation) {
            activeDraggedIndex = globalIndex
            activeItem = getItemAt(globalIndex) // Capture data blueprint
            val layoutInfo = dragDropState.getLayoutInfo()
            val itemInfo = layoutInfo.visibleItemsInfo.find { it.index == globalIndex }
            if (itemInfo != null) {
                activeItemSize = itemInfo.size
                val startOffset = Offset(itemInfo.offset.x.toFloat(), itemInfo.offset.y.toFloat())
                lastValidStartOffset = startOffset
                // Instantly snap shadow vector position to current touch position
                shadowOffset.snapTo(dragDropState.fingerOffset - dragDropState.initialTouchOffset)
            }
        }
        // PIPELINE 2: Gesture cancelled abruptly or finger released right at press location without moving
        else if (globalIndex == null && !dragDropState.isReturningAnimation) {
            // CRITICAL ADDITION: Immediately clean local variables to hide the component and prevent frozen ghost shadows
            activeDraggedIndex = null
            activeItem = null             // Forcing this to null collapses rendering block conditions below
            activeItemSize = IntSize.Zero // Clear layout bounds
        }

        // PIPELINE 3: HANDLES RETURNING INTERPOLATION WHEN GESTURE IS VALIDLY RELEASED
        if (dragDropState.isReturningAnimation && activeDraggedIndex != null) {
            val layoutInfo = dragDropState.getLayoutInfo()
            val targetIndex = dragDropState.animationTargetIndex

            // 1. Verify if the destination target slot layout is currently visible within the screen viewport
            val targetItemInfo = layoutInfo.visibleItemsInfo.find { it.index == targetIndex }

            val targetOffset = if (targetItemInfo != null) {
                // Target slot is visible: Route the visual shadow coordinates directly onto its layout block offset
                Offset(targetItemInfo.offset.x.toFloat(), targetItemInfo.offset.y.toFloat())
            } else {
                // COMPENSATION TRACKING FOR OUT-OF-BOUNDS OFFSCREEN TARGETS
                if (targetIndex != null && layoutInfo.visibleItemsInfo.isNotEmpty()) {
                    val firstVisibleIndex = layoutInfo.visibleItemsInfo.first().index
                    val lastVisibleIndex = layoutInfo.visibleItemsInfo.last().index

                    if (targetIndex < firstVisibleIndex) {
                        // Destination slot is hidden ABOVE view: Project shadow upwards offscreen
                        Offset(lastValidStartOffset.x, -activeItemSize.height.toFloat() * 1.5f)
                    } else if (targetIndex > lastVisibleIndex) {
                        // Destination slot is hidden BELOW view: Project shadow down below the screen limits
                        Offset(lastValidStartOffset.x, layoutInfo.viewportSize.height.toFloat() + activeItemSize.height.toFloat())
                    } else {
                        lastValidStartOffset
                    }
                } else {
                    lastValidStartOffset
                }
            }

            // Calculate Euclidean travel distance using Pythagorean Theorem (sqrt(dx^2 + dy^2))
            val currentX = shadowOffset.value.x
            val currentY = shadowOffset.value.y
            val deltaX = targetOffset.x - currentX
            val deltaY = targetOffset.y - currentY
            val distance = sqrt(deltaX * deltaX + deltaY * deltaY)

            // Dynamically scale translation duration based on travel distance, clamped safely between 40ms and 400ms
            val calculatedDuration = (distance * 0.4f).toInt().coerceIn(40, 400)
            shadowOffset.animateTo(
                targetValue = targetOffset,
                animationSpec = tween(durationMillis = calculatedDuration, easing = FastOutSlowInEasing)
            )

            // Interpolation complete: Force clear global coordinate frameworks inside DragDropState
            dragDropState.clearDragStateAfterAnimation(activeItem)

            // Wipe internal caches to finalize unmounting the floating shadow layer component
            activeDraggedIndex = null
            activeItem = null
            activeItemSize = IntSize.Zero
        }
    }

    // While dragging actively (and not returning), synchronize shadow position directly with real-time finger drifting
    if (!dragDropState.isReturningAnimation && dragDropState.draggedIndex != null) {
        LaunchedEffect(currentFingerOffset) {
            shadowOffset.snapTo(currentFingerOffset)
        }
    }

    val itemToRender = activeItem
    val shadowSizeDp = remember(activeItemSize) { activeItemSize }

    // Render structural shadow block UI only if the cache item state is validly populated
    if (itemToRender != null && activeItemSize != IntSize.Zero) {
        Box(
            modifier = Modifier
                .layout { measurable, constraints ->
                    // Override layout constraints to lock canvas measurements to match original item dimensions
                    val placeable = measurable.measure(
                        constraints.copy(
                            minWidth = shadowSizeDp.width,
                            maxWidth = shadowSizeDp.width,
                            minHeight = shadowSizeDp.height,
                            maxHeight = shadowSizeDp.height
                        )
                    )
                    layout(placeable.width, placeable.height) {
                        placeable.placeWithLayer(0, 0)
                    }
                }
                .graphicsLayer {
                    // Translate absolute spatial floating location coords on screen canvas layers
                    translationX = shadowOffset.value.x
                    translationY = shadowOffset.value.y
                }
        ) {
            // Render user custom item UI block
            itemContent(itemToRender)
        }
    }
}