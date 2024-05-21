package com.movieappjc.presentation.viewmodel.detail

import androidx.compose.runtime.Stable
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.core_app.base.viewmodel.BaseViewModel
import com.core_app.navigation.AppNavigator
import com.core_app.network.DataState
import com.core_app.network.HandelError
import com.movieappjc.app.route.Routes
import com.movieappjc.domain.entities.CastEntity
import com.movieappjc.domain.entities.MovieDetailEntity
import com.movieappjc.domain.usecases.CastCrewUseCase
import com.movieappjc.domain.usecases.DetailMovieUseCase
import com.movieappjc.domain.usecases.FavoriteMovieUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@Stable
@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    private val detailMovieUseCase: DetailMovieUseCase,
    private val getCastCrew: CastCrewUseCase,
    private val favoriteMovie: FavoriteMovieUseCase,
    appNavigator: AppNavigator,
    savedStateHandle: SavedStateHandle
) : BaseViewModel(appNavigator) {
    private val _movieDetail = MutableStateFlow<DataState<MovieDetailEntity>>(DataState.Loading())
    val movieDetail = _movieDetail.asStateFlow()
    private val _castMovie = MutableStateFlow<DataState<List<CastEntity>>>(DataState.Loading())
    val castMovie = _castMovie.asStateFlow()
    private var movieId: Int = -1
    private val _isMovieFavorite = MutableStateFlow(false)
    val isMovieFavorite = _isMovieFavorite.asStateFlow()

    init {
        movieId = savedStateHandle.toRoute<Routes.MovieDetail>().movieId
        getMovieDetail()
        safeLaunch {
            checkMovieFavorite()
        }
    }

    fun getMovieDetail() {
        safeLaunch {
            _movieDetail.emit(DataState.Loading())
        }
        executeTask(request = { detailMovieUseCase(movieId) },
            success = {
                _movieDetail.value = it
                executeTask(request = { getCastCrew(movieId) }, _castMovie)
            },
            error = {
                _movieDetail.value = DataState.Error(HandelError.getError(it))
            }
        )
    }

    fun saveFavoriteMovie() {
        val movieEntity = _movieDetail.value
        if (movieEntity is DataState.Success) {
            _isMovieFavorite.value = !_isMovieFavorite.value
            safeLaunch {
                if (_isMovieFavorite.value) {
                    favoriteMovie.saveMovie(movieEntity.data.toMovie())
                } else {
                    favoriteMovie.deleteFavoriteMovie(movieId)
                }
            }
        }
    }

    private suspend fun checkMovieFavorite() {
        favoriteMovie.checkIfMovieFavorite(movieId).collect {
            _isMovieFavorite.value = it
        }
    }

    fun openPersonDetailScreen(personId: Int) {
        navigateTo(Routes.PersonDetailScreen(personId))
    }

    fun openTrailerMovie() {
        navigateTo(Routes.TrailerMovie(movieId))
    }
}