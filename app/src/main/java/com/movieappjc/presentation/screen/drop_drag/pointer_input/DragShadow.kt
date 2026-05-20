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
    var activeDraggedIndex by remember { mutableStateOf<Int?>(null) }
    var activeItem by remember { mutableStateOf<T?>(null) }
    var activeItemSize by remember { mutableStateOf(IntSize.Zero) }
    var lastValidStartOffset by remember { mutableStateOf(Offset.Zero) }

    val shadowOffset = remember { Animatable(Offset.Zero, Offset.VectorConverter) }
    val currentFingerOffset = dragDropState.fingerOffset - dragDropState.initialTouchOffset

    LaunchedEffect(dragDropState.draggedIndex, dragDropState.isReturningAnimation) {
        val globalIndex = dragDropState.draggedIndex

        if (globalIndex != null && !dragDropState.isReturningAnimation) {
            activeDraggedIndex = globalIndex
            activeItem = getItemAt(globalIndex)
            val layoutInfo = dragDropState.getLayoutInfo()
            val itemInfo = layoutInfo.visibleItemsInfo.find { it.index == globalIndex }
            if (itemInfo != null) {
                activeItemSize = itemInfo.size
                val startOffset = Offset(itemInfo.offset.x.toFloat(), itemInfo.offset.y.toFloat())
                lastValidStartOffset = startOffset
                shadowOffset.snapTo(dragDropState.fingerOffset - dragDropState.initialTouchOffset)
            }
        }

        // HANDLES RETURNING INTERPOLATION WHEN GESTURE IS RELEASED
        if (dragDropState.isReturningAnimation && activeDraggedIndex != null) {
            val layoutInfo = dragDropState.getLayoutInfo()
            val targetIndex = dragDropState.animationTargetIndex

            // 1. Verify if the target destination layout slot is currently visible within the viewport
            val targetItemInfo = layoutInfo.visibleItemsInfo.find { it.index == targetIndex }

            val targetOffset = if (targetItemInfo != null) {
                // Target found: route the visual shadow right onto its absolute screen coordinates
                Offset(targetItemInfo.offset.x.toFloat(), targetItemInfo.offset.y.toFloat())
            } else {
                // COMPENSATION TRACKING FOR OUT-OF-BOUNDS OFFSCREEN TARGETS
                if (targetIndex != null && layoutInfo.visibleItemsInfo.isNotEmpty()) {
                    val firstVisibleIndex = layoutInfo.visibleItemsInfo.first().index
                    val lastVisibleIndex = layoutInfo.visibleItemsInfo.last().index

                    if (targetIndex < firstVisibleIndex) {
                        // Target item is hidden ABOVE the viewport -> Project shadow out beyond the upper edge
                        Offset(lastValidStartOffset.x, -activeItemSize.height.toFloat() * 1.5f)
                    } else if (targetIndex > lastVisibleIndex) {
                        // Target item is hidden BELOW the viewport -> Project shadow down past the screen threshold
                        Offset(lastValidStartOffset.x, layoutInfo.viewportSize.height.toFloat() + activeItemSize.height.toFloat())
                    } else {
                        lastValidStartOffset
                    }
                } else {
                    lastValidStartOffset
                }
            }

            // Calculate standard Euclidean distance (Pythagorean theorem) to interpolate smooth travel velocity
            val currentX = shadowOffset.value.x
            val currentY = shadowOffset.value.y
            val deltaX = targetOffset.x - currentX
            val deltaY = targetOffset.y - currentY
            val distance = sqrt(deltaX * deltaX + deltaY * deltaY)

            // Dynamically scale duration based on target distance, clamped cleanly between 40ms and 400ms
            val calculatedDuration = (distance * 0.4f).toInt().coerceIn(40, 400)
            shadowOffset.animateTo(
                targetValue = targetOffset,
                animationSpec = tween(durationMillis = calculatedDuration, easing = FastOutSlowInEasing)
            )

            dragDropState.clearDragStateAfterAnimation(activeItem)
            activeDraggedIndex = null
            activeItem = null
            activeItemSize = IntSize.Zero
        }
    }

    if (!dragDropState.isReturningAnimation && dragDropState.draggedIndex != null) {
        LaunchedEffect(currentFingerOffset) {
            shadowOffset.snapTo(currentFingerOffset)
        }
    }

    val itemToRender = activeItem
    val shadowSizeDp = remember(activeItemSize) { activeItemSize }

    if (itemToRender != null && activeItemSize != IntSize.Zero) {
        Box(
            modifier = Modifier
                .layout { measurable, constraints ->
                    // Force constraints to match the size of the original picked item layout
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
                    translationX = shadowOffset.value.x
                    translationY = shadowOffset.value.y
                }
        ) {
            itemContent(itemToRender)
        }
    }
}