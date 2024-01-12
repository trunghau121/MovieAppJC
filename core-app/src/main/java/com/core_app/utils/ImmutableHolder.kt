package com.core_app.utils

import androidx.compose.runtime.Immutable

@Immutable
class ImmutableHolder<T>(val item: T) {
    operator fun invoke(): T = item
}