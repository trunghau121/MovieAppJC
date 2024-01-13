package com.movieappjc.domain.entities

import com.movieappjc.common.constants.Endpoints

interface CastEntity {
    val id: Int
    val name: String
    val character: String
    val profilePath: String

    fun getProfileUrl(): String = "${Endpoints.baseUrlImage200}${profilePath}"
}