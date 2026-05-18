package com.movieappjc.presentation.screen.drop_drag.pointer_input

import android.annotation.SuppressLint
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
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