package com.movieappjc.presentation.screen.menu.utils

import android.annotation.SuppressLint
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import com.movieappjc.presentation.screen.menu.Empty
import com.movieappjc.presentation.screen.menu.MenuItem
import com.movieappjc.presentation.screen.menu.isEmpty
import com.movieappjc.presentation.screen.menu.reorderable.ReorderableCollectionItemScope

@Composable
fun Modifier.shaking(enabled: Boolean): Modifier {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val rotation by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(100, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )
    return if (enabled) this.graphicsLayer { rotationZ = rotation } else this
}

@SuppressLint("ModifierFactoryExtensionFunction")
fun ReorderableCollectionItemScope.reorderableItemModifier(
    list: MutableState<List<MenuItem>>,
    item: MenuItem,
    index: Int,
    haptic: HapticFeedback,
): Modifier {
    return Modifier
        .semantics {
            customActions = listOf(
                CustomAccessibilityAction(
                    label = "Move Before",
                    action = {
                        if (index > 0) {
                            val fromIndex = index
                            val toIndex = index - 1

                            // Disallow moving empty items
                            if (list.value[fromIndex].isEmpty()) {
                                return@CustomAccessibilityAction false
                            }

                            list.value = list.value.handleItemReorder(
                                fromIndex = fromIndex,
                                toIndex = toIndex
                            )

                            true
                        } else {
                            false
                        }
                    }
                ),
                CustomAccessibilityAction(
                    label = "Move After",
                    action = {
                        if (index < list.value.size - 1) {
                            val fromIndex = index
                            val toIndex = index + 1

                            // Disallow moving empty items
                            if (list.value[fromIndex].isEmpty()) {
                                return@CustomAccessibilityAction false
                            }

                            list.value = list.value.handleItemReorder(
                                fromIndex = fromIndex,
                                toIndex = toIndex
                            )

                            true
                        } else {
                            false
                        }
                    }
                )
            )
        }
        .longPressDraggableHandle(
            onDragStarted = {
                if (!item.isEmpty()) {
                    haptic.performHapticFeedback(HapticFeedbackType.GestureThresholdActivate)
                }
            },
            onDragStopped = {
                if (!item.isEmpty()) {
                    val mutableList = list.value.toMutableList().filterIndexed { index, it ->
                        !(index >= 8 && it.isEmpty())
                    }.toMutableList()

                    if (!item.isHome) {
                        mutableList.removeIf { menu ->
                            !menu.isHome && menu.id == item.id
                        }

                        list.value = insertOrder(mutableList, item)
                    } else {
                        list.value = mutableList
                    }
                    haptic.performHapticFeedback(HapticFeedbackType.GestureEnd)
                }
            }
        )
        .clearAndSetSemantics { }
}

fun List<MenuItem>.handleItemReorder(
    fromIndex: Int,
    toIndex: Int,
    topItemSize: Int = 9
): List<MenuItem> {
    var mutableList = toMutableList()

    val isFromTop = fromIndex < topItemSize
    val isToTop = toIndex < topItemSize

    // Remove the item from its original location
    val item = mutableList.removeAt(fromIndex).apply {
        isHome = isToTop
    }

    if (isFromTop) {
        // Insert EMPTY_ITEM in original spot if removing from top
        mutableList.add(fromIndex, Empty)
    } else if (isToTop) {
        var targetInsertIndex = -1

        for (i in (8 + 1) until mutableList.size) {
            val currentItem = mutableList[i]
            if (currentItem.index > item.index) {
                targetInsertIndex = i
                break
            }
        }

        if (targetInsertIndex != -1) {
            mutableList.add(targetInsertIndex, Empty)
        } else {
            mutableList.add(mutableList.size, Empty)
        }
    }

    if (isToTop) {
        // Add to top section logic
        if (mutableList[toIndex].isEmpty()) {
            // Replace empty directly
            mutableList[toIndex] = item
        } else {
            val emptyList = (0 until topItemSize).filter {
                mutableList[it].isEmpty()
            }
            if (emptyList.isNotEmpty()) {
                if (isFromTop) {
                    mutableList[toIndex] = item.also {
                        mutableList[fromIndex] = mutableList[toIndex]
                    }
                } else {
                    // Try shifting right to make room
                    val rightEmpty = (toIndex + 1 until topItemSize).firstOrNull {
                        mutableList[it].isEmpty()
                    }
                    if (rightEmpty != null) {
                        for (i in rightEmpty downTo toIndex + 1) {
                            mutableList[i] = mutableList[i - 1]
                        }
                        mutableList[toIndex] = item
                    } else {
                        // Try shifting left
                        val leftEmpty = (0 until toIndex).lastOrNull { mutableList[it].isEmpty() }

                        if (leftEmpty != null) {
                            for (i in leftEmpty until toIndex) {
                                mutableList[i] = mutableList[i + 1]
                            }
                            mutableList[toIndex] = item
                        } else {
                            // No empty space, shift right
                            val removedItem = mutableList.removeAt(toIndex)
                            mutableList.add(toIndex, item)

                            insertOrder(mutableList, removedItem)
                        }
                    }
                }
            } else {
                val removedItem = mutableList.removeAt(toIndex)
                // No empty space, shift right
                mutableList.add(toIndex, item)

                insertOrder(mutableList, removedItem)
            }
        }
    } else {
        mutableList = mutableList.filterIndexed { index, it ->
            !(index >= topItemSize && it.isEmpty())
        }.toMutableList()

        mutableList.add(toIndex, item)
    }

//    val cleaned = mutableList.filterIndexed { index, it ->
//        !(index >= topItemSize && it.isEmpty())
//    }.toMutableList()

    return mutableList
}

private fun insertOrder(
    mutableList: MutableList<MenuItem>,
    removedItem: MenuItem
): MutableList<MenuItem> {
    var targetInsertIndex = -1

    for (i in (8 + 1) until mutableList.size) {
        val currentItem = mutableList[i]
        if (currentItem.index > removedItem.index) {
            targetInsertIndex = i
            break
        }
    }

    if (targetInsertIndex != -1) {
        mutableList.add(targetInsertIndex, removedItem.apply {
            isHome = false
        })
    } else {
        mutableList.add(mutableList.size, removedItem.apply {
            isHome = false
        })
    }
    return mutableList
}

fun List<MenuItem>.addItemToTop(index: Int, topItemSize: Int = 8): List<MenuItem> {
    val mutableList = toMutableList()

    // Try to find an empty slot in top section
    val emptyIndex = (0 until topItemSize).firstOrNull { mutableList[it].isEmpty() }

    if (emptyIndex != null) {
        mutableList[emptyIndex] = mutableList.removeAt(index).apply {
            isHome = true
        }
    }

    return mutableList
}

fun List<MenuItem>.removeItemFromTop(index: Int): List<MenuItem> {
    val mutableList = toMutableList()

    val removedItem = mutableList[index]
    mutableList[index] = Empty
    mutableList

    return insertOrder(mutableList, removedItem)
}