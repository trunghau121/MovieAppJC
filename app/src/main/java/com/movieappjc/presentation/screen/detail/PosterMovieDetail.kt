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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.movieappjc.app.common.screenutil.ScreenUtil
import com.movieappjc.app.theme.kColorViolet

@Composable
fun PosterMovieDetail(
    modifier: Modifier = Modifier,
    posterUrl: String,
    contentDescription: String
) {
    val heightScreen = ScreenUtil.getScreenHeight()
    val heightPoster = heightScreen.div(4.16f)
    AsyncImage(
        modifier = modifier
            .width((heightPoster.div(1.4f)))
            .height(heightPoster)
            .border(
                border = BorderStroke(1.dp, color = kColorViolet),
                shape = RoundedCornerShape(15.dp)
            )
            .clip(shape = RoundedCornerShape(15.dp))
            .background(color = Color.LightGray),
        model = ImageRequest.Builder(LocalContext.current)
            .data(posterUrl)
            .crossfade(true)
            .build(),
        contentScale = ContentScale.Crop,
        contentDescription = contentDescription
    )
}