package com.movieappjc.data.models

import androidx.compose.runtime.Stable
import com.squareup.moshi.Json
import com.movieappjc.domain.entities.GenreEntity
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@JsonClass(generateAdapter = false)
@Stable
@Parcelize
data class GenreModel(
    @Json(name = "id")
    override val id: Int,
    @Json(name = "name")
    override val name: String
): GenreEntity