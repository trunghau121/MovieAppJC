package com.movieappjc.presentation.screen.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imeNestedScroll
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.core_app.extension.aDp
import com.movieappjc.app.common.constants.noMoviesSearchedText
import com.movieappjc.app.common.localization.LocalLanguages
import com.movieappjc.app.components.AppEmptyText
import com.movieappjc.app.components.CircularProgressBar
import com.movieappjc.app.components.ToUI
import com.movieappjc.presentation.viewmodel.search.SearchMovieViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SearchMovieScreen(viewModel: SearchMovieViewModel = hiltViewModel()) {
    val textSearch by viewModel.textSearch.collectAsStateWithLifecycle()
    val state by viewModel.movies.collectAsStateWithLifecycle()
    val sizeItem = 100.aDp
    Column {
        SearchAppBar(
            valueText = textSearch,
            onChangeText = viewModel::onChangText,
            onBack = viewModel::onBack
        )
        state.ToUI(
            content = { state ->
                val data = state.data
                if (data.isNotEmpty()) {
                    LazyColumn(modifier = Modifier.imePadding().imeNestedScroll()) {
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
            },
            onRetry = viewModel::reloadSearchMovie,
            loading = {
                Box(modifier = Modifier.height(200.aDp)) {
                    CircularProgressBar()
                }
            }

        )
    }
}