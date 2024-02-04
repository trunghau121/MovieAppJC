package com.core_app.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

abstract class BaseRepository {
    protected fun <T> getResult(request: suspend () -> T): Flow<DataState<T>> {
        return flow {
            emit(DataState.Success(request()))
        }.flowOn(Dispatchers.IO)
    }
}