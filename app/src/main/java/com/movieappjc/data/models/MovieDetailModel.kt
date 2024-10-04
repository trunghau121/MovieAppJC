package com.movieappjc.data.models

import androidx.compose.runtime.Stable
import com.core_app.extension.value
import com.core_app.network.ResponseMapper
import com.squareup.moshi.Json
import com.movieappjc.domain.entities.GenreEntity
import com.movieappjc.domain.entities.MovieDetailEntity
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = false)
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
) : ResponseMapper<MovieDetailEntity> {

    private fun toGenres(): List<GenreEntity> {
        return _genres.value().map {
            it.mapTo()
        }
    }

    override fun mapTo(): MovieDetailEntity {
        return MovieDetailEntity(
            id = _id.value(),
            title = _title.value(),
            overview = _overview.value(),
            releaseDate = _releaseDate.value(),
            duration = _duration.value(),
            voteAverage = _voteAverage.value(),
            backdropPath = _backdropPath.value(),
            posterPath = _posterPath.value(),
            genres = toGenres()
        )
    }
}