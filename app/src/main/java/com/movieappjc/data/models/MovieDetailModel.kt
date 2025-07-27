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
    @field:Json(name = "adult")
    val adult: Boolean?,
    @field:Json(name = "backdrop_path")
    private val _backdropPath: String?,
    @field:Json(name = "budget")
    val budget: Int?,
    @field:Json(name = "homepage")
    val homepage: String?,
    @field:Json(name = "id")
    private val _id: Int?,
    @field:Json(name = "imdb_id")
    val imdbId: String?,
    @field:Json(name = "original_language")
    val originalLanguage: String?,
    @field:Json(name = "original_title")
    val originalTitle: String?,
    @field:Json(name = "overview")
    private val _overview: String?,
    @field:Json(name = "popularity")
    val popularity: Double?,
    @field:Json(name = "poster_path")
    private val _posterPath: String?,
    @field:Json(name = "release_date")
    private val _releaseDate: String?,
    @field:Json(name = "revenue")
    val revenue: Int?,
    @field:Json(name = "runtime")
    private val _duration: Int?,
    @field:Json(name = "status")
    val status: String?,
    @field:Json(name = "tagline")
    val tagline: String?,
    @field:Json(name = "title")
    private val _title: String?,
    @field:Json(name = "video")
    val video: Boolean?,
    @field:Json(name = "vote_average")
    private val _voteAverage: Double?,
    @field:Json(name = "vote_count")
    val voteCount: Int?,
    @field:Json(name = "genres")
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