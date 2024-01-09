package com.movieappjc.data.models

import com.google.gson.annotations.SerializedName

data class VideoResultModel(
    @SerializedName("results")
    val videos: List<VideoModel>
)