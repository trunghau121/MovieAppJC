package com.movieappjc.data.models

import androidx.compose.runtime.Stable
import com.squareup.moshi.Json

@Stable
data class CastResultModel(
    @Json(name = "cast")
    val cast: List<CastModel>
)