package com.movieappjc.presentation.screen.home.movie_carousel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.core_app.utils.blur_glide.BlurGlideModel
import com.movieappjc.app.common.constants.Endpoints
import com.movieappjc.app.theme.kColorPrimarySecond
import com.movieappjc.domain.entities.MovieEntity

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun BackdropMovie(movieEntity: MovieEntity) {

    val data = BlurGlideModel(
        context = LocalContext.current,
        url = "${Endpoints.baseUrlImage200}${movieEntity.backdropPath}",
        radius = 20,
        sampling = 10
    )
    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.8f)
                .clip(shape = RoundedCornerShape(bottomStartPercent = 10, bottomEndPercent = 10))
        ) {
            GlideImage(
                modifier = Modifier.fillMaxSize(),
                model = data,
                contentScale = ContentScale.Crop,
                contentDescription = movieEntity.title
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = kColorPrimarySecond.copy(alpha = 0.2f))
            )
        }
        Box(modifier = Modifier.fillMaxWidth().weight(0.2f))
    }
}