package com.movieappjc.domain.entities

import android.os.Parcelable
import com.movieappjc.app.common.constants.Endpoints

interface CastEntity : Parcelable {
    val id: Int
    val name: String
    val character: String
    val profilePath: String

    fun getProfileUrl(): String = "${Endpoints.baseUrlImage200}${profilePath}"
}