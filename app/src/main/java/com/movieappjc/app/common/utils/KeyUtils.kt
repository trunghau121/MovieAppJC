package com.movieappjc.app.common.utils

object KeyUtils {
    init {
        System.loadLibrary("secure-values")
    }
    external fun apiKey() : String
    external fun baseUrl() : String
    external fun baseUrlImage() : String
    external fun baseUrlImage200() : String
    external fun urlOriginalImage() : String
}