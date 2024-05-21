package com.movieappjc.presentation.screen.favorite

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.movieappjc.app.theme.kColorViolet
import com.movieappjc.domain.entities.MovieEntity

@Composable
fun FavoriteMovieItem(
    modifier: Modifier = Modifier,
    movieEntity: MovieEntity,
    onNavigateToMovieDetail: (Int) -> Unit,
    onDelete: (Int) -> Unit
) {
    Card(
        modifier = modifier.padding(5.dp).clickable { onNavigateToMovieDetail(movieEntity.id) },
        shape = RoundedCornerShape(15.dp),
        border = BorderStroke(2.dp, color = kColorViolet)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                modifier = Modifier.fillMaxSize(),
                model = ImageRequest.Builder(LocalContext.current)
                    .data(movieEntity.getPosterUrl())
                    .crossfade(true)
                    .build(),
                contentScale = ContentScale.Crop,
                contentDescription = movieEntity.title
            )
            Icon(
                modifier = Modifier
                    .size(45.dp)
                    .align(Alignment.TopEnd)
                    .padding(10.dp)
                    .clickable { onDelete(movieEntity.id) },
                imageVector = Icons.Filled.Delete,
                contentDescription = "",
                tint = Color.White
            )
        }
    }
}