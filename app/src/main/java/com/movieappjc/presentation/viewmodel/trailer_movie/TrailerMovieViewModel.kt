package com.movieappjc.presentation.viewmodel.trailer_movie

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.core_app.repository.Resource
import com.core_app.viewmodel.BaseViewModel
import com.core_app.navigation.AppNavigator
import com.movieappjc.domain.entities.VideoEntity
import com.movieappjc.domain.usecases.GetVideoTrailer
import com.movieappjc.presentation.route.DestinationKey
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrailerMovieViewModel @Inject constructor(
    private val getVideoTrailer: GetVideoTrailer,
    private val appNavigator: AppNavigator,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {
    private val _videos = MutableStateFlow<Resource<List<VideoEntity>>>(Resource.Loading())
    val videos = _videos.asStateFlow()
    var movieId = -1
    init {
        movieId = savedStateHandle.get<Int>(DestinationKey.MOVIE_ID_KEY) ?: -1
        getTrailer()
    }

    fun getTrailer(){
        viewModelScope.launch {
            _videos.emit(Resource.Loading())
        }
        executeTask(request = { getVideoTrailer(movieId) }, onSuccess = _videos)
    }

    fun onBack() {
        viewModelScope.launch { appNavigator.navigateBack() }
    }
}