package com.movieappjc.presentation.viewmodel.home

import androidx.lifecycle.viewModelScope
import com.core_app.repository.Resource
import com.core_app.repository.Resource.Loading
import com.core_app.viewmodel.BaseViewModel
import com.movieappjc.domain.entities.MoviesResultEntity
import com.movieappjc.domain.usecases.GetComingSoon
import com.movieappjc.domain.usecases.GetPlayingNow
import com.movieappjc.domain.usecases.GetPopular
import com.movieappjc.domain.usecases.GetTrending
import com.core_app.navigation.AppNavigator
import com.movieappjc.presentation.route.DestinationApp
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class HomeViewModel(private val getTrending: GetTrending,
                    private val getPopular: GetPopular,
                    private val getPlayingNow: GetPlayingNow,
                    private val getComingSoon: GetComingSoon,
                    private val appNavigator: AppNavigator
    ) : BaseViewModel() {
    private val _movies = MutableStateFlow<Resource<MoviesResultEntity>>(Loading())
    val movies = _movies.asStateFlow()

    private val _movieTabbed = MutableStateFlow<Resource<MoviesResultEntity>>(Loading())
    val movieTabbed = _movieTabbed.asStateFlow()
    private var job: Job?= null
    private var jobTab: Job?= null
    var indexPage: Int = -1

    init {
        getMovies()
    }
    fun getMovies() {
        viewModelScope.launch {
            _movies.emit(Loading())
        }
        job = executeTask(request = { getTrending() }, onSuccess = _movies)
    }

    fun loadMovieTabbed(indexPage: Int){
        if (this.indexPage == indexPage) return
        this.indexPage = indexPage

        jobTab?.cancel()
        _movieTabbed.value = Loading()
        when (indexPage) {
            0 -> {
                jobTab = executeTask(request = { getPopular() }, onSuccess = _movieTabbed)
            }
            1 -> {
                jobTab= executeTask(request = { getPlayingNow() }, onSuccess = _movieTabbed)
            }
            2 -> {
                jobTab = executeTask(request = { getComingSoon() }, onSuccess = _movieTabbed)
            }
        }
    }

    fun onNavigateToMovieDetail(movieId: Int) {
        viewModelScope.launch {
            appNavigator.navigateTo(DestinationApp.MovieDetail(movieId))
        }
    }

    fun openFavoriteMovie() {
        viewModelScope.launch {
            appNavigator.navigateTo(DestinationApp.FavoriteMovieScreen.fullRoute)
        }
    }

    fun openSearchMovie() {
        viewModelScope.launch {
            appNavigator.navigateTo(DestinationApp.SearchMovieScreen.fullRoute)
        }
    }

    override fun onCleared() {
        job?.cancel()
        jobTab?.cancel()
        super.onCleared()
    }
}