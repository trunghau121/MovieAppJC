package com.movieappjc.data.models

import androidx.compose.runtime.Stable
import com.core_app.extension.value
import com.core_app.network.ResponseMapper
import com.squareup.moshi.Json
import com.movieappjc.domain.entities.GenreEntity
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = false)
@Stable
data class GenreModel(
    @param:Json(name = "id")
    val id: Int? = null,
    @param:Json(name = "name")
    val name: String? = null
) : ResponseMapper<GenreEntity> {
    override fun mapTo(): GenreEntity {
        return GenreEntity(id = id.value(), name = name.value())
    }
}