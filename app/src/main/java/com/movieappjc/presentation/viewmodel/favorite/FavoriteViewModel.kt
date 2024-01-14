package com.movieappjc.presentation.viewmodel.favorite

import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import com.core_app.repository.Resource
import com.core_app.viewmodel.BaseViewModel
import com.core_app.navigation.AppNavigator
import com.movieappjc.domain.entities.MovieEntity
import com.movieappjc.domain.usecases.FavoriteMovieUseCase
import com.movieappjc.presentation.route.DestinationApp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@Stable
@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val favoriteMovie: FavoriteMovieUseCase,
    private val appNavigator: AppNavigator
) : BaseViewModel() {
    private val _movies = MutableStateFlow<Resource<List<MovieEntity>>>(Resource.Loading())
    val movies = _movies.asStateFlow()

    fun getFavoriteMovies() {
        executeTask(request = { favoriteMovie.getFavoriteMovies() }, _movies)
    }

    fun deleteFavoriteMovie(movieId: Int){
        viewModelScope.launch {
            favoriteMovie.deleteFavoriteMovie(movieId)
            getFavoriteMovies()
        }
    }

    fun onBack(){
        viewModelScope.launch {
            appNavigator.navigateBack()
        }
    }

    fun onNavigateToMovieDetail(movieId: Int) {
        viewModelScope.launch {
            appNavigator.navigateTo(DestinationApp.MovieDetail(movieId))
        }
    }
}