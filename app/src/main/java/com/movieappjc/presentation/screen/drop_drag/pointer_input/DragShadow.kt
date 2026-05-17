package com.movieappjc.presentation.screen.drop_drag.pointer_input

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer

@Composable
fun <T> DragShadow(
    dragDropState: GenericDragDropState<T>,
    itemContent: @Composable (T) -> Unit
) {
    dragDropState.draggedIndex?.let { currentIndex ->
        val shadowItem = dragDropState.listData.getOrNull(currentIndex) ?: return@let
        Box(
            modifier = Modifier
                .wrapContentSize()
                .graphicsLayer {
                    translationX = dragDropState.fingerOffset.x - dragDropState.initialTouchOffset.x
                    translationY = dragDropState.fingerOffset.y - dragDropState.initialTouchOffset.y
                    scaleX = 1.0f
                    scaleY = 1.0f
                },
            contentAlignment = Alignment.Center
        ) {
            itemContent(shadowItem)
        }
    }
}