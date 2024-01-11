package com.movieappjc.domain.entities

import android.os.Parcelable

interface CastEntity : Parcelable {
    val id: Int
    val name: String
    val character: String
    val profilePath: String
}