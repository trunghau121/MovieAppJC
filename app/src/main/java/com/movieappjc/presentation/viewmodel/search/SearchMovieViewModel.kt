package com.movieappjc.presentation.viewmodel.search

import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import com.core_app.navigation.AppNavigator
import com.core_app.repository.Resource
import com.core_app.viewmodel.BaseViewModel
import com.movieappjc.domain.entities.MoviesResultEntity
import com.movieappjc.domain.usecases.SearchMovieUseCase
import com.movieappjc.presentation.route.DestinationApp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@Stable
@HiltViewModel
class SearchMovieViewModel @Inject constructor(
    private val getSearchMovie: SearchMovieUseCase,
    private val appNavigator: AppNavigator
): BaseViewModel() {
    private val _movies = MutableStateFlow<Resource<MoviesResultEntity>>(Resource.InitState())
    val movies = _movies.asStateFlow()
    private var job: Job?= null
    private val _textSearch = MutableStateFlow("")
    val textSearch: StateFlow<String> = _textSearch.asStateFlow()

    init {
        viewModelScope.launch {
            textSearch.debounce(300).collect { query ->
                searchMovie(query)
            }
        }
    }

    fun onChangText(text: String) {
        _textSearch.value = text
    }

    fun reloadSearchMovie(){
        searchMovie(textSearch.value)
    }

    private fun searchMovie(keySearch: String){
        job?.cancel()
        if (keySearch.isNotEmpty()) {
            viewModelScope.launch {
                _movies.emit(Resource.Loading())
                job = executeTask(request = { getSearchMovie(keySearch) }, _movies)
            }
        }else {
            viewModelScope.launch {
                _movies.emit(Resource.InitState())
            }
        }
    }

    fun onNavigateToMovieDetail(movieId: Int) {
        viewModelScope.launch {
            appNavigator.navigateTo(DestinationApp.MovieDetail(movieId))
        }
    }

    fun onBack(){
        viewModelScope.launch {
            appNavigator.navigateBack()
        }
    }

    override fun onCleared() {
        job?.cancel()
        super.onCleared()
    }
}