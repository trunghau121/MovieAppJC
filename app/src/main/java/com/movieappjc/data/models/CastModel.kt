package com.movieappjc.data.models

import com.core_app.extension.value
import com.movieappjc.domain.entities.CastEntity
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@JsonClass(generateAdapter = false)
@Parcelize
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
): CastEntity {
    override val id: Int get() = _id.value()
    override val name: String get() = _name.value()
    override val character: String get() = _character.value()
    override val profilePath: String get() = _profilePath.value()
}