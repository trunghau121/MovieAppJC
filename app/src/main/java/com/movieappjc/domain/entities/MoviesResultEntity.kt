package com.movieappjc.domain.entities

interface MoviesResultEntity {
    val currentPage: Int
    val data: List<MovieEntity>
    val totalCountPages: Int
}