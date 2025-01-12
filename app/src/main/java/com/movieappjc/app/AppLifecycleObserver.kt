package com.movieappjc.app

import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

class AppLifecycleObserver: DefaultLifecycleObserver {
    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        Log.d("AppLifecycleObserver", "App moved to foreground")
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        Log.d("AppLifecycleObserver", "App moved to background")
    }
}
