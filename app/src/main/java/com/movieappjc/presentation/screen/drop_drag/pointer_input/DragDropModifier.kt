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
    // Access Android's native haptic feedback system (vibrations)
    val haptic = LocalHapticFeedback.current

    this.pointerInput(dragDropState) {
        // A flag to check if the user's finger has actually moved from the initial press spot
        var hasMoved = false

        detectDragGesturesAfterLongPress(
            onDragStart = { offset ->
                // Reset the movement flag back to false whenever a new press begins
                hasMoved = false
                // Vibrate the device slightly to confirm to the user that the item is locked and ready to drag
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                // Initialize coordination states inside DragDropState for the selected item
                dragDropState.onDragStart(offset)
            },
            onDrag = { change, dragAmount ->
                // Only process movement if we have a valid item actively locked under drag
                if (dragDropState.draggedIndex != null) {
                    // If the movement delta vector is greater than 0, confirm the finger is moving
                    if (dragAmount.getDistance() > 0f) {
                        hasMoved = true
                    }
                    // Consume the touch event so parent scrollable containers don't handle it concurrently
                    change.consume()
                    // Pass the real-time movement offset to update positions
                    dragDropState.onDrag(dragAmount)
                }
            },
            onDragEnd = {
                // CASE 1: The user actually dragged the item to a new spot
                if (hasMoved) {
                    // Trigger standard drop validation logic and calculate return animation goals
                    dragDropState.onDragEnd()
                } else {
                    // CASE 2: The user long-pressed but released at the exact same spot (Tremor/Tap)
                    // Bypass return animations and immediately delete the DragShadow to avoid frozen ghosts
                    dragDropState.clearDragStateAfterAnimation(null)
                }
            },
            onDragCancel = {
                // CASE 3: System cancels gesture abruptly (e.g., system back swipe, notifications dropdown)
                // Immediately force-clean all states to unlock the list container and eliminate freezes
                dragDropState.clearDragStateAfterAnimation(null)
            }
        )
    }
}

fun <T> Modifier.dragDropItemModifier(
    index: Int,
    item: T,
    dragDropState: DragDropState<T>
): Modifier = this.composed {
    // Optimization: Compute visibility state using derivedStateOf to avoid redundant layout recompositions
    val shouldHideOriginalItem by remember(index, item, dragDropState) {
        derivedStateOf {
            // Check if the current item index matches the item being actively dragged
            val isCurrentlyDraggingThis = dragDropState.draggedIndex == index
            // Check if this item is currently animating back to its settled location
            val isReturningThis = dragDropState.isReturningAnimation && dragDropState.draggedIndex == index
            // Check if we are waiting for the lazy list database structure to finalize updating
            val isWaitingForListAnimationThis = dragDropState.lastDraggedItem == item

            // Hide the original layout placeholder item if any of these states are active
            isCurrentlyDraggingThis || isReturningThis || isWaitingForListAnimationThis
        }
    }

    // Set visibility alpha to 0f (invisible but holds space) or 1f (fully visible)
    this.alpha(if (shouldHideOriginalItem) 0f else 1f)
}