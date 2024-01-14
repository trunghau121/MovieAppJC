package com.movieappjc.presentation.screen.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bumptech.glide.integration.compose.rememberGlidePreloadingData
import com.core_app.extension.dpToPx
import com.core_app.repository.Resource
import com.movieappjc.common.constants.noMoviesSearchedText
import com.movieappjc.common.localization.LocalLanguages
import com.movieappjc.presentation.components.AppEmptyText
import com.movieappjc.presentation.components.ErrorApp
import com.movieappjc.presentation.components.CircularProgressBar
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
            is Resource.Success -> {
                val data = state.data.data
                if (data.isNotEmpty()) {
                    val glidePreload = rememberGlidePreloadingData(
                        data = data, preloadImageSize = Size(sizeItem.dpToPx(), sizeItem.dpToPx())
                    ) { item, requestBuilder ->
                        requestBuilder.load(item.getPosterUrl())
                    }

                    LazyColumn(modifier = Modifier.imePadding()) {
                        items(glidePreload.size, key = { data[it].id }) {
                            val (item, preloadRequest) = glidePreload[it]
                            SearchMovieItem(
                                movieEntity = item,
                                sizeItem = sizeItem,
                                preloadRequest = { preloadRequest },
                                onNavigateToMovieDetail = viewModel::onNavigateToMovieDetail
                            )
                        }
                    }
                } else {
                    AppEmptyText(LocalLanguages.current.noMoviesSearchedText())
                }
            }

            is Resource.Error -> {
                ErrorApp(error = state.error, onRetry = viewModel::reloadSearchMovie)
            }

            is Resource.Loading -> {
                Box(modifier = Modifier.height(200.dp)) {
                    CircularProgressBar()
                }
            }

            else -> {}
        }
    }
}