package com.movieappjc.presentation.screen.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.core_app.repository.Resource
import com.movieappjc.common.constants.noMoviesSearchedText
import com.movieappjc.common.localization.LocalLanguages
import com.movieappjc.presentation.components.EmptyTextApp
import com.movieappjc.presentation.components.ErrorAppComponent
import com.movieappjc.presentation.components.LoadingCircle
import com.movieappjc.presentation.viewmodel.search.SearchMovieViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun SearchMovieScreen(viewModel: SearchMovieViewModel = koinViewModel()) {
    Column {
        AppBarSearch(viewModel)
        when (val state = viewModel.movies.collectAsStateWithLifecycle().value) {
            is Resource.Success -> {
                if (state.data.data.isNotEmpty()) {
                    LazyColumn(modifier = Modifier.imePadding()) {
                        items(state.data.data) {
                            SearchMovieItem(viewModel, it)
                        }
                    }
                }else {
                    EmptyTextApp(LocalLanguages.current.noMoviesSearchedText())
                }
            }
            is Resource.Error -> {
                ErrorAppComponent(error = state.error) {
                    viewModel.reloadSearchMovie()
                }
            }
            is Resource.Loading -> {
                LoadingCircle()
            }
            else -> {}
        }
    }
}