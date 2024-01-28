package com.movieappjc.data.models

import com.core_app.extension.value
import com.google.gson.annotations.SerializedName
import com.movieappjc.domain.entities.PersonEntity
import kotlinx.parcelize.Parcelize

@Parcelize
data class PersonModel(
    @SerializedName("adult")
    val adult: Boolean?,
    @SerializedName("biography")
    private val biography: String?,
    @SerializedName("birthday")
    private val birthday: String?,
    @SerializedName("gender")
    val gender: Int?,
    @SerializedName("id")
    private val id: Int?,
    @SerializedName("imdb_id")
    val imdbId: String?,
    @SerializedName("known_for_department")
    private val knownForDepartment: String?,
    @SerializedName("name")
    private val name: String?,
    @SerializedName("place_of_birth")
    private val placeOfBirth: String?,
    @SerializedName("popularity")
    private val popularity: Double?,
    @SerializedName("profile_path")
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