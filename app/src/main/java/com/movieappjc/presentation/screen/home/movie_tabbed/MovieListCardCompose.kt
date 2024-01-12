package com.movieappjc.presentation.screen.home.movie_tabbed

import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.core_app.utils.ImmutableHolder
import com.movieappjc.domain.entities.MovieEntity

@Composable
fun MovieListCardCompose(
    onNavigateToMovieDetail: (Int) -> Unit,
    movies: ImmutableHolder<List<MovieEntity>>,
    modifier: Modifier
) {
    LazyRow(modifier = modifier) {
        items(movies(), key = { it.id }) {
            MovieTabCardCompose(onNavigateToMovieDetail, it)
        }
    }
}