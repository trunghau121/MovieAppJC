package com.movieappjc.data.models

import androidx.compose.runtime.Stable
import com.squareup.moshi.Json

@Stable
data class VideoResultModel(
    @Json(name = "results")
    val videos: List<VideoModel>
)