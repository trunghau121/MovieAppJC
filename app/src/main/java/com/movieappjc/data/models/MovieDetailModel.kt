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
    @param:Json(name = "adult")
    val adult: Boolean?,
    @param:Json(name = "backdrop_path")
    private val _backdropPath: String?,
    @param:Json(name = "budget")
    val budget: Int?,
    @param:Json(name = "homepage")
    val homepage: String?,
    @param:Json(name = "id")
    private val _id: Int?,
    @param:Json(name = "imdb_id")
    val imdbId: String?,
    @param:Json(name = "original_language")
    val originalLanguage: String?,
    @param:Json(name = "original_title")
    val originalTitle: String?,
    @param:Json(name = "overview")
    private val _overview: String?,
    @param:Json(name = "popularity")
    val popularity: Double?,
    @param:Json(name = "poster_path")
    private val _posterPath: String?,
    @param:Json(name = "release_date")
    private val _releaseDate: String?,
    @param:Json(name = "revenue")
    val revenue: Int?,
    @param:Json(name = "runtime")
    private val _duration: Int?,
    @param:Json(name = "status")
    val status: String?,
    @param:Json(name = "tagline")
    val tagline: String?,
    @param:Json(name = "title")
    private val _title: String?,
    @param:Json(name = "video")
    val video: Boolean?,
    @param:Json(name = "vote_average")
    private val _voteAverage: Double?,
    @param:Json(name = "vote_count")
    val voteCount: Int?,
    @param:Json(name = "genres")
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