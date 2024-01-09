package com.core_app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.core_app.repository.HandelError
import com.core_app.repository.Resource
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

abstract class BaseViewModel : ViewModel() {
    open fun <T> executeTask(
        request: () -> Flow<T>,
        success: (T) -> Unit,
        error: (Throwable) -> Unit = {},
        isShowError: Boolean = true
    ): Job {
        return viewModelScope.launch {
            request().catch { exception ->
                handleError(isShowError, exception, error)
            }.collect { response ->
                success(response)
            }
        }
    }

    protected fun <T> executeTask(
        request: () -> Flow<Resource<T>>,
        onSuccess: MutableStateFlow<Resource<T>>? = null,
        isShowError: Boolean = true
    ): Job {
        return executeTask(request, success = {
            onSuccess?.value = it
        }, error = { exception ->
            onSuccess?.value = Resource.Error(HandelError().getError(exception))
        }, isShowError)
    }

    private fun handleError(
        isShowError: Boolean,
        throwable: Throwable,
        onError: (Throwable) -> Unit
    ) {
        if (isShowError) {
            onError(throwable)
        }
    }
}
