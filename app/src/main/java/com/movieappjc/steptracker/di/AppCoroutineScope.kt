package com.movieappjc.steptracker.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Inject
import javax.inject.Singleton

// Application-lifetime scope for work that must outlive a Service/Activity (e.g. the final DB write in onDestroy)
@Singleton
class AppCoroutineScope @Inject constructor() {
    val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
}
