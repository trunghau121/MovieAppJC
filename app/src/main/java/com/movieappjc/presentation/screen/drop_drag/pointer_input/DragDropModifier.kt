package com.movieappjc.presentation.screen.drop_drag.pointer_input

import android.annotation.SuppressLint
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.draw.alpha

@SuppressLint("UnnecessaryComposedModifier")
fun <T> Modifier.dragDropSourceContainer(
    dragDropState: DragDropState<T>
): Modifier = composed {
    val haptic = LocalHapticFeedback.current

    this.pointerInput(dragDropState) {
        detectDragGesturesAfterLongPress(
            onDragStart = { offset ->
                // Provide tactile feedback when the item is locked and ready to drag
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                dragDropState.onDragStart(offset)
            },
            onDrag = { change, dragAmount ->
                // Consume the pointer changes immediately at the highest level.
                // Calling change.consume() signals to parents (like LazyColumn/Grid)
                // that this event is handled, preventing conflicting scroll gestures.
                if (dragDropState.draggedIndex != null) {
                    change.consume()
                    dragDropState.onDrag(dragAmount)
                }
            },
            onDragEnd = {
                dragDropState.onDragEnd()
            },
            onDragCancel = {
                dragDropState.onDragEnd()
                // dragDropState.clearDragStateAfterAnimation(null)
            }
        )
    }
}

fun <T> Modifier.dragDropItemModifier(
    index: Int,
    item: T,
    dragDropState: DragDropState<T>
): Modifier = this.composed {
    // Optimization: Calculate visibility state reactively using derivedStateOf to prevent unnecessary recompositions
    val shouldHideOriginalItem by remember(index, item, dragDropState) {
        derivedStateOf {
            val isCurrentlyDraggingThis = dragDropState.draggedIndex == index
            val isReturningThis = dragDropState.isReturningAnimation && dragDropState.draggedIndex == index
            val isWaitingForListAnimationThis = dragDropState.lastDraggedItem == item

            // Hide the original list placeholder item if it is actively being dragged,
            // animating back, or waiting for the layout state to finalize.
            isCurrentlyDraggingThis || isReturningThis || isWaitingForListAnimationThis
        }
    }

    this.alpha(if (shouldHideOriginalItem) 0f else 1f)
}