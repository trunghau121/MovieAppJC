package com.movieappjc.presentation.screen.home.movie_tabbed

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.CrossFade
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.movieappjc.common.constants.Endpoints
import com.movieappjc.common.screenutil.ScreenUtil
import com.movieappjc.domain.entities.MovieEntity
import com.movieappjc.theme.fontCustomSemiBold

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun MovieTabCardCompose(onNavigateToMovieDetail: (Int) -> Unit, movieEntity: MovieEntity) {
    Column(
        modifier = Modifier
            .width((ScreenUtil.getScreenWidth() / 8).dp)
            .fillMaxHeight()
            .padding(horizontal = 10.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(15.dp)
        ) {
            GlideImage(
                modifier = Modifier.fillMaxSize().clickable {
                    onNavigateToMovieDetail(movieEntity.id)
                },
                model = "${Endpoints.baseUrlImage}${movieEntity.posterPath}",
                contentScale = ContentScale.Crop,
                transition = CrossFade,
                contentDescription = movieEntity.title
            )
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