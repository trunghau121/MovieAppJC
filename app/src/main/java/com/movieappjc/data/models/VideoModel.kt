package com.movieappjc.data.models

import com.core_app.extension.value
import com.core_app.network.ResponseMapper
import com.squareup.moshi.Json
import com.movieappjc.domain.entities.VideoEntity
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = false)
data class VideoModel(
    @Json(name = "id")
    val id: String?,
    @Json(name = "iso_3166_1")
    val iso31661: String?,
    @Json(name = "iso_639_1")
    val iso6391: String?,
    @Json(name = "key")
    private val _key: String?,
    @Json(name = "name")
    private val _name: String?,
    @Json(name = "official")
    val official: Boolean?,
    @Json(name = "published_at")
    val publishedAt: String?,
    @Json(name = "site")
    val site: String?,
    @Json(name = "size")
    val size: Int?,
    @Json(name = "type")
    private val _type: String?
) : ResponseMapper<VideoEntity> {
    override fun mapTo(): VideoEntity {
        return VideoEntity(
            key = _key.value(),
            name = _name.value(),
            type = _type.value()
        )
    }
}