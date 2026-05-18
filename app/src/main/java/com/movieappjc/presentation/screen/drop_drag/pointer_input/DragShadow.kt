package com.movieappjc.presentation.screen.drop_drag.pointer_input

import androidx.compose.animation.core.Animatable
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

    // LẮNG NGHE CHU KỲ SỰ KIỆN ĐỂ ĐIỀU KHIỂN HOẠT ẢNH TRƯỢT VỀ CHUẨN XÁC
    LaunchedEffect(dragDropState.draggedIndex, dragDropState.isReturningAnimation) {
        val globalIndex = dragDropState.draggedIndex

        if (globalIndex != null && !dragDropState.isReturningAnimation) {
            // == 1. TRẠNG THÁI ĐANG KÉO DI CHUYỂN ==
            activeDraggedIndex = globalIndex
            activeItem = listData.getOrNull(globalIndex)
            activeItemSize = dragDropState.draggedItemSize
            lastValidStartOffset = dragDropState.dragStartAbsoluteOffset

            val initialOffset = dragDropState.fingerOffset - dragDropState.initialTouchOffset
            shadowOffset.snapTo(initialOffset)
        } else if (dragDropState.isReturningAnimation) {
            // == 2. TRẠNG THÁI NGƯỜI DÙNG THẢ TAY (DROP) ==
            if (activeDraggedIndex != null && activeItem != null) {
                val layoutInfo = dragDropState.getLayoutInfo()
                val targetLayoutItem = layoutInfo.visibleItemsInfo.find { it.index == activeDraggedIndex }

                val targetOffset = if (targetLayoutItem != null) {
                    Offset(targetLayoutItem.offset.x.toFloat(), targetLayoutItem.offset.y.toFloat())
                } else {
                    lastValidStartOffset
                }

                // Thực hiện hiệu ứng tịnh tiến trượt mượt mà về đích
                shadowOffset.animateTo(
                    targetValue = targetOffset,
                    animationSpec = tween(durationMillis = 2000)
                )

                // CHẠY XONG XUÔI ANIMATION -> Mới dọn dẹp state tổng để ô gốc hiển thị trở lại
                dragDropState.clearDragStateAfterAnimation()
                activeDraggedIndex = null
                activeItem = null
                activeItemSize = IntSize.Zero
            }
        }
    }

    // LIÊN TỤC SNAP THEO NGÓN TAY KHI ĐANG DI CHUYỂN (CHỈ KHI CHƯA RE-TURNING)
    if (!dragDropState.isReturningAnimation && dragDropState.draggedIndex != null) {
        LaunchedEffect(currentFingerOffset) {
            shadowOffset.snapTo(currentFingerOffset)
        }
    }

    // VẼ KHUNG HÌNH BÓNG MA (HIỂN THỊ TRONG SUỐT QUÁ TRÌNH KÉO CHO ĐẾN KHI TRƯỢT XONG)
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
                }
        ) {
            itemContent(itemToRender)
        }
    }
}