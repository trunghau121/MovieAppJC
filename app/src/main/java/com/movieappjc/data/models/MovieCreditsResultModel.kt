package com.movieappjc.data.models

import androidx.compose.runtime.Stable
import com.google.gson.annotations.SerializedName

@Stable
data class MovieCreditsResultModel(
    @SerializedName("cast")
    val cast: List<MovieModel>
)