package com.movieappjc.presentation.screen.account_setting.data

import java.util.UUID

open class AccountItem(
    val id: Int = UUID.randomUUID().hashCode(),
    val name: String = "",
    val number: String = "",
    val isEmpty: Boolean = false
)

class Header : AccountItem()
class Title(val text: String) : AccountItem()
class PlaceHolder : AccountItem()
class AccountItemEmpty(val isAccountDefault: Boolean = false) : AccountItem(isEmpty = true)