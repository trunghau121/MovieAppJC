package com.movieappjc.presentation.screen.drop_drag.pointer_input

import android.util.Log
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

        // ĐOẠN XỬ LÝ HOẠT HỌA BAY VỀ KHI BUÔNG TAY
        if (dragDropState.isReturningAnimation && activeDraggedIndex != null) {
            val layoutInfo = dragDropState.getLayoutInfo()
            val targetIndex = dragDropState.animationTargetIndex

            // 1. Tìm xem ô đích hiện có đang hiển thị trên màn hình không
            val targetItemInfo = layoutInfo.visibleItemsInfo.find { it.index == targetIndex }

            val targetOffset = if (targetItemInfo != null) {
                // Nếu tìm thấy, bay về đúng vị trí thực tế hiện tại của nó
                Offset(targetItemInfo.offset.x.toFloat(), targetItemInfo.offset.y.toFloat())
            } else {
                // THUẬT TOÁN BÙ TRỪ KHI Ô BỊ KHUẤT MÀN HÌNH (Ví dụ Item 10 trôi xuống đáy)
                if (targetIndex != null && layoutInfo.visibleItemsInfo.isNotEmpty()) {
                    val firstVisibleIndex = layoutInfo.visibleItemsInfo.first().index
                    val lastVisibleIndex = layoutInfo.visibleItemsInfo.last().index

                    if (targetIndex < firstVisibleIndex) {
                        // Ô đích bị khuất ở PHÍA TRÊN màn hình -> Ép bóng ma bay vọt lên cạnh trên
                        Offset(lastValidStartOffset.x, -activeItemSize.height.toFloat() * 1.5f)
                    } else if (targetIndex > lastVisibleIndex) {
                        // Ô đích bị khuất ở PHÍA DƯỚI màn hình (Trường hợp của bạn) -> Ép bóng ma lao thẳng xuống dưới đáy màn hình
                        Offset(lastValidStartOffset.x, layoutInfo.viewportSize.height.toFloat() + activeItemSize.height.toFloat())
                    } else {
                        lastValidStartOffset
                    }
                } else {
                    lastValidStartOffset
                }
            }

            // Tiến hành tính toán khoảng cách toán học Pitago để chạy Animation mượt mà như cũ
            val currentX = shadowOffset.value.x
            val currentY = shadowOffset.value.y
            val deltaX = targetOffset.x - currentX
            val deltaY = targetOffset.y - currentY
            val distance = sqrt(deltaX * deltaX + deltaY * deltaY)

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