package com.movieappjc.data.models

import com.google.gson.annotations.SerializedName

data class CastResultModel(
    @SerializedName("cast")
    val cast: List<CastModel>
)