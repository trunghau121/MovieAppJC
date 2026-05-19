package com.movieappjc.presentation.screen.account_setting.data

import java.util.UUID

open class AccountItem(
    val id: Int = UUID.randomUUID().hashCode(),
    val name: String = "",
    val number: String = "",
    var isAccountDefault: Boolean = false,
    var isAccountHome: Boolean = false
) {
    fun copy(
        id: Int = UUID.randomUUID().hashCode(),
        name: String = this.name,
        number: String = this.number,
        isAccountDefault: Boolean = this.isAccountDefault,
        isAccountHome: Boolean = this.isAccountHome
    ): AccountItem {
        return AccountItem(
            id = id,
            name = name,
            number = number,
            isAccountDefault = isAccountDefault,
            isAccountHome = isAccountHome
        )
    }
}

class Header : AccountItem()
class Title(val text: String) : AccountItem()
class PlaceHolder : AccountItem()
class AccountItemEmpty(isAccountDefault: Boolean = false) : AccountItem(isAccountDefault = isAccountDefault)
class AccountItemBlank : AccountItem()