package com.movieappjc.presentation.screen.favorite

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
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
import com.core_app.extension.pxToDp
import com.core_app.repository.Resource
import com.movieappjc.common.constants.noFavoriteMovieText
import com.movieappjc.common.localization.LocalLanguages
import com.movieappjc.common.screenutil.ScreenUtil
import com.movieappjc.presentation.components.EmptyTextApp
import com.movieappjc.presentation.viewmodel.favorite.FavoriteViewModel

@Composable
fun FavoriteMovieScreen(viewModel: FavoriteViewModel = hiltViewModel()) {
    val state by viewModel.movies.collectAsStateWithLifecycle()
    val width = (ScreenUtil.getScreenWidth() / 2).pxToDp() - 16.dp
    LaunchedEffect(true) {
        viewModel.getFavoriteMovies()
    }
    Column {
        AppBarFavorite(onBack = viewModel::onBack)
        if (state is Resource.Success) {
            val movies = (state as Resource.Success).data
            if (movies.isNotEmpty()) {
                val glidePreload = rememberGlidePreloadingData(
                    data = movies, preloadImageSize = Size(width.value, width.times(1.5f).value)
                ) { item, requestBuilder ->
                    requestBuilder.load(item.getPosterUrl())
                }
                LazyVerticalGrid(
                    modifier = Modifier.fillMaxSize(),
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(5.dp)
                ) {
                    items(glidePreload.size, key = { movies[it].id }) {
                        FavoriteMovieItem(
                            movieEntity = glidePreload[it].first,
                            preloadRequest = glidePreload[it].second,
                            onNavigateToMovieDetail = viewModel::onNavigateToMovieDetail,
                            onDelete = viewModel::deleteFavoriteMovie
                        )
                    }
                }
            } else {
                EmptyTextApp(LocalLanguages.current.noFavoriteMovieText())
            }
        }
    }
}