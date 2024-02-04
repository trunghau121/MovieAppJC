package com.movieappjc.presentation.screen.favorite

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bumptech.glide.integration.compose.rememberGlidePreloadingData
import com.core_app.extension.dpToPx
import com.core_app.network.DataState
import com.movieappjc.app.common.constants.noFavoriteMovieText
import com.movieappjc.app.common.localization.LocalLanguages
import com.movieappjc.app.common.screenutil.ScreenUtil
import com.movieappjc.app.components.AppEmptyText
import com.movieappjc.presentation.viewmodel.favorite.FavoriteViewModel

@Composable
fun FavoriteMovieScreen(viewModel: FavoriteViewModel = hiltViewModel()) {
    val state by viewModel.movies.collectAsStateWithLifecycle()
    val widthItem = (ScreenUtil.getScreenWidth() / 2) - 16.dp
    val heightItem = widthItem.times(1.5f)
    LaunchedEffect(true) {
        viewModel.getFavoriteMovies()
    }
    Column {
        FavoriteAppBar(onBack = viewModel::onBack)
        if (state is DataState.Success) {
            val movies = (state as DataState.Success).data
            if (movies.isNotEmpty()) {
                val glidePreload = rememberGlidePreloadingData(
                    data = movies, preloadImageSize = Size(widthItem.dpToPx(), heightItem.dpToPx())
                ) { item, requestBuilder ->
                    requestBuilder.load(item.getPosterUrl())
                }
                LazyVerticalGrid(
                    modifier = Modifier.fillMaxSize(),
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(5.dp)
                ) {
                    items(glidePreload.size, key = { movies[it].id }) {
                        val (item, preloadRequest) = glidePreload[it]
                        FavoriteMovieItem(
                            modifier = Modifier.width(widthItem).height(heightItem),
                            movieEntity = item,
                            preloadRequest = { preloadRequest },
                            onNavigateToMovieDetail = viewModel::onNavigateToMovieDetail,
                            onDelete = viewModel::deleteFavoriteMovie
                        )
                    }
                }
            } else {
                AppEmptyText(LocalLanguages.current.noFavoriteMovieText())
            }
        }
    }
}