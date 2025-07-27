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
    @param:Json(name = "page")
    private val currentPage: Int?,
    @param:Json(name = "results")
    private val data: List<MovieModel>?,
    @param:Json(name = "total_pages")
    private val totalCountPages: Int?,
) : ResponseMapper<MoviesResultEntity> {
    override fun mapTo(): MoviesResultEntity {
        return MoviesResultEntity(
            currentPage = currentPage.value(),
            data = data.value().map { it.mapTo() },
            totalCountPages = totalCountPages.value()
        )
    }
}