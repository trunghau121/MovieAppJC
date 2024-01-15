package com.movieappjc.presentation.screen.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import com.bumptech.glide.integration.compose.CrossFade
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.core_app.extension.value
import com.movieappjc.common.constants.Endpoints
import com.movieappjc.common.screenutil.ScreenUtil

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun BackdropMovieDetail(
    modifier: Modifier = Modifier,
    backdropPath: String?,
    contentDescription: String
) {
    val heightScreen = ScreenUtil.getScreenHeight()
    val heightTotal = heightScreen.div(2.3f)

    GlideImage(
        modifier = modifier
            .fillMaxSize()
            .height(heightTotal)
            .clip(
                shape = RoundedCornerShape(
                    bottomStartPercent = 5,
                    bottomEndPercent = 5
                )
            )
            .background(color = Color.LightGray),
        model = "${Endpoints.urlOriginalImage}${backdropPath.value()}",
        transition = CrossFade,
        contentScale = ContentScale.FillHeight,
        contentDescription = contentDescription
    )
}