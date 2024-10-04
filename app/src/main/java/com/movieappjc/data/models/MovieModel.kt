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
    @Json(name = "adult")
    val adult: Boolean? = null,
    @Json(name = "backdrop_path")
    private val _backdropPath: String? = null,
    @Json(name = "genre_ids")
    val genreIds: List<Int?>? = null,
    @Json(name = "id")
    private val _id: Int? = null,
    @Json(name = "media_type")
    val mediaType: String? = null,
    @Json(name = "original_language")
    val originalLanguage: String? = null,
    @Json(name = "original_title")
    val originalTitle: String? = null,
    @Json(name = "overview")
    private val _overview: String? = null,
    @Json(name = "popularity")
    val popularity: Double? = null,
    @Json(name = "poster_path")
    private val _posterPath: String? = null,
    @Json(name = "release_date")
    private val _releaseDate: String? = null,
    @Json(name = "title")
    private val _title: String? = null,
    @Json(name = "video")
    val video: Boolean? = null,
    @Json(name = "vote_average")
    private val _voteAverage: Double? = null,
    @Json(name = "vote_count")
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