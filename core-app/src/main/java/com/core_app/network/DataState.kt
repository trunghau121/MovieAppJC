package com.core_app.network

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.transform

abstract class DataState<T> {
    class Success<T>(val data: T) : DataState<T>()
    class Error<T>(val error: Pair<ErrorType, String>) : DataState<T>()
    class Loading<T> : DataState<T>()
    class InitState<T> : DataState<T>()

    inline fun <R : Any> map(transform: (T) -> R): DataState<R> {
        return when (this) {
            is Error -> Error(this.error)
            is Success -> Success(transform(this.data))
            else -> {
                Loading()
            }
        }
    }

    suspend inline fun <R : Any> suspendMap(crossinline transform: suspend (T) -> R): DataState<R> {
        return when (this) {
            is Error -> Error(this.error)
            is Success -> Success(transform(this.data))
            else -> {
                Loading()
            }
        }
    }
}

fun <T : Any> Flow<T>.asStatefulData(): Flow<DataState<T>> = wrapWithStatefulData().catch {
    emit(DataState.Error(HandelError.getError(it)))
}

fun <T : Any> Flow<T>.wrapWithStatefulData(): Flow<DataState<T>> = transform { value ->
    return@transform emit(DataState.Success(value))
}

inline fun <T : Any, R : Any> Flow<DataState<T>>.mapState(
    crossinline transform: suspend (value: T) -> R
): Flow<DataState<R>> = transform { value ->
    return@transform emit(value.suspendMap(transform))
}

inline fun <T : Any> Flow<DataState<T>>.onSuccessState(
    crossinline action: suspend (value: T) -> Unit
): Flow<DataState<T>> = onEach {
    if (it is DataState.Success) action(it.data)
}

inline fun <T : Any> Flow<DataState<T>>.onErrorState(
    crossinline action: suspend (error: Pair<ErrorType, String>) -> Unit
): Flow<DataState<T>> = onEach {
    if (it is DataState.Error) action(it.error)
}
