package com.movieappjc.data.models

import com.core_app.extension.value
import com.core_app.network.ResponseMapper
import com.movieappjc.domain.entities.CastEntity
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = false)
data class CastModel(
    @Json(name = "adult")
    val adult: Boolean?,
    @Json(name = "cast_id")
    val castId: Int?,
    @Json(name = "character")
    private val _character: String?,
    @Json(name = "credit_id")
    val creditId: String?,
    @Json(name = "gender")
    val gender: Int?,
    @Json(name = "id")
    private val _id: Int?,
    @Json(name = "known_for_department")
    val knownForDepartment: String?,
    @Json(name = "name")
    private val _name: String?,
    @Json(name = "order")
    val order: Int?,
    @Json(name = "original_name")
    val originalName: String?,
    @Json(name = "popularity")
    val popularity: Double?,
    @Json(name = "profile_path")
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