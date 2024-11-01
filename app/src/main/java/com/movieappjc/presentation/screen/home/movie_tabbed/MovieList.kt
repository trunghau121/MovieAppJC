package com.movieappjc.presentation.screen.home.movie_tabbed

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.core_app.extension.aDp
import com.core_app.utils.ImmutableHolder
import com.movieappjc.app.common.screenutil.ScreenUtil
import com.movieappjc.domain.entities.MovieEntity

@Composable
fun MovieList(
    movies: ImmutableHolder<List<MovieEntity>>,
    modifier: Modifier,
    onNavigateToMovieDetail: (Int) -> Unit,
) {
    val widthScreen = ScreenUtil.getScreenWidth()
    val withItem = widthScreen.div(
        when {
            widthScreen > 700.aDp -> 4.3f
            widthScreen > 600.aDp -> 3.3f
            else -> 2.3f
        }
    )

    LazyRow(modifier = modifier) {
        items(movies().size, key = { movies()[it].id }) {
            val item = movies()[it]
            MovieItem(
                modifier = Modifier.width(withItem).fillMaxHeight(),
                movieEntity = item,
                onNavigateToMovieDetail = onNavigateToMovieDetail
            )
        }
    }
}