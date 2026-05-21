package com.movieappjc.presentation.screen.menu.reorder

import com.movieappjc.presentation.screen.drop_drag.pointer_input.DragDropPolicy
import com.movieappjc.presentation.screen.menu.EmptyMenuItem
import com.movieappjc.presentation.screen.menu.MenuItem

class MenuDragDropPolicy: DragDropPolicy<MenuItem> {
    override fun canDrag(
        item: MenuItem,
        index: Int
    ): Boolean {
        return item !is EmptyMenuItem
    }

    override fun canAcceptDrop(
        item: MenuItem,
        index: Int
    ): Boolean {
        return item is EmptyMenuItem || item.isHome
    }

    override fun canSwapOnHover(
        item: MenuItem,
        index: Int
    ): Boolean {
        return item !is EmptyMenuItem && item.isHome
    }
}