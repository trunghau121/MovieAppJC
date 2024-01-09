package com.movieappjc.presentation.screen.home.movie_carousel

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.CrossFade
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.movieappjc.common.constants.Endpoints
import com.movieappjc.common.screenutil.ScreenUtil
import com.movieappjc.domain.entities.MovieEntity
import com.movieappjc.presentation.viewmodel.home.HomeViewModel
import kotlin.math.absoluteValue

@OptIn(ExperimentalFoundationApi::class, ExperimentalGlideComposeApi::class)
@Composable
fun MoviePageCompose(
    homeViewModel: HomeViewModel,
    movies: List<MovieEntity>,
    pagerState: PagerState
) {
    val currentIndex = pagerState.currentPage
    val currentPageOffset = pagerState.currentPageOffsetFraction
    val maxOffset = 20.dp
    HorizontalPager(
        state = pagerState,
        pageSpacing = (ScreenUtil.getScreenWidth() / 26).dp,
        contentPadding = PaddingValues(horizontal = (ScreenUtil.getScreenWidth() / 14).dp),
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
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(((ScreenUtil.getScreenWidth() / 7) * 1.3).dp)
                .offset(y = -offset),
            shape = RoundedCornerShape(15.dp)
        ) {
            GlideImage(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable {
                        homeViewModel.onNavigateToMovieDetail(movies[page].id)
                    },
                model = "${Endpoints.baseUrlImage}${movies[page].posterPath}",
                transition = CrossFade,
                contentScale = ContentScale.Crop,
                contentDescription = movies[page].title
            )
        }
    }
}