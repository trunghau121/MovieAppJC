package com.movieappjc.domain.entities

import android.os.Parcelable
import com.movieappjc.app.common.utils.KeyUtils

abstract class PersonEntity : Parcelable {
    abstract fun getId(): Int
    abstract fun getName(): String
    abstract fun getProfilePath(): String
    abstract fun getBirthday(): String
    abstract fun getPlaceOfBirth(): String
    abstract fun getBiography(): String
    abstract fun getKnownForDepartment(): String

    fun getProfileUrl(): String = "${KeyUtils.urlOriginalImage()}${getProfilePath()}"
}