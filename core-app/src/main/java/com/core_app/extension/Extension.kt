package com.core_app.extension

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.math.BigDecimal


fun Int?.value(def: Int = 0) = this ?: def

fun Boolean?.value(def: Boolean = false) = this ?: def

fun String?.value(def: String = "") = this ?: def

fun Float?.value(def: Float = 0f) = this ?: def

fun Double?.value(def: Double = 0.0) = this ?: def

fun Long?.value(def: Long = 0) = this ?: def

fun BigDecimal?.value(def: BigDecimal = BigDecimal.ZERO) = this ?: def

fun <T> List<T>?.value() = this ?: emptyList()

@Composable
fun Int.pxToDp(): Dp {
    return with(LocalDensity.current) { this@pxToDp.toDp() }
}

@Composable
fun Dp.dpToPx(): Float {
    return with(LocalDensity.current) { this@dpToPx.toPx() }
}

@Composable
fun <T> T.useDebounce(
    delayMillis: Long = 300L,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    onChange: (T) -> Unit
): T {
    val state by rememberUpdatedState(this)
    DisposableEffect(state) {
        val job = coroutineScope.launch {
            delay(delayMillis)
            onChange(state)
        }
        onDispose {
            job.cancel()
        }
    }
    return state
}