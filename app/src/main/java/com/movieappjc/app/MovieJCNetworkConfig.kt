package com.movieappjc.app

import com.core_app.base.app.NetworkConfig
import com.movieappjc.BuildConfig
import com.movieappjc.app.common.utils.KeyUtils

class MovieJCNetworkConfig : NetworkConfig() {
    override fun baseUrl(): String {
        return KeyUtils.baseUrl()
    }
    override fun isDev(): Boolean {
        return BuildConfig.DEBUG
    }

    override fun isCache(): Boolean {
        return true
    }
}