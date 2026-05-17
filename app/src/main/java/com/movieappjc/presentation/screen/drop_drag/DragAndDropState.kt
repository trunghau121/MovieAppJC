package com.movieappjc.presentation.screen.drop_drag

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

class DragAndDropState<T>{
    val listData = mutableStateListOf<T>() // Khởi tạo rỗng trước

    var draggedSourceIndex by mutableStateOf<Int?>(null)
        internal set

    // Hàm dùng để cập nhật lại toàn bộ danh sách khi API trả về dữ liệu mới
    fun updateData(newList: List<T>) {
        listData.clear()
        listData.addAll(newList)
    }
}

@Composable
fun <T> rememberDragAndDropState(): DragAndDropState<T> {
    return remember { DragAndDropState() }
}