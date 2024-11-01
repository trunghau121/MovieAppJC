package com.core_app.extension

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable

typealias EmptyFunc = () -> Unit
typealias ParamFunc<T> = (T) -> Unit
typealias ParamFunc2<I1, I2> = (I1, I2) -> Unit
typealias ParamSuspend<I, O> = suspend (I) -> O
typealias EmptySuspend<O> = suspend () -> O
typealias EmptyCompose = @Composable () -> Unit
typealias ParamCompose<T> = @Composable (T) -> Unit
typealias EmptyColumn = @Composable ColumnScope.() -> Unit
typealias ParamColumn<T> = @Composable ColumnScope.(T) -> Unit

typealias EmptyRow = @Composable RowScope.() -> Unit
typealias ParamsCompose<T, R> = @Composable (T, R) -> Unit