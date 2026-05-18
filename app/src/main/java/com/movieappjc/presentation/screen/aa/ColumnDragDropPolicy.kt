package com.movieappjc.presentation.screen.aa

import com.movieappjc.presentation.screen.drop_drag.pointer_input.DragDropContext
import com.movieappjc.presentation.screen.drop_drag.pointer_input.DragDropPolicy

class ColumnDragDropPolicy: DragDropPolicy<ColumnItemModel> {
    override fun isItemFixed(item: ColumnItemModel, context: DragDropContext): Boolean {
        return item.isFixed
    }

    override fun shouldRestrictSwapUntilDrop(item: ColumnItemModel, context: DragDropContext): Boolean {
        return item.isLocked
    }
}