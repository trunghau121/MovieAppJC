package com.movieappjc.presentation.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.core_app.extension.value
import com.core_app.repository.Resource
import com.movieappjc.domain.entities.MovieEntity
import com.movieappjc.presentation.components.ErrorAppComponent
import com.movieappjc.presentation.components.LoadingCircle
import com.movieappjc.presentation.components.drawer.NavigationDrawerApp
import com.movieappjc.presentation.screen.home.movie_carousel.MovieCarouselCompose
import com.movieappjc.presentation.screen.home.movie_tabbed.MovieListCardCompose
import com.movieappjc.presentation.screen.home.movie_tabbed.MovieTabbedCompose
import com.movieappjc.presentation.viewmodel.home.HomeViewModel
import org.koin.androidx.compose.koinViewModel
import java.util.Locale

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = koinViewModel(),
    onClickLanguage: (Locale) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    when (val state = homeViewModel.movies.collectAsState().value) {
        is Resource.Success -> {
            NavigationDrawerApp(drawerState, homeViewModel, onClickLanguage) {
                HomeContent(homeViewModel, drawerState, state.data.data)
            }
        }
        is Resource.Error -> {
            ErrorAppComponent(error = state.error) {
                homeViewModel.getMovies()
                homeViewModel.loadMovieTabbed(homeViewModel.indexPage)
            }
        }
        else -> {
            LoadingCircle()
        }
    }
}

@Composable
fun HomeContent(homeViewModel: HomeViewModel, drawerState: DrawerState, movies: List<MovieEntity>) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(modifier = Modifier.weight(1.15f)) {
            MovieCarouselCompose(homeViewModel, movies)
            AppbarHome(drawerState, homeViewModel)
        }

        Column(modifier = Modifier.weight(0.85f), verticalArrangement = Arrangement.SpaceBetween) {
            Spacer(modifier = Modifier.height(20.dp))
            MovieTabbedCompose(homeViewModel)
            Spacer(modifier = Modifier.height(5.dp))
            when (val state = homeViewModel.movieTabbed.collectAsState().value) {
                is Resource.Success -> {
                    MovieListCardCompose(
                        homeViewModel,
                        state.data.data.value(),
                        modifier = Modifier.weight(1f)
                    )
                }
                is Resource.Error -> {
                    ErrorAppComponent(error = state.error) {
                        homeViewModel.loadMovieTabbed(homeViewModel.indexPage)
                    }
                }
                else -> {
                    LoadingCircle()
                }
            }
        }
    }
}