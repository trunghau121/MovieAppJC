package com.movieappjc.app.common.localization

import java.util.concurrent.atomic.AtomicInteger

private val resUID = AtomicInteger(Int.MIN_VALUE)

internal fun generateUID(): Int = resUID.incrementAndGet()