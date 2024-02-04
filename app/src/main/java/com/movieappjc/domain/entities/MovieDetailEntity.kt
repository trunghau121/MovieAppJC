package com.movieappjc.domain.entities

import android.os.Parcelable
import androidx.compose.runtime.Stable
import com.movieappjc.app.common.constants.Endpoints

@Stable
interface MovieDetailEntity : Parcelable {
    val id: Int
    val title: String
    val overview: String
    val releaseDate: String
    val duration: Int
    val voteAverage: Double
    val backdropPath: String
    val posterPath: String
    val genres: List<GenreEntity>

    fun getPosterUrl():String = "${Endpoints.baseUrlImage}${posterPath}"
    fun toMovie(): MovieEntity
}