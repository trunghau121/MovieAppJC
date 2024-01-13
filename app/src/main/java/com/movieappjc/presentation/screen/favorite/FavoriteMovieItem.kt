package com.movieappjc.presentation.screen.favorite

import android.graphics.drawable.Drawable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.unit.dp
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.integration.compose.CrossFade
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.core_app.extension.pxToDp
import com.movieappjc.common.screenutil.ScreenUtil
import com.movieappjc.domain.entities.MovieEntity
import com.movieappjc.theme.kColorViolet

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun FavoriteMovieItem(
    movieEntity: MovieEntity,
    preloadRequest: () -> RequestBuilder<Drawable>,
    onNavigateToMovieDetail: (Int) -> Unit,
    onDelete: (Int) -> Unit
) {
    val width = (ScreenUtil.getScreenWidth() / 2).pxToDp() - 16.dp
    Card(
        modifier = Modifier
            .width(width)
            .height(width.times(1.5f))
            .padding(5.dp).clickable { onNavigateToMovieDetail(movieEntity.id) },
        shape = RoundedCornerShape(15.dp),
        border = BorderStroke(2.dp, color = kColorViolet)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            GlideImage(
                modifier = Modifier.fillMaxSize(),
                model = movieEntity.getPosterUrl(),
                transition = CrossFade,
                contentScale = ContentScale.Crop,
                contentDescription = movieEntity.title
            ) { primaryRequest ->
                primaryRequest.thumbnail(preloadRequest())
            }
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