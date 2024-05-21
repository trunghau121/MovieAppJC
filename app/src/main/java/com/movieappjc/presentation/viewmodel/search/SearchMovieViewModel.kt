package com.movieappjc.presentation.viewmodel.search

import androidx.compose.runtime.Stable
import com.core_app.base.viewmodel.BaseViewModel
import com.core_app.navigation.AppNavigator
import com.core_app.network.DataState
import com.movieappjc.app.route.Routes
import com.movieappjc.domain.entities.MoviesResultEntity
import com.movieappjc.domain.usecases.SearchMovieUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import javax.inject.Inject

@OptIn(FlowPreview::class)
@Stable
@HiltViewModel
class SearchMovieViewModel @Inject constructor(
    private val getSearchMovie: SearchMovieUseCase,
    appNavigator: AppNavigator
) : BaseViewModel(appNavigator) {
    private val _movies = MutableStateFlow<DataState<MoviesResultEntity>>(DataState.InitState())
    val movies = _movies.asStateFlow()
    private var job: Job? = null
    private val _textSearch = MutableStateFlow("")
    val textSearch: StateFlow<String> = _textSearch.asStateFlow()

    init {
        safeLaunch {
            textSearch.debounce(300).collect { query ->
                searchMovie(query)
            }
        }
    }

    fun onChangText(text: String) {
        _textSearch.value = text
    }

    fun reloadSearchMovie() {
        searchMovie(textSearch.value)
    }

    private fun searchMovie(keySearch: String) {
        job?.cancel()
        if (keySearch.isNotEmpty()) {
            safeLaunch {
                _movies.emit(DataState.Loading())
                job = executeTask({ getSearchMovie(keySearch) }, _movies)
            }
        } else {
            safeLaunch {
                _movies.emit(DataState.InitState())
            }
        }
    }

    fun onNavigateToMovieDetail(movieId: Int) {
        navigateTo(Routes.MovieDetail(movieId))
    }

    override fun onCleared() {
        job?.cancel()
        super.onCleared()
    }
}