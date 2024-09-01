package com.movieappjc.app.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.UiComposable
import com.core_app.network.DataState

@Composable
fun <T> State<DataState<T>>.ToUI(
    content: @Composable @UiComposable (T) -> Unit,
    onRetry: () -> Unit = {}
) = this.value.ToUI(content, onRetry)

@Composable
fun <T> DataState<T>.ToUI(
    content: @Composable @UiComposable (T) -> Unit,
    onRetry: () -> Unit = {},
    loading: @Composable @UiComposable () -> Unit = {
        CircularProgressBar()
    },
    error: @Composable @UiComposable (DataState.Error<T>) -> Unit = {
        ErrorApp(error = it.error, onRetry = onRetry)
    },
) {
    when (val state = this) {
        is DataState.Success -> {
            content(state.data)
        }

        is DataState.Error -> {
            error(state.error)
        }

        is DataState.Loading -> {
            loading()
        }

        else -> {}
    }
}