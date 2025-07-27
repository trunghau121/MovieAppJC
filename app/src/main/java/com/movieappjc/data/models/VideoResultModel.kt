package com.movieappjc.data.models

import androidx.compose.runtime.Stable
import com.core_app.extension.value
import com.core_app.network.ResponseMapper
import com.movieappjc.domain.entities.VideoEntity
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = false)
@Stable
data class VideoResultModel(
    @field:Json(name = "results")
    val videos: List<VideoModel>
): ResponseMapper<List<VideoEntity>> {
    override fun mapTo(): List<VideoEntity> {
        return videos.value().map {
            it.mapTo()
        }
    }

}