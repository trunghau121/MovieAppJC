package com.movieappjc.data.models

import androidx.compose.runtime.Stable
import com.core_app.extension.value
import com.squareup.moshi.Json
import com.movieappjc.data.local.MovieTable
import com.movieappjc.domain.entities.GenreEntity
import com.movieappjc.domain.entities.MovieDetailEntity
import com.movieappjc.domain.entities.MovieEntity
import kotlinx.parcelize.Parcelize

@Parcelize
@Stable
data class MovieDetailModel(
    @Json(name = "adult")
    val adult: Boolean?,
    @Json(name = "backdrop_path")
    private val _backdropPath: String?,
    @Json(name = "budget")
    val budget: Int?,
    @Json(name = "homepage")
    val homepage: String?,
    @Json(name = "id")
    private val _id: Int?,
    @Json(name = "imdb_id")
    val imdbId: String?,
    @Json(name = "original_language")
    val originalLanguage: String?,
    @Json(name = "original_title")
    val originalTitle: String?,
    @Json(name = "overview")
    private val _overview: String?,
    @Json(name = "popularity")
    val popularity: Double?,
    @Json(name = "poster_path")
    private val _posterPath: String?,
    @Json(name = "release_date")
    private val _releaseDate: String?,
    @Json(name = "revenue")
    val revenue: Int?,
    @Json(name = "runtime")
    private val _duration: Int?,
    @Json(name = "status")
    val status: String?,
    @Json(name = "tagline")
    val tagline: String?,
    @Json(name = "title")
    private val _title: String?,
    @Json(name = "video")
    val video: Boolean?,
    @Json(name = "vote_average")
    private val _voteAverage: Double?,
    @Json(name = "vote_count")
    val voteCount: Int?,
    @Json(name = "genres")
    private val _genres: List<GenreModel>?
): MovieDetailEntity {
    override val id: Int get() = _id.value()
    override val title: String get() = _title.value()
    override val overview: String get() = _overview.value()
    override val releaseDate: String get() = _releaseDate.value()
    override val duration: Int get() = _duration.value()
    override val voteAverage: Double get() = _voteAverage.value()
    override val backdropPath: String get() = _backdropPath.value()
    override val posterPath: String get() = _posterPath.value()
    override val genres: List<GenreEntity> get() = _genres.value()

    override fun toMovie(): MovieEntity {
        return MovieTable(
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