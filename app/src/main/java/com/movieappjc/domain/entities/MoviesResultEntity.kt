package com.movieappjc.domain.entities

data class MoviesResultEntity(
    val currentPage: Int,
    val data: List<MovieEntity>,
    val totalCountPages: Int
)