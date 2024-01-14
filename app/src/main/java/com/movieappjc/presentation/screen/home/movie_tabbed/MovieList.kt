package com.movieappjc.presentation.screen.home.movie_tabbed

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import com.bumptech.glide.integration.compose.rememberGlidePreloadingData
import com.core_app.extension.dpToPx
import com.core_app.utils.ImmutableHolder
import com.movieappjc.common.screenutil.ScreenUtil
import com.movieappjc.domain.entities.MovieEntity

@Composable
fun MovieList(
    movies: ImmutableHolder<List<MovieEntity>>,
    modifier: Modifier,
    onNavigateToMovieDetail: (Int) -> Unit,
) {
    val widthScreen = ScreenUtil.getScreenWidth()
    val withItem =  widthScreen.div(2.3f)
    val heightItem = withItem.times(1.5f)

    val glidePreload = rememberGlidePreloadingData(
        data = movies(), preloadImageSize = Size(withItem.dpToPx(), heightItem.dpToPx())
    ) { item, requestBuilder ->
        requestBuilder.load(item.getPosterUrl())
    }

    LazyRow(modifier = modifier) {
        items(glidePreload.size, key = { movies()[it].id }) {
            val (item, preloadRequest) = glidePreload[it]
            MovieItem(
                modifier = Modifier.width(withItem).fillMaxHeight(),
                movieEntity = item,
                preloadRequest = { preloadRequest },
                onNavigateToMovieDetail = onNavigateToMovieDetail
            )
        }
    }
}