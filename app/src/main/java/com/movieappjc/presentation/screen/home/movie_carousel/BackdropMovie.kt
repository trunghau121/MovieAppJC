package com.movieappjc.presentation.screen.home.movie_carousel

import android.os.Build
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.core_app.utils.blur_glide.BlurGlideModel
import com.movieappjc.domain.entities.MovieEntity

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun BackdropMovie(movieEntity: MovieEntity) {

    val data: Any = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
        BlurGlideModel(
            context = LocalContext.current,
            url = movieEntity.getBackdropUrl(),
            radius = 20,
            sampling = 10
        )
    } else {
        movieEntity.getBackdropUrl()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        GlideImage(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.8f)
                .clip(shape = RoundedCornerShape(bottomStartPercent = 10, bottomEndPercent = 10))
                .then(
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        Modifier.blur(55.dp)
                    } else {
                        Modifier
                    }
                ),
            model = data,
            contentScale = ContentScale.FillHeight,
            contentDescription = movieEntity.title
        )
        Box(modifier = Modifier
            .fillMaxWidth()
            .weight(0.2f))
    }
}