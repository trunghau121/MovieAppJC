package com.movieappjc.presentation.screen.menu

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.movieappjc.R
import com.movieappjc.presentation.screen.drop_drag.pointer_input.swap
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.indices

@HiltViewModel
class MyMenuViewModel @Inject constructor(): ViewModel() {
    val ignoreIndices = mutableSetOf<Int>()
    val myMenuList = mutableStateListOf<MenuItem>()

    fun loadMyMenuList() {
        viewModelScope.launch {
            myMenuList.clear()
            myMenuList.addAll(listOf(
                MenuItem(title = "Transfer", icon = R.drawable.icon_play, isFixed = true),
                MenuItem(title = "Payments", icon = R.drawable.icon_play, isFixed = true),
                EmptyMenuItem(),
                EmptyMenuItem(),
                EmptyMenuItem(),
                EmptyMenuItem(),
                EmptyMenuItem(),
                EmptyMenuItem(),
                MyMenuPlaceHolder("Out of home"),
                MenuItem(index = 9, title = "Inquiry", icon = R.drawable.icon_play, isHome = false),
                MenuItem(index = 10,title = "Product Center", icon = R.drawable.icon_play, isHome = false),
                MenuItem(index = 11,title = "Cards", icon = R.drawable.icon_play, isHome = false),
                MenuItem(index = 12,title = "QRIS", icon = R.drawable.icon_play, isHome = false),
                MenuItem(index = 13,title = "Personal Set", icon = R.drawable.icon_play, isHome = false),
                MenuItem(index = 14,title = "Security", icon = R.drawable.icon_play, isHome = false),
                MenuItem(index = 15,title = "Manage M-OTP", icon = R.drawable.icon_play, isHome = false),
                MenuItem(index = 16,title = "Favorite Acc", icon = R.drawable.icon_play, isHome = false),
                MenuItem(index = 17,title = "Inbox", icon = R.drawable.icon_play, isHome = false),
                MenuItem(index = 18,title = "Help Center", icon = R.drawable.icon_play, isHome = false),

                MenuItem(index = 19, title = "Transfer FX", icon = R.drawable.icon_play, isHome = false),
                MenuItem(index = 20,title = "Flazz", icon = R.drawable.icon_play, isHome = false),
                MenuItem(index = 21,title = "Setting", icon = R.drawable.icon_play, isHome = false),
                MenuItem(index = 22,title = "Account", icon = R.drawable.icon_play, isHome = false),
                MenuItem(index = 23,title = "E-slip", icon = R.drawable.icon_play, isHome = false),
                MenuItem(index = 24,title = "Contact", icon = R.drawable.icon_play, isHome = false),
                MenuItem(index = 25,title = "Information", icon = R.drawable.icon_play, isHome = false),
                MenuItem(index = 26,title = "Profile", icon = R.drawable.icon_play, isHome = false),
                MenuItem(index = 27,title = "Chat", icon = R.drawable.icon_play, isHome = false),
                MenuItem(index = 28,title = "About", icon = R.drawable.icon_play, isHome = false),
                MenuItem(index = 29,title = "Information", icon = R.drawable.icon_play, isHome = false),
                MenuItem(index = 30,title = "Profile", icon = R.drawable.icon_play, isHome = false),
                MenuItem(index = 31,title = "Chat", icon = R.drawable.icon_play, isHome = false),
                MenuItem(index = 32,title = "About", icon = R.drawable.icon_play, isHome = false)

            ))

            ignoreIndices.clear()
            ignoreIndices.addAll(myMenuList.mapIndexedNotNull { index, item ->
                if (item is MyMenuPlaceHolder || item.isFixed) index else null
            }.toSet())
        }
    }

    fun getTopItemSize(): Int = 8

    fun isFromMenuHome(index: Int) = getTopItemSize() == index

    fun swapMenuItem(from: Int, to: Int) {
        myMenuList.apply {
            if (from == to) return
            if (from !in indices || to !in indices) return

            val temp = this[from]
            this[from] = this[to].apply {
                isHome = isFromMenuHome(from)
            }
            this[to] = temp.apply {
                isHome = isFromMenuHome(to)
            }
        }
    }

    fun onRemoveOrAdd(index: Int) {
        val currentItem = myMenuList[index]
        if (!currentItem.isHome) {
            addItemToTop(index)
        } else {
            removeItemFromTop(index)
        }
    }

    private fun addItemToTop(index: Int) {
        // Try to find an empty slot in top section
        val emptyIndex = (0 until getTopItemSize()).firstOrNull { myMenuList[it].isEmpty() }

        if (emptyIndex != null) {
            myMenuList[emptyIndex] = myMenuList.removeAt(index).apply {
                isHome = true
            }
        }
    }

    private fun removeItemFromTop(index: Int) {
        val removedItem = myMenuList[index]
        myMenuList[index] = EmptyMenuItem()
        insertOrder(removedItem)
    }

    private fun insertOrder(removedItem: MenuItem) {
        var targetInsertIndex = -1

        for (i in (8 + 1) until myMenuList.size) {
            val currentItem = myMenuList[i]
            if (currentItem.index > removedItem.index) {
                targetInsertIndex = i
                break
            }
        }

        if (targetInsertIndex != -1) {
            myMenuList.add(targetInsertIndex, removedItem.apply {
                isHome = false
            })
        } else {
            myMenuList.add(myMenuList.size, removedItem.apply {
                isHome = false
            })
        }
    }
}