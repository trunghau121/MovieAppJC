package com.movieappjc.domain.entities

import android.os.Parcelable

interface MoviesResultEntity : Parcelable {
    val currentPage: Int
    val data: List<MovieEntity>
    val totalCountPages: Int
}