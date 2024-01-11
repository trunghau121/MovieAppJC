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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.core_app.repository.Resource
import com.movieappjc.common.constants.noFavoriteMovieText
import com.movieappjc.common.localization.LocalLanguages
import com.movieappjc.presentation.components.EmptyTextApp
import com.movieappjc.presentation.viewmodel.favorite.FavoriteViewModel

@Composable
fun FavoriteMovieScreen(viewModel: FavoriteViewModel = hiltViewModel()) {
    val state by viewModel.movies.collectAsStateWithLifecycle()
    LaunchedEffect(true) {
        viewModel.getFavoriteMovies()
    }
    Column {
        AppBarFavorite(viewModel)
        if (state is Resource.Success) {
            val movies = state as Resource.Success
            if (movies.data.isNotEmpty()) {
                LazyVerticalGrid(
                    modifier = Modifier.fillMaxSize(),
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(5.dp)
                ) {
                    items(movies.data.size) {
                        val item = movies.data[it]
                        FavoriteMovieItem(viewModel, item) {
                            viewModel.deleteFavoriteMovie(item.id)
                        }
                    }
                }
            }else {
                EmptyTextApp(LocalLanguages.current.noFavoriteMovieText())
            }
        }
    }
}