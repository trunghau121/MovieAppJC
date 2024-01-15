package com.movieappjc.domain.entities

import android.os.Parcelable

interface GenreEntity : Parcelable {
    val id: Int
    val name: String
}