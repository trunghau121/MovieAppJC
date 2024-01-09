package com.core_app.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

abstract class BaseRepository {
    protected fun <T> getResult(request: suspend () -> T): Flow<Resource<T>> {
        return flow {
            emit(Resource.Success(request()))
        }.flowOn(Dispatchers.IO)
    }
}