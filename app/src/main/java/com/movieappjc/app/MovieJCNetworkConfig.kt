package com.movieappjc.app

import com.core_app.base.app.NetworkConfig
import com.movieappjc.BuildConfig
import com.movieappjc.app.common.constants.Endpoints

class MovieJCNetworkConfig : NetworkConfig() {
    override fun baseUrl(): String {
        return Endpoints.apiUrl
    }
    override fun isDev(): Boolean {
        return BuildConfig.DEBUG
    }

    override fun isCache(): Boolean {
        return true
    }
}