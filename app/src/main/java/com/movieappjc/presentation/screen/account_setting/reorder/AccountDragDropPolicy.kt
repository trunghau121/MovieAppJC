package com.movieappjc.presentation.screen.account_setting.reorder

import com.movieappjc.presentation.screen.account_setting.data.AccountItem
import com.movieappjc.presentation.screen.account_setting.data.AccountItemBlank
import com.movieappjc.presentation.screen.account_setting.data.AccountItemEmpty
import com.movieappjc.presentation.screen.account_setting.data.Title
import com.movieappjc.presentation.screen.drop_drag.pointer_input.DragDropPolicy

class AccountDragDropPolicy: DragDropPolicy<AccountItem> {
    override fun canDrag(
        item: AccountItem,
        index: Int
    ): Boolean {
        return item !is AccountItemEmpty && item !is Title
    }

    override fun canAcceptDrop(
        item: AccountItem,
        index: Int
    ): Boolean {
        return item is AccountItemEmpty
                || item is AccountItemBlank
                || item.isAccountDefault
                || item.isAccountHome
                || item.number.isNotEmpty()
    }

    override fun canSwapOnHover(
        item: AccountItem,
        index: Int
    ): Boolean {
        return true
    }
}