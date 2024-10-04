package com.core_app.network

interface ResponseMapper<out O> {
    fun mapTo(): O
}