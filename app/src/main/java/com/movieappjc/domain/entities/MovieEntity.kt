package com.movieappjc.domain.entities

import android.os.Parcelable
import com.movieappjc.app.common.constants.Endpoints

interface MovieEntity : Parcelable {
    val posterPath: String
    val id: Int
    val backdropPath: String
    val title: String
    val voteAverage: Double
    val releaseDate: String
    val overview: String

    fun getPosterUrl():String = "${Endpoints.baseUrlImage}${posterPath}"
    fun getBackdropUrl():String = "${Endpoints.baseUrlImage200}${backdropPath}"
}