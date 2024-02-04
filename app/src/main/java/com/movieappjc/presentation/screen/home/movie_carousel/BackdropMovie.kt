package com.movieappjc.presentation.screen.home.movie_carousel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.movieappjc.app.common.constants.Endpoints
import com.movieappjc.domain.entities.MovieEntity
import com.movieappjc.app.theme.kColorPrimarySecond

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun BackdropMovie(movieEntity: MovieEntity) {
    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.8f)
                .clip(
                    shape = RoundedCornerShape(
                        bottomStartPercent = 10,
                        bottomEndPercent = 10,
                    )
                )
                .blur(40.dp)
        ) {
            GlideImage(
                modifier = Modifier.fillMaxSize(),
                model = "${Endpoints.baseUrlImage}${movieEntity.backdropPath}",
                contentScale = ContentScale.FillHeight,
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