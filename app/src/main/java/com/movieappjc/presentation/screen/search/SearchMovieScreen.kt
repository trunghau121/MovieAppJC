package com.movieappjc.presentation.screen.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.core_app.network.DataState
import com.movieappjc.app.common.constants.noMoviesSearchedText
import com.movieappjc.app.common.localization.LocalLanguages
import com.movieappjc.app.components.AppEmptyText
import com.movieappjc.app.components.CircularProgressBar
import com.movieappjc.app.components.ErrorApp
import com.movieappjc.presentation.viewmodel.search.SearchMovieViewModel

@Composable
fun SearchMovieScreen(viewModel: SearchMovieViewModel = hiltViewModel()) {
    val textSearch by viewModel.textSearch.collectAsStateWithLifecycle()
    val sizeItem = 100.dp
    Column {
        SearchAppBar(
            valueText = textSearch,
            onChangeText = viewModel::onChangText,
            onBack = viewModel::onBack
        )
        when (val state = viewModel.movies.collectAsStateWithLifecycle().value) {
            is DataState.Success -> {
                val data = state.data.data
                if (data.isNotEmpty()) {
                    LazyColumn(modifier = Modifier.imePadding()) {
                        items(data.size, key = { data[it].id }) {
                            val item = data[it]
                            SearchMovieItem(
                                movieEntity = item,
                                sizeItem = sizeItem,
                                onNavigateToMovieDetail = viewModel::onNavigateToMovieDetail
                            )
                        }
                    }
                } else {
                    AppEmptyText(LocalLanguages.current.noMoviesSearchedText())
                }
            }

            is DataState.Error -> {
                ErrorApp(error = state.error, onRetry = viewModel::reloadSearchMovie)
            }

            is DataState.Loading -> {
                Box(modifier = Modifier.height(200.dp)) {
                    CircularProgressBar()
                }
            }

            else -> {}
        }
    }
}