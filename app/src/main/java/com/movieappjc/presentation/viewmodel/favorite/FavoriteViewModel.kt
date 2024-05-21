package com.movieappjc.presentation.viewmodel.favorite

import androidx.compose.runtime.Stable
import com.core_app.base.viewmodel.BaseViewModel
import com.core_app.navigation.AppNavigator
import com.core_app.network.DataState
import com.movieappjc.app.route.Routes
import com.movieappjc.domain.entities.MovieEntity
import com.movieappjc.domain.usecases.FavoriteMovieUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@Stable
@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val favoriteMovie: FavoriteMovieUseCase,
    appNavigator: AppNavigator
) : BaseViewModel(appNavigator) {
    private val _movies = MutableStateFlow<DataState<List<MovieEntity>>>(DataState.Loading())
    val movies = _movies.asStateFlow()

    fun getFavoriteMovies() {
        safeLaunch {
            executeTask(favoriteMovie::getFavoriteMovies, _movies)
        }
    }

    fun deleteFavoriteMovie(movieId: Int){
        safeLaunch {
            favoriteMovie.deleteFavoriteMovie(movieId)
            getFavoriteMovies()
        }
    }

    fun onNavigateToMovieDetail(movieId: Int) {
        navigateTo(Routes.MovieDetail(movieId))
    }
}