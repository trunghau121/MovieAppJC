package com.movieappjc.data.models

import com.core_app.extension.value
import com.core_app.network.ResponseMapper
import com.movieappjc.domain.entities.CastEntity
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = false)
data class CastModel(
    @field:Json(name = "adult")
    val adult: Boolean?,
    @field:Json(name = "cast_id")
    val castId: Int?,
    @field:Json(name = "character")
    private val _character: String?,
    @field:Json(name = "credit_id")
    val creditId: String?,
    @field:Json(name = "gender")
    val gender: Int?,
    @field:Json(name = "id")
    private val _id: Int?,
    @field:Json(name = "known_for_department")
    val knownForDepartment: String?,
    @field:Json(name = "name")
    private val _name: String?,
    @field:Json(name = "order")
    val order: Int?,
    @field:Json(name = "original_name")
    val originalName: String?,
    @field:Json(name = "popularity")
    val popularity: Double?,
    @field:Json(name = "profile_path")
    private val _profilePath: String?
) : ResponseMapper<CastEntity> {
    override fun mapTo(): CastEntity {
        return CastEntity(
            id = _id.value(),
            name = _name.value(),
            character = _character.value(),
            profilePath = _profilePath.value()
        )
    }
}