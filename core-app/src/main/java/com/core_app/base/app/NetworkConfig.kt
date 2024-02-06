package com.core_app.base.app

abstract class NetworkConfig {
    abstract fun baseUrl(): String

    open fun isDev() = false

    open fun isCache() = false
}