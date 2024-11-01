package com.movieappjc.presentation.screen.home.movie_carousel

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
import com.core_app.extension.aDp
import com.core_app.extension.aSp
import com.core_app.utils.ImmutableHolder
import com.movieappjc.app.theme.fontCustomSemiBold
import com.movieappjc.domain.entities.MovieEntity

@Composable
fun CarouselMovie(movies: ImmutableHolder<List<MovieEntity>>, onNavigateToMovieDetail: (Int) -> Unit) {
    val pagerState = rememberPagerState {
        movies().size
    }
    Box(
        modifier = Modifier.fillMaxSize().padding(bottom = 10.aDp)) {
        BackdropMovie(movies()[pagerState.currentPage])
        Column(modifier = Modifier.align(Alignment.BottomStart)) {
            CarouselMovieList(
                movies = movies,
                pagerState = pagerState,
                onNavigateToMovieDetail = onNavigateToMovieDetail
            )
            Text(
                modifier = Modifier.fillMaxWidth().padding(PaddingValues(10.aDp)),
                text = movies()[pagerState.currentPage].title,
                color = Color.White,
                style = MaterialTheme.typography.fontCustomSemiBold,
                fontSize = 18.aSp,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}