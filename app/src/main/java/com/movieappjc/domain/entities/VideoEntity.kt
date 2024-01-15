package com.movieappjc.domain.entities

import android.os.Parcelable

interface VideoEntity : Parcelable {
    val key: String
    val name: String
    val type: String

    fun getThumbnail(): String = "https://i3.ytimg.com/vi_webp/${key}/hqdefault.webp"
}