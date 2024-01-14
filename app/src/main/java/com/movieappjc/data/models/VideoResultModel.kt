package com.movieappjc.data.models

import androidx.compose.runtime.Stable
import com.google.gson.annotations.SerializedName

@Stable
data class VideoResultModel(
    @SerializedName("results")
    val videos: List<VideoModel>
)