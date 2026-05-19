package com.movieappjc.presentation.screen.account_setting.reorder

import com.movieappjc.presentation.screen.account_setting.data.AccountItem
import com.movieappjc.presentation.screen.account_setting.data.AccountItemEmpty
import com.movieappjc.presentation.screen.account_setting.data.Header
import com.movieappjc.presentation.screen.account_setting.data.PlaceHolder
import com.movieappjc.presentation.screen.account_setting.data.Title
import com.movieappjc.presentation.screen.drop_drag.pointer_input.DragDropContext
import com.movieappjc.presentation.screen.drop_drag.pointer_input.DragDropPolicy

class AccountDragDropPolicy: DragDropPolicy<AccountItem> {
    override fun canDrag(
        item: AccountItem,
        context: DragDropContext
    ): Boolean {
        return item !is AccountItemEmpty &&
                item !is Header && item !is Title && item !is PlaceHolder
    }

    override fun canAcceptDrop(
        item: AccountItem,
        context: DragDropContext
    ): Boolean {
        return item is AccountItemEmpty
    }

    override fun canSwapOnHover(
        item: AccountItem,
        context: DragDropContext
    ): Boolean {
        return item !is AccountItemEmpty &&
                item !is Header && item !is Title && item !is PlaceHolder
    }
}