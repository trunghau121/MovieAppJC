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
     * Chặn hoàn toàn: Không cho phép nắm kéo, không cho phép di chuyển 
     * và không cho bất kỳ phần tử nào khác hoán đổi vị trí với nó.
     */
    fun isItemFixed(item: T, context: DragDropContext): Boolean

    /**
     * Chặn tạm thời khi Drag: Khi đang kéo các ô khác lướt qua thì không tự động hoán đổi.
     * Chỉ thực hiện kiểm tra và hoán đổi duy nhất một lần khi người dùng buông tay (Drop).
     */
    fun shouldRestrictSwapUntilDrop(item: T, context: DragDropContext): Boolean
}