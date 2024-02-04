package com.movieappjc.data.models

import androidx.compose.runtime.Stable
import com.core_app.extension.value
import com.movieappjc.domain.entities.MovieEntity
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@JsonClass(generateAdapter = false)
@Stable
@Parcelize
data class MovieModel(
    @Json(name = "adult")
    val adult: Boolean?,
    @Json(name = "backdrop_path")
    private val _backdropPath: String?,
    @Json(name = "genre_ids")
    val genreIds: List<Int?>?,
    @Json(name = "id")
    private val _id: Int?,
    @Json(name = "media_type")
    val mediaType: String?,
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
    @Json(name = "title")
    private val _title: String?,
    @Json(name = "video")
    val video: Boolean?,
    @Json(name = "vote_average")
    private val _voteAverage: Double?,
    @Json(name = "vote_count")
    val voteCount: Int?
): MovieEntity{
    override val backdropPath: String get() = _backdropPath.value()
    override val title: String get() = _title.value()
    override val voteAverage: Double get() = _voteAverage.value()
    override val releaseDate: String get() = _releaseDate.value()
    override val overview: String get() = _overview.value()
    override val posterPath: String get() = _posterPath.value()
    override val id: Int get() = _id.value()

}