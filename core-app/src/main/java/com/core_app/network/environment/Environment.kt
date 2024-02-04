package com.core_app.network.environment

import androidx.annotation.IntDef
import com.core_app.network.environment.Environment.Companion.DEVELOPMENT
import com.core_app.network.environment.Environment.Companion.PRODUCTION
import com.core_app.network.environment.Environment.Companion.STAGING

@IntDef(DEVELOPMENT, STAGING, PRODUCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class Environment {
    companion object {
        const val DEVELOPMENT = 1
        const val STAGING = 2
        const val PRODUCTION = 3
    }
}