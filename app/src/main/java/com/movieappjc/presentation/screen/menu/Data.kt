package com.movieappjc.presentation.screen.menu

import java.util.UUID

open class MenuItem(
    val index: Int = -1,
    var id: Int = UUID.randomUUID().hashCode(),
    val title: String = "",
    val icon: Int = 0,
    val isFixed: Boolean = false,
    var isHome: Boolean = true,
    var isEmpty: Boolean = false,
    var isCanDrag: Boolean = true
)

val Empty get() = MenuItem(UUID.randomUUID().hashCode(), isEmpty = true)

data class MyMenuHeader(val text: String) : MenuItem(title = text)

fun MenuItem.isEmpty(): Boolean = isEmpty