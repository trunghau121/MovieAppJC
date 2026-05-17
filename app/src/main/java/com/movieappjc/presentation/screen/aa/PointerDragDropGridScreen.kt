package com.movieappjc.presentation.screen.aa

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PointerDragDropGridScreen() {
    val lazyGridState = rememberLazyGridState()
    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current
    val listData = remember { mutableStateListOf<String>().apply {
        addAll((1..50).map { "Phần tử $it" })
    }}

    val dragDropState = remember(lazyGridState, scope) {
        PointerDragDropState(listData, lazyGridState, scope)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .safeDrawingPadding()
            // Lắng nghe cử chỉ kéo tại nền Box cha để không lo mất drag khi scroll
            .pointerInput(Unit) {
                detectDragGesturesAfterLongPress(
                    onDragStart = { offset ->
                        haptic.performHapticFeedback(HapticFeedbackType.GestureThresholdActivate)
                        dragDropState.onDragStart(offset)
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        dragDropState.onDrag(dragAmount)
                    },
                    onDragEnd = {
                        haptic.performHapticFeedback(HapticFeedbackType.GestureEnd)
                        dragDropState.onDragEnd() // Thả tay ra mới đổi chỗ
                    },
                    onDragCancel = {
                        dragDropState.onDragEnd()
                    }
                )
            }
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            state = lazyGridState,
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp)
        ) {
            itemsIndexed(
                items = listData,
                key = { _, item -> item },
                span = { index, _ ->
                    val span = if (index in 9..12) maxLineSpan else 1
                    GridItemSpan(span)
                }
            ) { index, item ->
                if (index in 9..12) {
                    // TITLE VÙNG CẤM (ĐỨNG IM)
                    Text(
                        text = "Set accounts for Home screen",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF111827),
                        modifier = Modifier.padding(12.dp)
                    )
                } else {
                    // CARD CHỨA ITEM
                    val isDraggingThisItem = dragDropState.draggedIndex == index

                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(6.dp)
                            .height(100.dp)
                            // Kích hoạt Spec trượt mượt mà cho các phần tử lúc THẢ TAY RA
                            .animateItem(
                                placementSpec = tween(
                                    durationMillis = 450,
                                    easing = FastOutSlowInEasing
                                )
                            )
                            // Trong lúc kéo, ẩn ô gốc đi để lộ khoảng trống, các ô khác vẫn giữ nguyên vị trí cũ
                            .alpha(if (isDraggingThisItem) 0f else 1f)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = item,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B)
                            )
                        }
                    }
                }
            }
        }

        // VẼ "BÓNG MA LAYOUT" ĐÈ LÊN TRÊN BOX CHA
        dragDropState.draggedIndex?.let { currentIndex ->
            val shadowItemText = listData.getOrNull(currentIndex) ?: ""

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                modifier = Modifier
                    .size(width = 110.dp, height = 100.dp)
                    .graphicsLayer {
                        // SỬ DỤNG TRỰC TIẾP FINGER_OFFSET SẠCH:
                        // Ngón tay di chuyển đi đâu bóng đi theo đó, List tự cuộn bóng vẫn đứng im tại tay!
                        translationX = dragDropState.fingerOffset.x - (110.dp.toPx() / 2f)
                        translationY = dragDropState.fingerOffset.y - (100.dp.toPx() / 2f)

                        scaleX = 1.08f
                        scaleY = 1.08f
                    }
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = shadowItemText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                }
            }
        }
    }
}