package com.movieappjc.data.models

import androidx.compose.runtime.Stable
import com.core_app.extension.value
import com.core_app.network.ResponseMapper
import com.movieappjc.domain.entities.MovieEntity
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = false)
@Stable
data class MovieModel(
    @field:Json(name = "adult")
    val adult: Boolean? = null,
    @field:Json(name = "backdrop_path")
    private val _backdropPath: String? = null,
    @field:Json(name = "genre_ids")
    val genreIds: List<Int?>? = null,
    @field:Json(name = "id")
    private val _id: Int? = null,
    @field:Json(name = "media_type")
    val mediaType: String? = null,
    @field:Json(name = "original_language")
    val originalLanguage: String? = null,
    @field:Json(name = "original_title")
    val originalTitle: String? = null,
    @field:Json(name = "overview")
    private val _overview: String? = null,
    @field:Json(name = "popularity")
    val popularity: Double? = null,
    @field:Json(name = "poster_path")
    private val _posterPath: String? = null,
    @field:Json(name = "release_date")
    private val _releaseDate: String? = null,
    @field:Json(name = "title")
    private val _title: String? = null,
    @field:Json(name = "video")
    val video: Boolean? = null,
    @field:Json(name = "vote_average")
    private val _voteAverage: Double? = null,
    @field:Json(name = "vote_count")
    val voteCount: Int? = null
) : ResponseMapper<MovieEntity> {
    override fun mapTo(): MovieEntity {
        return MovieEntity(
            backdropPath = _backdropPath.value(),
            title = _title.value(),
            voteAverage = _voteAverage.value(),
            releaseDate = _releaseDate.value(),
            overview = _overview.value(),
            posterPath = _posterPath.value(),
            id = _id.value()
        )
    }

}