package com.movieappjc.presentation.screen.home.movie_tabbed

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.core_app.extension.aDp
import com.core_app.extension.aSp
import com.movieappjc.app.theme.fontCustomSemiBold
import com.movieappjc.domain.entities.MovieEntity

@Composable
fun MovieItem(
    modifier: Modifier = Modifier,
    movieEntity: MovieEntity,
    onNavigateToMovieDetail: (Int) -> Unit
) {
    Column(
        modifier = modifier.padding(start = 10.aDp, top = 10.aDp, end = 10.aDp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            modifier = Modifier
                .weight(1f)
                .clip(shape = RoundedCornerShape(15.aDp))
                .background(color = Color.LightGray)
                .clickable { onNavigateToMovieDetail(movieEntity.id) },
            model = ImageRequest.Builder(LocalContext.current)
                .data(movieEntity.getPosterUrl())
                .crossfade(true)
                .build(),
            contentScale = ContentScale.Crop,
            contentDescription = movieEntity.title
        )
        Text(
            modifier = Modifier.fillMaxWidth().padding(6.aDp),
            text = movieEntity.title,
            color = Color.White,
            style = MaterialTheme.typography.fontCustomSemiBold,
            fontSize = 15.aSp,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}