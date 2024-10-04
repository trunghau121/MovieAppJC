package com.movieappjc.data.models

import androidx.compose.runtime.Stable
import com.core_app.extension.value
import com.core_app.network.ResponseMapper
import com.movieappjc.domain.entities.MovieEntity
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = false)
@Stable
data class MovieCreditsResultModel(
    @Json(name = "cast")
    val cast: List<MovieModel>?= null
): ResponseMapper<List<MovieEntity>> {
    override fun mapTo(): List<MovieEntity> {
        return cast.value().map {
            it.mapTo()
        }
    }

}