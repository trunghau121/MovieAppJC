package com.movieappjc.data.models

import androidx.compose.runtime.Stable
import com.core_app.extension.value
import com.core_app.network.ResponseMapper
import com.squareup.moshi.Json
import com.movieappjc.domain.entities.MoviesResultEntity
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = false)
@Stable
class MoviesResultModel(
    @Json(name = "page")
    private val _currentPage: Int?,
    @Json(name = "results")
    private val _data: List<MovieModel>?,
    @Json(name = "total_pages")
    private val _totalCountPages: Int?,
) : ResponseMapper<MoviesResultEntity> {
    override fun mapTo(): MoviesResultEntity {
        return MoviesResultEntity(
            currentPage = _currentPage.value(),
            data = _data.value().map { it.mapTo() },
            totalCountPages = _totalCountPages.value()
        )
    }
}