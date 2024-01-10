package com.movieappjc.presentation.screen.detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.core_app.repository.Resource
import com.movieappjc.presentation.components.ErrorAppComponent
import com.movieappjc.presentation.components.LoadingCircle
import com.movieappjc.presentation.screen.detail.cast_crew.CastCrewComponent
import com.movieappjc.presentation.viewmodel.detail.MovieDetailViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun MovieDetailScreen(
    viewModel: MovieDetailViewModel = koinViewModel()
) {
    val scrollState = rememberScrollState()
    val castState by viewModel.castMovie.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        when (val state = viewModel.movieDetail.collectAsStateWithLifecycle().value) {
            is Resource.Success -> {
                Column(modifier = Modifier.fillMaxSize().verticalScroll(scrollState)) {
                    MovieDetailPoster(viewModel, state.data)
                    ContentMovieDetail(state.data)
                    if (castState is Resource.Success) {
                        val cast = (castState as Resource.Success).data
                        if (cast.isNotEmpty()) {
                            CastCrewComponent(cast)
                        }
                    }
                }
                MovieDetailHeader(viewModel)
            }
            is Resource.Error -> {
                ErrorAppComponent(error = state.error) {
                    viewModel.getMovieDetail()
                }
            }
            else -> {
                LoadingCircle()
            }
        }
    }
}