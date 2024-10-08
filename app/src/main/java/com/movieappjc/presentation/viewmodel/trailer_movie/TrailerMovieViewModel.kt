package com.movieappjc.presentation.viewmodel.trailer_movie

import androidx.compose.runtime.Stable
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.core_app.base.viewmodel.BaseViewModel
import com.core_app.navigation.AppNavigator
import com.core_app.network.DataState
import com.movieappjc.app.route.Routes
import com.movieappjc.domain.entities.VideoEntity
import com.movieappjc.domain.usecases.VideoTrailerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@Stable
@HiltViewModel
class TrailerMovieViewModel @Inject constructor(
    private val getVideoTrailer: VideoTrailerUseCase,
    appNavigator: AppNavigator,
    savedStateHandle: SavedStateHandle
) : BaseViewModel(appNavigator) {
    private val _videos = MutableStateFlow<DataState<List<VideoEntity>>>(DataState.Loading())
    val videos = _videos.asStateFlow()
    var movieId = -1
    init {
        movieId = savedStateHandle.toRoute<Routes.TrailerMovie>().movieId
        getTrailer()
    }

    fun getTrailer(){
        getVideoTrailer(movieId).executeTask(_videos)
    }
}