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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.movieappjc.app.common.constants.noFavoriteMovieText
import com.movieappjc.app.common.localization.LocalLanguages
import com.movieappjc.app.common.screenutil.ScreenUtil
import com.movieappjc.app.components.AppEmptyText
import com.movieappjc.app.components.ToUI
import com.movieappjc.presentation.viewmodel.favorite.FavoriteViewModel

@Composable
fun FavoriteMovieScreen(viewModel: FavoriteViewModel = hiltViewModel()) {
    val state by viewModel.movies.collectAsStateWithLifecycle()
    val widthScreen = ScreenUtil.getScreenWidth()
    val columns =  when {
        widthScreen > 700.dp -> 5
        widthScreen > 600.dp -> 4
        else -> 2
    }
    val widthItem = (widthScreen / columns) - 16.dp
    val heightItem = widthItem.times(1.5f)
    LaunchedEffect(true) {
        viewModel.getFavoriteMovies()
    }
    Column {
        FavoriteAppBar(onBack = viewModel::onBack)
        state.ToUI({ data ->
            if (data.isNotEmpty()) {
                LazyVerticalGrid(
                    modifier = Modifier.fillMaxSize(),
                    columns = GridCells.Fixed(columns),
                    contentPadding = PaddingValues(5.dp)
                ) {
                    items(data.size, key = { data[it].id }) {
                        val item = data[it]
                        FavoriteMovieItem(
                            modifier = Modifier
                                .width(widthItem)
                                .height(heightItem),
                            movieEntity = item,
                            onNavigateToMovieDetail = viewModel::onNavigateToMovieDetail,
                            onDelete = viewModel::deleteFavoriteMovie
                        )
                    }
                }
            } else {
                AppEmptyText(LocalLanguages.current.noFavoriteMovieText())
            }
        })
    }
}