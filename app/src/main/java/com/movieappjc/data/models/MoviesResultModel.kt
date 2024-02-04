package com.movieappjc.data.models

import androidx.compose.runtime.Stable
import com.core_app.extension.value
import com.movieappjc.domain.entities.MovieEntity
import com.squareup.moshi.Json
import com.movieappjc.domain.entities.MoviesResultEntity

@Stable
class MoviesResultModel(
    @Json(name = "page")
    private val _currentPage: Int?,
    @Json(name = "results")
    private val _data: List<MovieModel>?,
    @Json(name = "total_pages")
    private val _totalCountPages: Int?,
): MoviesResultEntity {
    override val currentPage: Int get() = _currentPage.value()
    override val data: List<MovieEntity> get() = _data.value()
    override val totalCountPages: Int get() = _totalCountPages.value()
}