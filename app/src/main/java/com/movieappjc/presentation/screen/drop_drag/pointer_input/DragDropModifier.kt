package com.movieappjc.presentation.screen.drop_drag.pointer_input

import android.annotation.SuppressLint
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback

@SuppressLint("UnnecessaryComposedModifier")
fun <T> Modifier.dragDropSourceContainer(
    dragDropState: GenericDragDropState<T>
): Modifier = composed {
    val haptic = LocalHapticFeedback.current

    this.pointerInput(dragDropState) {
        detectDragGesturesAfterLongPress(
            onDragStart = { offset ->
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                dragDropState.onDragStart(offset)
            },
            onDrag = { change, dragAmount ->
                change.consume()
                dragDropState.onDrag(dragAmount)
            },
            onDragEnd = { dragDropState.onDragEnd() },
            onDragCancel = { dragDropState.onDragEnd() }
        )
    }
}

fun <T> Modifier.dragDropItemModifier(
    index: Int,
    itemId: Any,
    dragDropState: GenericDragDropState<T>
): Modifier = this.composed {
    val shouldHideOriginalItem by remember(index, itemId, dragDropState) {
        derivedStateOf {
            val isCurrentlyDraggingThis = dragDropState.draggedIndex == index
            val isReturningThis = dragDropState.isReturningAnimation && dragDropState.draggedIndex == index
            val isWaitingForListAnimationThis = dragDropState.lastDraggedId == itemId

            isCurrentlyDraggingThis || isReturningThis || isWaitingForListAnimationThis
        }
    }

    this.alpha(if (shouldHideOriginalItem) 0f else 1f)
}