package com.movieappjc.data.models

import androidx.compose.runtime.Stable
import com.core_app.network.ResponseMapper
import com.movieappjc.domain.entities.CastEntity
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = false)
@Stable
data class CastResultModel(
    @param:Json(name = "cast")
    val cast: List<CastModel>
): ResponseMapper<List<CastEntity>>{
    override fun mapTo(): List<CastEntity> {
        return cast.map { it.mapTo() }
    }

}