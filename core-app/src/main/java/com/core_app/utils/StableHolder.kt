package com.core_app.utils

import androidx.compose.runtime.Stable

@Stable
class StableHolder<T>(val item: T) {
    operator fun invoke(): T = item
}