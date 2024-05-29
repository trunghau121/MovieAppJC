package com.movieappjc.domain.entities

import android.os.Parcelable
import com.movieappjc.app.common.utils.KeyUtils
import kotlinx.parcelize.Parcelize

interface CastEntity : Parcelable {
    val id: Int
    val name: String
    val character: String
    val profilePath: String

    fun getProfileUrl(): String = "${KeyUtils.baseUrlImage200()}${profilePath}"
}

@Parcelize
class CastExample: CastEntity{
    override val id: Int = 100
    override val name: String = "AAAA"
    override val character: String = "AAA"
    override val profilePath: String = "AAAAA"
}