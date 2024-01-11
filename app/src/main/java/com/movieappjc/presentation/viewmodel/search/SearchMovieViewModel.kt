package com.movieappjc.presentation.viewmodel.search

import androidx.lifecycle.viewModelScope
import com.core_app.repository.Resource
import com.core_app.viewmodel.BaseViewModel
import com.core_app.navigation.AppNavigator
import com.movieappjc.domain.entities.MoviesResultEntity
import com.movieappjc.domain.usecases.SearchMovieUseCase
import com.movieappjc.presentation.route.DestinationApp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchMovieViewModel @Inject constructor(
    private val getSearchMovie: SearchMovieUseCase,
    private val appNavigator: AppNavigator
): BaseViewModel() {
    private val _movies = MutableStateFlow<Resource<MoviesResultEntity>>(SearchMovieInitState())
    val movies = _movies.asStateFlow()
    private var job: Job?= null
    private var keySearch: String= ""

    fun reloadSearchMovie(){
        searchMovie(keySearch)
    }

    fun searchMovie(keySearch: String){
        job?.cancel()
        if (this.keySearch == keySearch) return
        this.keySearch = keySearch
        if (keySearch.isNotEmpty()) {
            viewModelScope.launch {
                _movies.emit(Resource.Loading())
                job = executeTask(request = { getSearchMovie(keySearch) }, _movies)
            }
        }else {
            viewModelScope.launch {
                _movies.emit(SearchMovieInitState())
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