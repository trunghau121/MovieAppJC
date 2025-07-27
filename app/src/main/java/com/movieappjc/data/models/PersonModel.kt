package com.movieappjc.data.models

import com.core_app.extension.value
import com.squareup.moshi.Json
import com.movieappjc.domain.entities.PersonEntity
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@JsonClass(generateAdapter = false)
@Parcelize
data class PersonModel(
    @param:Json(name = "adult")
    val adult: Boolean?,
    @param:Json(name = "biography")
    private val biography: String?,
    @param:Json(name = "birthday")
    private val birthday: String?,
    @param:Json(name = "gender")
    val gender: Int?,
    @param:Json(name = "id")
    private val id: Int?,
    @param:Json(name = "imdb_id")
    val imdbId: String?,
    @param:Json(name = "known_for_department")
    private val knownForDepartment: String?,
    @param:Json(name = "name")
    private val name: String?,
    @param:Json(name = "place_of_birth")
    private val placeOfBirth: String?,
    @param:Json(name = "popularity")
    private val popularity: Double?,
    @param:Json(name = "profile_path")
    private val profilePath: String?
): PersonEntity(){
    override fun getId(): Int = id.value()

    override fun getName(): String = name.value()

    override fun getProfilePath(): String = profilePath.value()

    override fun getBirthday(): String = birthday.value()

    override fun getPlaceOfBirth(): String = placeOfBirth.value()

    override fun getBiography(): String = biography.value()

    override fun getKnownForDepartment(): String = knownForDepartment.value()

}