package com.movieappjc.domain.entities

import android.os.Parcelable

interface VideoEntity : Parcelable {
    val key: String
    val name: String
    val type: String
}