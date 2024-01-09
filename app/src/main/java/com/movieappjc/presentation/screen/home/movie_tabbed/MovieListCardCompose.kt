package com.movieappjc.presentation.screen.home.movie_tabbed

import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.movieappjc.domain.entities.MovieEntity
import com.movieappjc.presentation.viewmodel.home.HomeViewModel

@Composable
fun MovieListCardCompose(
    homeViewModel: HomeViewModel,
    movies: List<MovieEntity>,
    modifier: Modifier
) {
    LazyRow(modifier = modifier) {
        items(movies , key = { it.id }) {
            MovieTabCardCompose(homeViewModel, it)
        }
    }
}