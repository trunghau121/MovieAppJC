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
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.core_app.extension.value
import com.movieappjc.app.common.constants.Endpoints
import com.movieappjc.app.common.screenutil.ScreenUtil

@Composable
fun BackdropMovieDetail(
    modifier: Modifier = Modifier,
    backdropPath: String?,
    contentDescription: String
) {
    val heightScreen = ScreenUtil.getScreenHeight()
    val heightTotal = heightScreen.div(2.3f)

    AsyncImage(
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
        model = ImageRequest.Builder(LocalContext.current)
            .data("${Endpoints.urlOriginalImage}${backdropPath.value()}")
            .crossfade(true)
            .build(),
        contentScale = ContentScale.Crop,
        contentDescription = contentDescription
    )
}