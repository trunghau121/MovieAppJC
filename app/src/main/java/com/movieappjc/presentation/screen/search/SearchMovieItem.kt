package com.movieappjc.presentation.screen.search

import android.graphics.drawable.Drawable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.integration.compose.CrossFade
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.movieappjc.domain.entities.MovieEntity
import com.movieappjc.theme.fontCustomMedium
import com.movieappjc.theme.fontCustomNormal

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun SearchMovieItem(
    movieEntity: MovieEntity,
    preloadRequest: () -> RequestBuilder<Drawable>,
    onNavigateToMovieDetail: (Int) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp, vertical = 8.dp)
            .clickable {
                keyboardController?.hide()
                onNavigateToMovieDetail(movieEntity.id)
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        GlideImage(
            modifier = Modifier
                .size(100.dp)
                .clip(shape = RoundedCornerShape(16.dp))
                .background(color = Color.LightGray),
            model = movieEntity.getPosterUrl(),
            contentScale = ContentScale.Crop,
            transition = CrossFade,
            contentDescription = movieEntity.title
        ) { primaryRequest ->
            primaryRequest.thumbnail(preloadRequest())
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = movieEntity.title,
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                style = MaterialTheme.typography.fontCustomMedium,
                fontSize = 17.sp,
                textAlign = TextAlign.Start,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = movieEntity.overview,
                modifier = Modifier.fillMaxWidth(),
                color = Color.Gray,
                style = MaterialTheme.typography.fontCustomNormal,
                fontSize = 15.sp,
                textAlign = TextAlign.Start,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}