package com.movieappjc.presentation.screen.drop_drag.pointer_input

import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize

// === COMMON BRIDGE INTERFACES ===
/**
 * Holds geometric and indexing information for a single visible item in the layout.
 */
interface DragDropItemInfo {
    // The specific positional index of the item inside the backing list data structure
    val index: Int

    // The absolute coordinate position (x, y) of the item's top-left corner relative to the parent container
    val offset: IntOffset

    // The current physical pixel dimensions (width, height) of this item layout block
    val size: IntSize
}

/**
 * Holds information about the layout container's viewport and its currently visible items.
 */
interface DragDropLayoutInfo {
    // The overall pixel size bounds of the visible screen area (viewport) containing the list
    val viewportSize: IntSize

    // A real-time collection holding geometric metadata for all items currently visible to the user
    val visibleItemsInfo: List<DragDropItemInfo>
}

// === DRAG & DROP BUSINESS POLICY STRATEGY ===
interface DragDropPolicy<T> {
    // Evaluates permission boundaries to verify if a long-press can successfully initialize a drag on this item
    fun canDrag(item: T, index: Int): Boolean

    // Validates if the selected target area is legally allowed to act as a permanent landing destination
    fun canAcceptDrop(item: T, index: Int): Boolean

    // Dictates if this item placeholder should dynamically step aside and swap positions while a shadow drifts past it
    fun canSwapOnHover(item: T, index: Int): Boolean
}