package com.movieappjc.presentation.screen.drop_drag.pointer_input

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import kotlin.math.sqrt

@Composable
fun <T> DragShadow(
    dragDropState: GenericDragDropState<T>,
    listData: List<T>,
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
            activeItem = listData.getOrNull(globalIndex)
            activeItemSize = dragDropState.draggedItemSize
            lastValidStartOffset = dragDropState.dragStartAbsoluteOffset

            val initialOffset = dragDropState.fingerOffset - dragDropState.initialTouchOffset
            shadowOffset.snapTo(initialOffset)
        } else if (dragDropState.isReturningAnimation) {
            if (activeDraggedIndex != null && activeItem != null) {
                val layoutInfo = dragDropState.getLayoutInfo()

                // Xác định vị trí đích bay: Nếu có ô hoán đổi thì bay về vị trí mới của ô đó, nếu không quay về chỗ cũ
                val targetIndexToFly = dragDropState.animationTargetIndex ?: activeDraggedIndex
                val targetLayoutItem = layoutInfo.visibleItemsInfo.find { it.index == targetIndexToFly }

                val targetOffset = if (targetLayoutItem != null) {
                    Offset(targetLayoutItem.offset.x.toFloat(), targetLayoutItem.offset.y.toFloat())
                } else {
                    lastValidStartOffset
                }

                // Tính toán khoảng cách thực tế để tạo duration chạy động mượt mà
                val currentSnapshotOffset = shadowOffset.value
                val deltaX = targetOffset.x - currentSnapshotOffset.x
                val deltaY = targetOffset.y - currentSnapshotOffset.y
                val distance = sqrt((deltaX * deltaX + deltaY * deltaY).toDouble()).toFloat()

                // Gần bay nhanh (60ms), xa bay đầm mắt (tối đa 350ms)
                val calculatedDuration = (distance * 0.5f).toInt().coerceIn(10, 350)

                shadowOffset.animateTo(
                    targetValue = targetOffset,
                    animationSpec = tween(
                        durationMillis = calculatedDuration,
                        easing = FastOutSlowInEasing
                    )
                )

                // Hoàn thành hiệu ứng tịnh tiến, giải phóng toàn bộ flag kéo thả ngay
                dragDropState.clearDragStateAfterAnimation()
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
    if (itemToRender != null && activeItemSize != IntSize.Zero) {
        Box(
            modifier = Modifier
                .size(
                    width = with(LocalDensity.current) { activeItemSize.width.toDp() },
                    height = with(LocalDensity.current) { activeItemSize.height.toDp() }
                )
                .graphicsLayer {
                    translationX = shadowOffset.value.x
                    translationY = shadowOffset.value.y
                    scaleX = 1.0f
                    scaleY = 1.0f
                    alpha = 1.0f
                    shadowElevation = 0f
                }
        ) {
            itemContent(itemToRender)
        }
    }
}