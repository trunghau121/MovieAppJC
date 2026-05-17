package com.movieappjc.presentation.screen.drop_drag

interface DragDropScroller {
    /**
     * Xử lý di chuyển ngón tay để kích hoạt tự động cuộn (Auto Scroll).
     * @param pointerAbsoluteY Tọa độ Y tuyệt đối của ngón tay trên màn hình.
     * @param containerTopPx Tọa độ biên trên cùng của vùng chứa (List/Grid/Column).
     * @param containerBottomPx Tọa độ biên dưới cùng của vùng chứa (List/Grid/Column).
     */
    fun handleDragPosition(pointerAbsoluteY: Float, containerTopPx: Float, containerBottomPx: Float)

    /**
     * Tìm vị trí Index của Item tại tọa độ đang thả ngón tay.
     * @param containerLeftPx Tọa độ biên trái của vùng chứa (để tính khoảng cách tương đối X).
     * @param containerTopPx Tọa độ biên trên của vùng chứa (để tính khoảng cách tương đối Y).
     */
    fun findTargetIndex(dropTargetX: Float, dropTargetY: Float, containerLeftPx: Float, containerTopPx: Float): Int?

    /**
     * Yêu cầu giữ nguyên vị trí cuộn hiện tại (Ép buộc Layout đứng im)
     */
    fun requestKeepPosition()

    /**
     * Dừng hành động cuộn tự động
     */
    fun stop()
}