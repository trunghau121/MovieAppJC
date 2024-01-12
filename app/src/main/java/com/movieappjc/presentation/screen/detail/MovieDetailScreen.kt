package com.movieappjc.presentation.screen.detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.core_app.repository.Resource
import com.core_app.utils.ImmutableHolder
import com.movieappjc.presentation.components.ErrorAppComponent
import com.movieappjc.presentation.components.LoadingCircle
import com.movieappjc.presentation.screen.detail.cast_crew.CastCrewComponent
import com.movieappjc.presentation.viewmodel.detail.MovieDetailViewModel

@Composable
fun MovieDetailScreen(
    viewModel: MovieDetailViewModel = hiltViewModel()
) {
    val scrollState = rememberScrollState()
    val isMovieFavorite by viewModel.isMovieFavorite.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        when (val state = viewModel.movieDetail.collectAsStateWithLifecycle().value) {
            is Resource.Success -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                ) {
                    val data = state.data
                    MovieDetailPoster(
                        movieDetailEntity = data,
                        openTrailerMovie = viewModel::openTrailerMovie
                    )
                    TimeMovieDetail(movieDetailEntity = data)
                    DescriptionMovieDetail(overview = data.overview)
                    val castState by viewModel.castMovie.collectAsStateWithLifecycle()
                    if (castState is Resource.Success) {
                        val cast = (castState as Resource.Success).data
                        if (cast.isNotEmpty()) {
                            CastCrewComponent(ImmutableHolder(cast))
                        }
                    }
                }
                MovieDetailHeader(
                    isMovieFavorite = isMovieFavorite,
                    onSaveMovie = viewModel::saveFavoriteMovie,
                    onBack = viewModel::onBack
                )
            }

            is Resource.Error -> {
                ErrorAppComponent(error = state.error, onRetry = viewModel::getMovieDetail)
            }

            else -> {
                LoadingCircle()
            }
        }
    }
}