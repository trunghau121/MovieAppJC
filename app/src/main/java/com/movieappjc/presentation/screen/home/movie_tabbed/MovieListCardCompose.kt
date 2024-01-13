package com.movieappjc.presentation.screen.home.movie_tabbed

import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
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
    val widthScreen = ScreenUtil.getScreenWidth()
    val withItem = remember (widthScreen){
        widthScreen.div(2.2f)
    }
    val glidePreload = rememberGlidePreloadingData(
        data = movies(), preloadImageSize = Size(withItem, withItem * 1.5f)
    ) { item, requestBuilder ->
        requestBuilder.load(item.getPosterUrl())
    }
    LazyRow(modifier = modifier) {
        items(glidePreload.size, key = { movies()[it].id }) {
            val (item, preloadRequest) = glidePreload[it]
            MovieTabCardCompose(
                movieEntity = item,
                preloadRequest = { preloadRequest },
                onNavigateToMovieDetail = onNavigateToMovieDetail
            )
        }
    }
}