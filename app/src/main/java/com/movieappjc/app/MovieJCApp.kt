package com.movieappjc.app

import com.core_app.base.app.AppInitializer
import com.core_app.base.app.CoreApplication
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration

@HiltAndroidApp
class MovieJCApp : CoreApplication(), Configuration.Provider {
    @Inject
    lateinit var initializer: AppInitializer

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        initializer.init(this)
    }
}