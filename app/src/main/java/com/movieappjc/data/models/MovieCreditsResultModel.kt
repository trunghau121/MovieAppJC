package com.movieappjc.data.models

import androidx.compose.runtime.Stable
import com.squareup.moshi.Json

@Stable
data class MovieCreditsResultModel(
    @Json(name = "cast")
    val cast: List<MovieModel>
)