package com.movieappjc.presentation.screen.drop_drag.pointer_input

import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize

// === COMMON BRIDGE INTERFACES ===
/**
 * Holds geometric and indexing information for a single visible item in the layout.
 */
interface DragDropItemInfo {
    val index: Int
    val offset: IntOffset
    val size: IntSize
}

/**
 * Holds information about the layout container's viewport and its currently visible items.
 */
interface DragDropLayoutInfo {
    val viewportSize: IntSize
    val visibleItemsInfo: List<DragDropItemInfo>
}

// === ADDITIONAL CONTEXT FOR RULE VALIDATION ===
data class DragDropContext(
    val currentUserRole: String = "USER",
    val isEditMode: Boolean = true,
    val currentTimeMillis: Long = System.currentTimeMillis()
)

// === DRAG & DROP BUSINESS POLICY STRATEGY ===
interface DragDropPolicy<T> {
    /**
     * Determines if this specific item can be grabbed and dragged by the user.
     */
    fun canDrag(item: T, context: DragDropContext): Boolean

    /**
     * Determines if other items can be dropped onto this item's position when the drag gesture ends.
     */
    fun canAcceptDrop(item: T, context: DragDropContext): Boolean

    /**
     * Determines if this item can dynamically shift/swap its position while another item is actively hovering over it.
     */
    fun canSwapOnHover(item: T, context: DragDropContext): Boolean
}