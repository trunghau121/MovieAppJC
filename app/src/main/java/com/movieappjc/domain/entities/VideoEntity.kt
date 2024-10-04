package com.movieappjc.domain.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class VideoEntity(
    val key: String,
    val name: String,
    val type: String
): Parcelable {
    fun getThumbnail(): String = "https://i3.ytimg.com/vi_webp/${key}/hqdefault.webp"
}