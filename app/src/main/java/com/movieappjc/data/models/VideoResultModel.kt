package com.movieappjc.data.models

import androidx.compose.runtime.Stable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = false)
@Stable
data class VideoResultModel(
    @Json(name = "results")
    val videos: List<VideoModel>
)