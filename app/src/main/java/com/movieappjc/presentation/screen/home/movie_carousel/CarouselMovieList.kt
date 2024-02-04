package com.movieappjc.presentation.screen.home.movie_carousel

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.CrossFade
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.core_app.extension.dpToPx
import com.core_app.utils.ImmutableHolder
import com.movieappjc.app.common.screenutil.ScreenUtil
import com.movieappjc.domain.entities.MovieEntity
import com.movieappjc.app.common.utils.pagerAnimation

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CarouselMovieList(
    movies: ImmutableHolder<List<MovieEntity>>,
    pagerState: PagerState,
    onNavigateToMovieDetail: (Int) -> Unit
) {
    val maxOffset = 30.dp.dpToPx()
    val heightScreen = ScreenUtil.getScreenHeight()
    val heightItem = heightScreen.div(3)
    HorizontalPager(
        state = pagerState,
        pageSpacing = ScreenUtil.getScreenWidth() / 7,
        contentPadding = PaddingValues(horizontal = ScreenUtil.getScreenWidth() / 4),
    ) { page ->
        PosterCard(
            modifier = Modifier.pagerAnimation(pagerState, maxOffset, page),
            movie = movies()[page],
            heightItem = heightItem,
            onNavigateToMovieDetail = onNavigateToMovieDetail
        )
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun PosterCard(
    modifier: Modifier = Modifier,
    movie: MovieEntity,
    heightItem: Dp,
    onNavigateToMovieDetail: (Int) -> Unit,
) {
    GlideImage(
        modifier = modifier
            .fillMaxWidth()
            .height(heightItem)
            .clip(RoundedCornerShape(15.dp))
            .background(color = Color.LightGray)
            .clickable { onNavigateToMovieDetail(movie.id) },
        model = movie.getPosterUrl(),
        transition = CrossFade,
        contentScale = ContentScale.Crop,
        contentDescription = movie.title
    )
}