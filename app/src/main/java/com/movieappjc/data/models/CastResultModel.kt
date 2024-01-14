package com.movieappjc.data.models

import androidx.compose.runtime.Stable
import com.google.gson.annotations.SerializedName

@Stable
data class CastResultModel(
    @SerializedName("cast")
    val cast: List<CastModel>
)