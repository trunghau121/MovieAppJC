package com.movieappjc.data.models

import com.google.gson.annotations.SerializedName
import com.movieappjc.data.local.MovieTable
import com.movieappjc.domain.entities.MovieDetailEntity
import com.movieappjc.domain.entities.MovieEntity

data class MovieDetailModel(
    @SerializedName("adult")
    val adult: Boolean?,
    @SerializedName("backdrop_path")
    override val backdropPath: String,
    @SerializedName("budget")
    val budget: Int?,
    @SerializedName("homepage")
    val homepage: String?,
    @SerializedName("id")
    override val id: Int,
    @SerializedName("imdb_id")
    val imdbId: String?,
    @SerializedName("original_language")
    val originalLanguage: String?,
    @SerializedName("original_title")
    val originalTitle: String?,
    @SerializedName("overview")
    override val overview: String,
    @SerializedName("popularity")
    val popularity: Double?,
    @SerializedName("poster_path")
    override val posterPath: String,
    @SerializedName("release_date")
    override val releaseDate: String,
    @SerializedName("revenue")
    val revenue: Int?,
    @SerializedName("runtime")
    override val duration: Int,
    @SerializedName("status")
    val status: String?,
    @SerializedName("tagline")
    val tagline: String?,
    @SerializedName("title")
    override val title: String,
    @SerializedName("video")
    val video: Boolean?,
    @SerializedName("vote_average")
    override val voteAverage: Double,
    @SerializedName("vote_count")
    val voteCount: Int?
): MovieDetailEntity {
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