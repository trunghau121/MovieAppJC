package com.movieappjc.domain.entities

import android.os.Parcelable
import androidx.compose.runtime.Stable
import com.movieappjc.app.common.utils.KeyUtils
import kotlinx.parcelize.Parcelize

@Parcelize
@Stable
data class MovieDetailEntity(
    val id: Int,
    val title: String,
    val overview: String,
    val releaseDate: String,
    val duration: Int,
    val voteAverage: Double,
    val backdropPath: String,
    val posterPath: String,
    val genres: List<GenreEntity>
): Parcelable {
    fun getPosterUrl():String = "${KeyUtils.baseUrlImage()}${posterPath}"

    fun toMovie(): MovieEntity {
        return MovieEntity(
            id = id,
            title = title,
            posterPath = posterPath,
            backdropPath = backdropPath,
            releaseDate = releaseDate,
            voteAverage = voteAverage,
            overview = overview
        )
    }
}