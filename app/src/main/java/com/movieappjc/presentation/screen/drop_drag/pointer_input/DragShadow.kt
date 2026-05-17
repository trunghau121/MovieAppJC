package com.movieappjc.presentation.screen.drop_drag.pointer_input

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun <T> DragShadow(
    dragDropState: GenericDragDropState<T>,
    width: Dp = 110.dp,
    height: Dp = 100.dp,
    itemContent: @Composable (T) -> Unit
) {
    dragDropState.draggedIndex?.let { currentIndex ->
        val shadowItem = dragDropState.listData.getOrNull(currentIndex) ?: return@let

        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
            modifier = Modifier
                .size(width = width, height = height)
                .graphicsLayer {
                    // Ép ngón tay luôn nằm CHÍNH GIỮA ô Card
                    translationX = dragDropState.fingerOffset.x - (width.toPx() / 2f)
                    translationY = dragDropState.fingerOffset.y - (height.toPx() / 2f)

                    scaleX = 1.08f
                    scaleY = 1.08f
                }
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                itemContent(shadowItem)
            }
        }
    }
}