package com.movieappjc.domain.entities

interface MovieEntity{
    val posterPath: String
    val id: Int
    val backdropPath: String
    val title: String
    val voteAverage: Double
    val releaseDate: String
    val overview: String
}