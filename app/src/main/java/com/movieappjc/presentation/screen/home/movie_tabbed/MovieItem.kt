package com.movieappjc.presentation.screen.home.movie_tabbed

import android.graphics.drawable.Drawable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.integration.compose.CrossFade
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.movieappjc.domain.entities.MovieEntity
import com.movieappjc.theme.fontCustomSemiBold

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun MovieItem(
    modifier: Modifier = Modifier,
    movieEntity: MovieEntity,
    preloadRequest: () -> RequestBuilder<Drawable>,
    onNavigateToMovieDetail: (Int) -> Unit
) {
    Column(
        modifier = modifier.padding(horizontal = 10.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GlideImage(
            modifier = Modifier
                .weight(1f)
                .clip(shape = RoundedCornerShape(15.dp))
                .background(color = Color.LightGray)
                .clickable { onNavigateToMovieDetail(movieEntity.id) },
            model = movieEntity.getPosterUrl(),
            contentScale = ContentScale.Crop,
            transition = CrossFade,
            contentDescription = movieEntity.title
        ) { primaryRequest ->
            primaryRequest.thumbnail(preloadRequest())
        }
        Text(
            modifier = Modifier.fillMaxWidth().padding(10.dp),
            text = movieEntity.title,
            color = Color.White,
            style = MaterialTheme.typography.fontCustomSemiBold,
            fontSize = 15.sp,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}