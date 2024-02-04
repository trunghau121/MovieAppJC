package com.movieappjc.presentation.screen.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.CrossFade
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.movieappjc.app.common.screenutil.ScreenUtil
import com.movieappjc.app.theme.kColorViolet

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun PosterMovieDetail(
    modifier: Modifier = Modifier,
    posterUrl: String,
    contentDescription: String
) {
    val heightScreen = ScreenUtil.getScreenHeight()
    val heightPoster = heightScreen.div(4.16f)
    GlideImage(
        modifier = modifier
            .width((heightPoster.div(1.4f)))
            .height(heightPoster)
            .border(
                border = BorderStroke(1.dp, color = kColorViolet),
                shape = RoundedCornerShape(15.dp)
            )
            .clip(shape = RoundedCornerShape(15.dp))
            .background(color = Color.LightGray),
        model = posterUrl,
        transition = CrossFade,
        contentScale = ContentScale.Crop,
        contentDescription = contentDescription
    )
}