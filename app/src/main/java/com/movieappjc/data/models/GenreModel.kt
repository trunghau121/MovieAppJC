package com.movieappjc.data.models

import androidx.compose.runtime.Stable
import com.google.gson.annotations.SerializedName
import com.movieappjc.domain.entities.GenreEntity
import kotlinx.parcelize.Parcelize

@Stable
@Parcelize
data class GenreModel(
    @SerializedName("id")
    override val id: Int,
    @SerializedName("name")
    override val name: String
): GenreEntity