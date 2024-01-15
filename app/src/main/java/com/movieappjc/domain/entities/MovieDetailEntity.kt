package com.movieappjc.domain.entities

import android.os.Parcelable

interface MovieDetailEntity : Parcelable {
    val id: Int
    val title: String
    val overview: String
    val releaseDate: String
    val duration: Int
    val voteAverage: Double
    val backdropPath: String
    val posterPath: String
    fun toMovie(): MovieEntity
}