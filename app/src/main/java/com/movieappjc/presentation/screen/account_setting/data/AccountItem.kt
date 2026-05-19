package com.movieappjc.presentation.screen.account_setting.data

import java.util.UUID

open class AccountItem(
    val id: Int = UUID.randomUUID().hashCode(),
    val name: String = "",
    val number: String = "",
    var isAccountDefault: Boolean = false,
    var isAccountHome: Boolean = false
)

class Header : AccountItem()
class Title(val text: String) : AccountItem()
class PlaceHolder : AccountItem()
class AccountItemEmpty(isAccountDefault: Boolean = false) : AccountItem(isAccountDefault = isAccountDefault)