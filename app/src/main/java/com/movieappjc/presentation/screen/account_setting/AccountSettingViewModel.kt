package com.movieappjc.presentation.screen.account_setting

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.core_app.base.viewmodel.BaseViewModel
import com.core_app.navigation.AppNavigator
import com.movieappjc.presentation.screen.account_setting.data.AccountItem
import com.movieappjc.presentation.screen.account_setting.data.AccountItemEmpty
import com.movieappjc.presentation.screen.account_setting.data.Header
import com.movieappjc.presentation.screen.account_setting.data.PlaceHolder
import com.movieappjc.presentation.screen.account_setting.data.Title
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountSettingViewModel @Inject constructor(
    appNavigator: AppNavigator
): BaseViewModel(appNavigator) {
    private val _accounts = MutableStateFlow<List<AccountItem>>(arrayListOf())
    val accounts = _accounts.asStateFlow()

    fun setDataAccounts(list: List<AccountItem>) {
        _accounts.value = list
    }

    fun loadDataAccounts() {
        viewModelScope.launch {
            _accounts.value = listOf(
                Header(),
                Title("Default Account"),
                AccountItemEmpty(isAccountDefault = true),
                Title("Accounts on Home"),
                AccountItemEmpty(),
                AccountItemEmpty(),
                PlaceHolder(),
                Title("Accounts"),
                AccountItem(name = "Account name 1", number = "700-001-11111"),
                AccountItem(name = "Account name 2", number = "700-001-22222"),
                AccountItem(name = "Account name 3", number = "700-001-33333"),
                AccountItem(name = "Account name 4", number = "700-001-44444"),
                AccountItem(name = "Account name 5", number = "700-001-55555"),
                AccountItem(name = "Account name 6", number = "700-001-66666"),
                AccountItem(name = "Account name 7", number = "700-001-77777"),
                AccountItem(name = "Account name 8", number = "700-001-88888"),
                AccountItem(name = "Account name 9", number = "700-001-99999"),
                AccountItem(name = "Account name 10", number = "700-001-10101")
            )
        }
    }
}