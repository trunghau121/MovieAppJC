package com.movieappjc.domain.entities

import android.os.Parcelable
import com.movieappjc.app.common.utils.KeyUtils

interface CastEntity : Parcelable {
    val id: Int
    val name: String
    val character: String
    val profilePath: String

    fun getProfileUrl(): String = "${KeyUtils.baseUrlImage200()}${profilePath}"
}