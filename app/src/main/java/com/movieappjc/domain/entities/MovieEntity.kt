package com.movieappjc.domain.entities

import android.os.Parcelable
import com.movieappjc.app.common.utils.KeyUtils

interface MovieEntity : Parcelable {
    val posterPath: String
    val id: Int
    val backdropPath: String
    val title: String
    val voteAverage: Double
    val releaseDate: String
    val overview: String

    fun getPosterUrl():String = "${KeyUtils.baseUrlImage()}${posterPath}"
    fun getBackdropUrl():String = "${KeyUtils.baseUrlImage200()}${backdropPath}"
}