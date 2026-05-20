package com.movieappjc.presentation.screen.drop_drag.pointer_input

/**
 * Swaps two elements seamlessly within a MutableList.
 * Optimized to perform correctly with both standard collections and Compose SnapshotStateLists.
 */
fun <T> MutableList<T>.swap(fromIndex: Int, toIndex: Int): List<T> {
    if (fromIndex == toIndex) return this
    if (fromIndex !in indices || toIndex !in indices) return this

    val temp = this[fromIndex]
    this[fromIndex] = this[toIndex]
    this[toIndex] = temp
    return this
}