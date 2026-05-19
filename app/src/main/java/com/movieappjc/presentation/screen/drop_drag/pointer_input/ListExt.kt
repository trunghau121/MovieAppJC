package com.movieappjc.presentation.screen.drop_drag.pointer_input

/**
 * Hoán đổi vị trí của hai phần tử bất kỳ trong một MutableList.
 * Hàm này hoạt động mượt mà với cả List thường lẫn SnapshotStateList của Compose.
 */
fun <T> MutableList<T>.swap(fromIndex: Int, toIndex: Int): List<T> {
    if (fromIndex == toIndex) return this
    if (fromIndex !in indices || toIndex !in indices) return this

    val temp = this[fromIndex]
    this[fromIndex] = this[toIndex]
    this[toIndex] = temp
    return this
}