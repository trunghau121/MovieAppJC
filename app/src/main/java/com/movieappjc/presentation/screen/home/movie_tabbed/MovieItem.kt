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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.movieappjc.app.theme.fontCustomSemiBold
import com.movieappjc.domain.entities.MovieEntity

@Composable
fun MovieItem(
    modifier: Modifier = Modifier,
    movieEntity: MovieEntity,
    onNavigateToMovieDetail: (Int) -> Unit
) {
    Column(
        modifier = modifier.padding(start = 10.dp, top = 10.dp, end = 10.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            modifier = Modifier
                .weight(1f)
                .clip(shape = RoundedCornerShape(15.dp))
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
            modifier = Modifier.fillMaxWidth().padding(6.dp),
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