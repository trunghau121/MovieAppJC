package com.movieappjc.presentation.viewmodel.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.core_app.repository.HandelError
import com.core_app.repository.Resource
import com.core_app.viewmodel.BaseViewModel
import com.core_app.navigation.AppNavigator
import com.movieappjc.domain.entities.CastEntity
import com.movieappjc.domain.entities.MovieDetailEntity
import com.movieappjc.domain.usecases.ActionsFavoriteMovie
import com.movieappjc.domain.usecases.GetCastCrew
import com.movieappjc.domain.usecases.GetDetailMovie
import com.movieappjc.presentation.route.DestinationApp
import com.movieappjc.presentation.route.DestinationKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MovieDetailViewModel(
    private val getDetailMovie: GetDetailMovie,
    private val getCastCrew: GetCastCrew,
    private val favoriteMovie: ActionsFavoriteMovie,
    private val appNavigator: AppNavigator,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {
    private val _movieDetail = MutableStateFlow<Resource<MovieDetailEntity>>(Resource.Loading())
    val movieDetail = _movieDetail.asStateFlow()
    private val _castMovie = MutableStateFlow<Resource<List<CastEntity>>>(Resource.Loading())
    val castMovie = _castMovie.asStateFlow()
    private var movieId: Int = -1
    private val _isMovieFavorite = MutableStateFlow(false)
    val isMovieFavorite = _isMovieFavorite.asStateFlow()

    init {
        movieId = savedStateHandle.get<Int>(DestinationKey.MOVIE_ID_KEY) ?: -1
        getMovieDetail()
        viewModelScope.launch {
            checkMovieFavorite()
        }
    }

    fun getMovieDetail(){
        viewModelScope.launch {
            _movieDetail.emit(Resource.Loading())
        }
        executeTask(request = { getDetailMovie(movieId) },
            success = {
                _movieDetail.value = it
                executeTask(request = { getCastCrew(movieId) }, onSuccess = _castMovie)
            },
            error = {
                _movieDetail.value = Resource.Error( HandelError().getError(it))
            }
        )
    }

    fun saveFavoriteMovie(){
        val movieEntity = _movieDetail.value
        if (movieEntity is Resource.Success) {
            _isMovieFavorite.value = !_isMovieFavorite.value
            viewModelScope.launch {
                if(_isMovieFavorite.value) {
                    favoriteMovie.saveMovie(movieEntity.data.toMovie())
                }else {
                    favoriteMovie.deleteFavoriteMovie(movieId)
                }
            }
        }
    }

    private suspend fun checkMovieFavorite(){
        favoriteMovie.checkIfMovieFavorite(movieId).collect {
            _isMovieFavorite.value = it
        }
    }

    fun onBack() {
        viewModelScope.launch {
            appNavigator.navigateBack()
        }
    }

    fun openTrailerMovie() {
        viewModelScope.launch {
            appNavigator.navigateTo(DestinationApp.TrailerMovie(movieId))
        }
    }
}