package com.movieappjc.presentation.screen.test

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.movieappjc.presentation.screen.drop_drag.dragDropTargetContainer
import com.movieappjc.presentation.screen.drop_drag.dragSourceItem
import com.movieappjc.presentation.screen.drop_drag.rememberDragAndDropState

@Composable
fun AbsoluteCoordinatesDragAndDropGridScreen() {
    val lazyGridState = rememberLazyGridState()
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current

    // 1. Khởi tạo State của Reorder và AutoScroller qua bộ hỗ trợ mới
    val dragAndDropState = rememberDragAndDropState<String>()
    dragAndDropState.updateData((1..50).map { "Phần tử $it" })

    val thresholdPx = with(density) { 90.dp.toPx() }
    val autoScroller = remember(lazyGridState, scope) {
        GridDragDropScroller(lazyGridState, scope, thresholdPx)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .safeDrawingPadding()
            // 2. Tái sử dụng: Chỉ cần gắn Container Modifier này vào Box chứa Grid
            .dragDropTargetContainer(dragAndDropState, autoScroller) { sourceIndex, targetIndex ->
                if (sourceIndex in 9..12 || targetIndex in 9..12) {
                    return@dragDropTargetContainer
                }
                if (sourceIndex !in dragAndDropState.listData.indices
                    || targetIndex !in dragAndDropState.listData.indices) {
                    return@dragDropTargetContainer
                }

                val temp = dragAndDropState.listData[sourceIndex]
                dragAndDropState.listData[sourceIndex] = dragAndDropState.listData[targetIndex]
                dragAndDropState.listData[targetIndex] = temp
            }
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            state = lazyGridState,
            modifier = Modifier.fillMaxSize().padding(4.dp)
        ) {
            itemsIndexed(
                dragAndDropState.listData,
                key = { _, item -> item },
                span = { index, _ ->
                    GridItemSpan(if (index in 9..12) maxLineSpan else 1)
                }
            ) { index, item ->
                if (index in 9..12) {
                    Text(
                        text = "Set accounts for Home screen",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(12.dp)
                    )
                } else {
                    val itemGraphicsLayer = rememberGraphicsLayer()

                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Green),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(6.dp)
                            .height(100.dp)
                            .animateItem(placementSpec = tween(
                                durationMillis = 400, // Thay đổi thời gian bay tại đây (ví dụ: 600ms cho mượt và chậm lại)
                                easing = FastOutSlowInEasing
                            ))
                            // 3. Tái sử dụng: Chỉ cần gắn Item Modifier cho các ô Grid mong muốn reorder
                            .dragSourceItem(index, dragAndDropState, itemGraphicsLayer)
                    ) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(text = item, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}