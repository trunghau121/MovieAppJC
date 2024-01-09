package com.movieappjc.data.models

import com.google.gson.annotations.SerializedName
import com.movieappjc.domain.entities.CastEntity

data class CastModel(
    @SerializedName("adult")
    val adult: Boolean?,
    @SerializedName("cast_id")
    val castId: Int?,
    @SerializedName("character")
    override val character: String,
    @SerializedName("credit_id")
    val creditId: String?,
    @SerializedName("gender")
    val gender: Int?,
    @SerializedName("id")
    override val id: Int,
    @SerializedName("known_for_department")
    val knownForDepartment: String?,
    @SerializedName("name")
    override val name: String,
    @SerializedName("order")
    val order: Int?,
    @SerializedName("original_name")
    val originalName: String?,
    @SerializedName("popularity")
    val popularity: Double?,
    @SerializedName("profile_path")
    override val profilePath: String
): CastEntity