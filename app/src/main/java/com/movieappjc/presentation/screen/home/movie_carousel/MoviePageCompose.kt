package com.movieappjc.presentation.screen.home.movie_carousel

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.CrossFade
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.core_app.extension.pxToDp
import com.core_app.utils.ImmutableHolder
import com.movieappjc.common.screenutil.ScreenUtil
import com.movieappjc.domain.entities.MovieEntity
import kotlin.math.absoluteValue

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MoviePageCompose(
    movies: ImmutableHolder<List<MovieEntity>>,
    pagerState: PagerState,
    onNavigateToMovieDetail: (Int) -> Unit
) {
    val currentIndex = pagerState.currentPage
    val currentPageOffset = pagerState.currentPageOffsetFraction
    val maxOffset = 30.dp
    val heightScreen = ScreenUtil.getScreenHeight().pxToDp()
    val heightItem = remember(heightScreen) {
        heightScreen.div(3)
    }
    HorizontalPager(
        state = pagerState,
        pageSpacing = (ScreenUtil.getScreenWidth() / 7).pxToDp(),
        contentPadding = PaddingValues(horizontal = (ScreenUtil.getScreenWidth() / 4).pxToDp()),
    ) { page ->
        val offset = maxOffset * when (page) {
            currentIndex -> {
                currentPageOffset.absoluteValue
            }

            currentIndex - 1 -> {
                1 + currentPageOffset.coerceAtMost(0f)
            }

            currentIndex + 1 -> {
                1 - currentPageOffset.coerceAtLeast(0f)
            }

            else -> {
                1f
            }
        }
        PosterCard(
            movie = movies()[page],
            heightItem = heightItem,
            onNavigateToMovieDetail = onNavigateToMovieDetail,
            offset = { offset.toPx().toInt() })
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun PosterCard(
    movie: MovieEntity,
    heightItem: Dp,
    onNavigateToMovieDetail: (Int) -> Unit,
    offset: Density.() -> Int
) {
    GlideImage(
        modifier = Modifier
            .fillMaxWidth()
            .height(heightItem)
            .offset { IntOffset(0, -offset()) }
            .clip(RoundedCornerShape(15.dp))
            .background(color = Color.LightGray)
            .clickable { onNavigateToMovieDetail(movie.id) },
        model = movie.getPosterUrl(),
        transition = CrossFade,
        contentScale = ContentScale.Crop,
        contentDescription = movie.title
    )
}