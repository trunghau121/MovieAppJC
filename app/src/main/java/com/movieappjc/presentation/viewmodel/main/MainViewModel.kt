package com.movieappjc.presentation.viewmodel.main

import androidx.lifecycle.ViewModel
import com.core_app.navigation.AppNavigator

class MainViewModel(
    appNavigator: AppNavigator
) : ViewModel() {
    val navigationChannel = appNavigator.navigationChannel

    override fun onCleared() {
        navigationChannel.close()
        super.onCleared()
    }
}