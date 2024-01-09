package com.movieappjc.presentation.screen.home.movie_carousel

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.movieappjc.domain.entities.MovieEntity
import com.movieappjc.presentation.viewmodel.home.HomeViewModel
import com.movieappjc.theme.fontCustomSemiBold

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MovieCarouselCompose(homeViewModel: HomeViewModel, movies: List<MovieEntity>) {
    val pagerState = rememberPagerState {
        movies.size
    }
    Box(modifier = Modifier
        .fillMaxSize()
        .padding(bottom = 10.dp)) {
        MovieBackdropCompose(movies[pagerState.currentPage])
        Column(
            modifier = Modifier.align(Alignment.BottomStart)
        ) {
            MoviePageCompose(homeViewModel, movies, pagerState)
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(PaddingValues(10.dp)),
                text = movies[pagerState.currentPage].title,
                color = Color.White,
                style = MaterialTheme.typography.fontCustomSemiBold,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}