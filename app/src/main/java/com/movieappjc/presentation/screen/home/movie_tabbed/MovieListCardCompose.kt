package com.movieappjc.presentation.screen.home.movie_tabbed

import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.rememberGlidePreloadingData
import com.core_app.utils.ImmutableHolder
import com.movieappjc.common.screenutil.ScreenUtil
import com.movieappjc.domain.entities.MovieEntity

@Composable
fun MovieListCardCompose(
    onNavigateToMovieDetail: (Int) -> Unit,
    movies: ImmutableHolder<List<MovieEntity>>,
    modifier: Modifier
) {
    val with = (ScreenUtil.getScreenWidth() / 8).dp
    val glidePreload = rememberGlidePreloadingData(
        data = movies(), preloadImageSize = Size(with.value, with.value * 1.5f)
    ) { item, requestBuilder ->
        requestBuilder.load(item.getPosterUrl())
    }
    LazyRow(modifier = modifier) {
        items(glidePreload.size, key = { movies()[it].id }) {
            MovieTabCardCompose(
                movieEntity = glidePreload[it].first,
                preloadRequest = glidePreload[it].second,
                onNavigateToMovieDetail = onNavigateToMovieDetail
            )
        }
    }
}