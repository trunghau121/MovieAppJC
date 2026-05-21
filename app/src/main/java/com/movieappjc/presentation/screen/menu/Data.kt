package com.movieappjc.presentation.screen.menu

import java.util.UUID

open class MenuItem(
    val index: Int = -1,
    var id: Int = UUID.randomUUID().hashCode(),
    val title: String = "",
    val icon: Int = 0,
    val isFixed: Boolean = false,
    var isHome: Boolean = true,
    var isEmpty: Boolean = false
) {
    fun copy(
        index: Int = this.index,
        id: Int = UUID.randomUUID().hashCode(),
        title: String = this.title,
        icon: Int = this.icon,
        isFixed: Boolean = this.isFixed,
        isHome: Boolean = this.isHome,
        isEmpty: Boolean = this.isEmpty
    ): MenuItem {
        return MenuItem(
            index = index,
            id = id,
            title = title,
            icon = icon,
            isFixed = isFixed,
            isHome = isHome,
            isEmpty = isEmpty
        )
    }
}

class EmptyMenuItem : MenuItem(UUID.randomUUID().hashCode(), isEmpty = true)

data class MyMenuPlaceHolder(val text: String) : MenuItem(title = text)

fun MenuItem.isEmpty(): Boolean = isEmpty