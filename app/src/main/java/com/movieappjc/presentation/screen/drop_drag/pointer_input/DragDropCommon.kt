package com.movieappjc.presentation.screen.drop_drag.pointer_input

import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize

// === INTERFACE CẦU NỐI CHUNG ===
interface DragDropItemInfo {
    val index: Int
    val offset: IntOffset
    val size: IntSize
}

interface DragDropLayoutInfo {
    val viewportSize: IntSize
    val visibleItemsInfo: List<DragDropItemInfo>
}

// === NGỮ CẢNH BỔ SUNG KHHI KIỂM TRA QUY TẮC ===
data class DragDropContext(
    val currentUserRole: String = "USER",
    val isEditMode: Boolean = true,
    val currentTimeMillis: Long = System.currentTimeMillis()
)

// === CHIẾN LƯỢC QUẢN LÝ KHÓA/CỐ ĐỊNH ITEM ===
interface DragDropPolicy<T> {
    /**
     * Bản thân item này có thể bị nắm kéo đi hay không.
     */
    fun canDrag(item: T, context: DragDropContext): Boolean

    /**
     * Các item khác có thể thả (Drop) đè lên vị trí của ô này khi kết thúc kéo hay không.
     */
    fun canAcceptDrop(item: T, context: DragDropContext): Boolean

    /**
     * Ô này có thể tự động hoán đổi/dạt vị trí ngay trong lúc một item khác đang kéo lướt qua hay không.
     */
    fun canSwapOnHover(item: T, context: DragDropContext): Boolean
}