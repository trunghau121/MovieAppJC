package com.movieappjc.data.models

import com.core_app.extension.value
import com.core_app.network.ResponseMapper
import com.movieappjc.domain.entities.CastEntity
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = false)
data class CastModel(
    @param:Json(name = "adult")
    val adult: Boolean?,
    @param:Json(name = "cast_id")
    val castId: Int?,
    @param:Json(name = "character")
    private val _character: String?,
    @param:Json(name = "credit_id")
    val creditId: String?,
    @param:Json(name = "gender")
    val gender: Int?,
    @param:Json(name = "id")
    private val _id: Int?,
    @param:Json(name = "known_for_department")
    val knownForDepartment: String?,
    @param:Json(name = "name")
    private val _name: String?,
    @param:Json(name = "order")
    val order: Int?,
    @param:Json(name = "original_name")
    val originalName: String?,
    @param:Json(name = "popularity")
    val popularity: Double?,
    @param:Json(name = "profile_path")
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