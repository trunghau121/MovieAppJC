package com.movieappjc.presentation.screen.detail

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.core_app.repository.Resource
import com.movieappjc.presentation.components.CircularProgressBar
import com.movieappjc.presentation.components.ErrorApp
import com.movieappjc.presentation.viewmodel.detail.MovieDetailViewModel

@Composable
fun MovieDetailScreen(
    viewModel: MovieDetailViewModel = hiltViewModel()
) {
    val isMovieFavorite by viewModel.isMovieFavorite.collectAsStateWithLifecycle()
    val castState by viewModel.castMovie.collectAsStateWithLifecycle()
    val stateMovieDetail = viewModel.movieDetail.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    when (val state = stateMovieDetail.value) {
        is Resource.Success -> {
            val data = state.data
            MainMovieDetail(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState),
                data = data,
                castState = { castState },
                isMovieFavorite = isMovieFavorite,
                openTrailerMovie = viewModel::openTrailerMovie,
                saveFavoriteMovie = viewModel::saveFavoriteMovie,
                onBack = viewModel::onBack
            )
        }

        is Resource.Error -> {
            ErrorApp(error = state.error, onRetry = viewModel::getMovieDetail)
        }

        else -> {
            CircularProgressBar()
        }
    }
}