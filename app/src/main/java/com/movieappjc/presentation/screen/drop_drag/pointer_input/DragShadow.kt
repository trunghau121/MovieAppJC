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
            activeItemSize = dragDropState.draggedItemSize
            lastValidStartOffset = dragDropState.dragStartAbsoluteOffset

            val initialOffset = dragDropState.fingerOffset - dragDropState.initialTouchOffset
            shadowOffset.snapTo(initialOffset)
        } else if (dragDropState.isReturningAnimation) {
            if (activeDraggedIndex != null && activeItem != null) {
                val layoutInfo = dragDropState.getLayoutInfo()

                val targetIndexToFly = dragDropState.animationTargetIndex ?: activeDraggedIndex
                val targetLayoutItem = layoutInfo.visibleItemsInfo.find { it.index == targetIndexToFly }

                val targetOffset = if (targetLayoutItem != null) {
                    Offset(targetLayoutItem.offset.x.toFloat(), targetLayoutItem.offset.y.toFloat())
                } else {
                    lastValidStartOffset
                }

                val currentSnapshotOffset = shadowOffset.value
                val deltaX = targetOffset.x - currentSnapshotOffset.x
                val deltaY = targetOffset.y - currentSnapshotOffset.y
                val distance = sqrt((deltaX * deltaX + deltaY * deltaY).toDouble()).toFloat()

                // Quy đổi tỷ lệ thời gian động mượt mà khi bóng ma bay về vị trí cũ/mới
                val calculatedDuration = (distance * 0.4f).toInt().coerceIn(40, 400)

                shadowOffset.animateTo(
                    targetValue = targetOffset,
                    animationSpec = tween(
                        durationMillis = calculatedDuration,
                        easing = FastOutSlowInEasing
                    )
                )

                // Kích hoạt dọn dẹp trạng thái và phát sự kiện thả tay thành công ra ngoài UI
                dragDropState.clearDragStateAfterAnimation(activeItem)

                activeDraggedIndex = null
                activeItem = null
                activeItemSize = IntSize.Zero
            }
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