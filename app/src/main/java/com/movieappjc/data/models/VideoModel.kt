package com.movieappjc.data.models

import com.google.gson.annotations.SerializedName
import com.movieappjc.domain.entities.VideoEntity
import kotlinx.parcelize.Parcelize

@Parcelize
data class VideoModel(
    @SerializedName("id")
    val id: String?,
    @SerializedName("iso_3166_1")
    val iso31661: String?,
    @SerializedName("iso_639_1")
    val iso6391: String?,
    @SerializedName("key")
    override val key: String,
    @SerializedName("name")
    override val name: String,
    @SerializedName("official")
    val official: Boolean?,
    @SerializedName("published_at")
    val publishedAt: String?,
    @SerializedName("site")
    val site: String?,
    @SerializedName("size")
    val size: Int?,
    @SerializedName("type")
    override val type: String
) : VideoEntity