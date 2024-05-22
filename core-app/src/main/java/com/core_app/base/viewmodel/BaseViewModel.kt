package com.core_app.base.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.core_app.navigation.AppNavigator
import com.core_app.network.DataState
import com.core_app.network.ErrorType
import com.core_app.network.HandelError
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class BaseViewModel(val appNavigator: AppNavigator) : ViewModel() {

    private val handler = CoroutineExceptionHandler { _, exception ->
        handleError(HandelError.getError(exception))
    }

    open fun handleError(error: Pair<ErrorType, String>) {}

    protected fun safeLaunch(block: suspend CoroutineScope.() -> Unit) {
        viewModelScope.launch(handler, block = block)
    }

    protected fun navigateTo(
        route: Any,
        popUpToRoute: String? = null,
        inclusive: Boolean = false,
        isSingleTop: Boolean = false,
    ) {
        safeLaunch {
            appNavigator.navigateTo(route, popUpToRoute, inclusive, isSingleTop)
        }
    }

    fun <T> executeTask(
        request: () -> Flow<T>,
        success: (T) -> Unit,
        error: (Throwable) -> Unit = {}
    ): Job {
        return viewModelScope.launch {
            request().catch { exception ->
                error(exception)
            }.collect { response ->
                success(response)
            }
        }
    }

    fun <T> executeTask(
        request: () -> Flow<DataState<T>>,
        onSuccess: MutableStateFlow<DataState<T>>
    ): Job {
        return executeTask(request, success = { data ->
            onSuccess.update { data }
        }, error = { exception ->
            onSuccess.update { DataState.Error(HandelError.getError(exception)) }
        })
    }

    fun onBack() {
        safeLaunch {
            appNavigator.navigateBack()
        }
    }
}
