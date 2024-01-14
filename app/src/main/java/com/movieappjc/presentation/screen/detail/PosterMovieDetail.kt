package com.movieappjc.presentation.screen.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.CrossFade
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.movieappjc.R
import com.movieappjc.common.GlideListener
import com.movieappjc.common.constants.Endpoints
import com.movieappjc.common.screenutil.ScreenUtil
import com.movieappjc.domain.entities.MovieDetailEntity
import com.movieappjc.theme.fontCustomSemiBold
import com.movieappjc.theme.kColorPrimarySecond
import com.movieappjc.theme.kColorViolet
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun PosterMovieDetail(
    movieDetailEntity: MovieDetailEntity,
    openTrailerMovie: () -> Unit
) {
    val heightScreen = ScreenUtil.getScreenHeight()
    val heightTotal = remember(heightScreen){ heightScreen.div(1.6f) }
    val heightPoster = remember(heightTotal){ heightTotal.div(2.6f) }
    val hazeState = remember { HazeState() }
    var showButtonReview by rememberSaveable { mutableStateOf(false) }
    Box(modifier = Modifier.fillMaxWidth().height(heightTotal)) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(heightTotal)
                .padding(bottom = heightPoster / 2),
            shape = RoundedCornerShape(
                bottomStartPercent = 5,
                bottomEndPercent = 5
            )
        ) {
            Box {
                GlideImage(
                    modifier = Modifier
                        .haze(
                            hazeState,
                            backgroundColor = kColorViolet,
                            tint = Color.White.copy(alpha = .2f),
                            blurRadius = 30.dp,
                        )
                        .fillMaxSize(),
                    model = "${Endpoints.urlOriginalImage}${movieDetailEntity.backdropPath}",
                    transition = CrossFade,
                    contentScale = ContentScale.FillHeight,
                    contentDescription = movieDetailEntity.title
                ) {
                    it.listener(GlideListener {
                        showButtonReview = true
                    })
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = kColorPrimarySecond.copy(alpha = 0.1f))
                )
                Image(
                    modifier = Modifier
                        .size(60.dp)
                        .align(Alignment.Center)
                        .clickable { openTrailerMovie() },
                    imageVector = ImageVector.vectorResource(id = R.drawable.icon_play),
                    contentDescription = ""
                )
                if (showButtonReview) {
                    ReviewButton(
                        modifier = Modifier
                            .padding(horizontal = 10.dp, vertical = 10.dp)
                            .wrapContentSize()
                            .align(Alignment.BottomEnd)
                            .hazeChild(hazeState, shape = CircleShape),
                        voteAverage = movieDetailEntity.voteAverage
                    )
                }
            }
        }

        Row(modifier = Modifier.padding(start = 15.dp).align(Alignment.BottomStart)) {
            Card(
                modifier = Modifier.width((heightPoster.div(1.5f))).height(heightPoster),
                shape = RoundedCornerShape(15.dp),
                border = BorderStroke(1.dp, color = kColorViolet)
            ) {
                GlideImage(
                    modifier = Modifier.fillMaxSize(),
                    model = "${Endpoints.baseUrlImage}${movieDetailEntity.posterPath}",
                    transition = CrossFade,
                    contentScale = ContentScale.Crop,
                    contentDescription = movieDetailEntity.title
                )
            }
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, top = (heightPoster / 2) + 10.dp, end = 10.dp),
                text = movieDetailEntity.title,
                color = Color.White,
                style = MaterialTheme.typography.fontCustomSemiBold,
                fontSize = 18.sp,
                textAlign = TextAlign.Start
            )
        }
    }
}