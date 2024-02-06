package com.movieappjc.app

import com.core_app.base.app.AppInitializer
import com.core_app.base.app.CoreApplication
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MovieJCApp : CoreApplication(){
    @Inject
    lateinit var initializer: AppInitializer

    override fun onCreate() {
        super.onCreate()
        initializer.init(this)
    }
}