package com.movieappjc.data.models

import androidx.compose.runtime.Stable
import com.core_app.extension.value
import com.google.gson.annotations.SerializedName
import com.movieappjc.domain.entities.MoviesResultEntity

@Stable
class MoviesResultModel(
    @SerializedName("page")
    override val currentPage: Int,
    @SerializedName("results")
    override val data: List<MovieModel>,
    @SerializedName("total_pages")
    override val totalCountPages: Int,
): MoviesResultEntity