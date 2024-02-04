package com.movieappjc.data.models

import androidx.compose.runtime.Stable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = false)
@Stable
data class CastResultModel(
    @Json(name = "cast")
    val cast: List<CastModel>
)