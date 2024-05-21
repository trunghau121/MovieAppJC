package com.movieappjc.presentation.viewmodel.home

import androidx.compose.runtime.Stable
import com.core_app.base.viewmodel.BaseViewModel
import com.core_app.navigation.AppNavigator
import com.core_app.network.DataState
import com.core_app.network.DataState.Loading
import com.movieappjc.app.route.Routes
import com.movieappjc.domain.entities.MoviesResultEntity
import com.movieappjc.domain.usecases.ComingSoonUseCase
import com.movieappjc.domain.usecases.PlayingNowUseCase
import com.movieappjc.domain.usecases.PopularUseCase
import com.movieappjc.domain.usecases.TrendingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@Stable
@HiltViewModel
class HomeViewModel @Inject constructor(private val getTrending: TrendingUseCase,
                                        private val getPopular: PopularUseCase,
                                        private val getPlayingNow: PlayingNowUseCase,
                                        private val comingSoonUseCase: ComingSoonUseCase,
                                        appNavigator: AppNavigator
    ) : BaseViewModel(appNavigator) {
    private val _movies = MutableStateFlow<DataState<MoviesResultEntity>>(Loading())
    val movies = _movies.asStateFlow()

    private val _movieTabbed = MutableStateFlow<DataState<MoviesResultEntity>>(Loading())
    val movieTabbed = _movieTabbed.asStateFlow()
    private var job: Job?= null
    private var jobTab: Job?= null
    var indexPage: Int = -1

    init {
        getMovies()
    }
    fun getMovies() {
        safeLaunch {
            _movies.emit(Loading())
        }
        job = executeTask({ getTrending() }, _movies)
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
                jobTab = executeTask(request = { comingSoonUseCase() }, onSuccess = _movieTabbed)
            }
        }
    }

    fun onNavigateToMovieDetail(movieId: Int) {
        navigateTo(Routes.MovieDetail(movieId))
    }

    fun openFavoriteMovie() {
        navigateTo(Routes.FavoriteMovieScreen)
    }

    fun openSearchMovie() {
        navigateTo(Routes.SearchMovieScreen)
    }

    override fun onCleared() {
        job?.cancel()
        jobTab?.cancel()
        super.onCleared()
    }
}