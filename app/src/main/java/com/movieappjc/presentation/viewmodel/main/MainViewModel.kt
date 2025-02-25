package com.movieappjc.presentation.viewmodel.main

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import com.core_app.navigation.AppNavigator
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@Stable
@HiltViewModel
class MainViewModel @Inject constructor(
    val appNavigator: AppNavigator
) : ViewModel() {
    val navigationChannel = appNavigator.navigationChannel

    override fun onCleared() {
        navigationChannel.close()
        super.onCleared()
    }
}