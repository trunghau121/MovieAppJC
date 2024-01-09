package com.core_app.repository

abstract class Resource<T> {
    class Success<T>(val data: T) : Resource<T>()
    class Error<T>(val error: Pair<ErrorType, String>) : Resource<T>()
    class Loading<T> : Resource<T>()
}
