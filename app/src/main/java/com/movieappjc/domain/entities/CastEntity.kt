package com.movieappjc.domain.entities

import android.os.Parcelable
import com.movieappjc.app.common.utils.KeyUtils
import kotlinx.parcelize.Parcelize

@Parcelize
data class CastEntity(
    val id: Int,
    val name: String,
    val character: String,
    val profilePath: String
): Parcelable {
    fun getProfileUrl(): String = "${KeyUtils.baseUrlImage200()}${profilePath}"
}