package com.core_app.utils.blur_glide

import android.content.Context

data class BlurGlideModel(
    val context: Context,
    val url: String,
    val radius: Int = 30,
    val sampling: Int = 3
)