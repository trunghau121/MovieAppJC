package com.movieappjc.presentation.screen.home.movie_carousel

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
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.commit451.coiltransformations.BlurTransformation
import com.movieappjc.domain.entities.MovieEntity


@Composable
fun BackdropMovie(movieEntity: MovieEntity) {

    val data = movieEntity.getBackdropUrl()

    Column(modifier = Modifier.fillMaxSize()) {
        AsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.8f)
                .clip(shape = RoundedCornerShape(bottomStartPercent = 10, bottomEndPercent = 10)),
            model = ImageRequest.Builder(LocalContext.current)
                .data(data)
                .transformations(BlurTransformation(LocalContext.current, 20f, 1f))
                .crossfade(true)
                .build(),
            contentScale = ContentScale.Crop,
            contentDescription = movieEntity.title
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.2f)
        )
    }
}