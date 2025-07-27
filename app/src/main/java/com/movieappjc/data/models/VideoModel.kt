package com.movieappjc.data.models

import com.core_app.extension.value
import com.core_app.network.ResponseMapper
import com.squareup.moshi.Json
import com.movieappjc.domain.entities.VideoEntity
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = false)
data class VideoModel(
    @param:Json(name = "id")
    val id: String?,
    @param:Json(name = "iso_3166_1")
    val iso31661: String?,
    @param:Json(name = "iso_639_1")
    val iso6391: String?,
    @param:Json(name = "key")
    private val _key: String?,
    @param:Json(name = "name")
    private val _name: String?,
    @param:Json(name = "official")
    val official: Boolean?,
    @param:Json(name = "published_at")
    val publishedAt: String?,
    @param:Json(name = "site")
    val site: String?,
    @param:Json(name = "size")
    val size: Int?,
    @param:Json(name = "type")
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