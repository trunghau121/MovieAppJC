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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.core_app.extension.value
import com.core_app.repository.Resource
import com.core_app.utils.ImmutableHolder
import com.core_app.utils.StableHolder
import com.movieappjc.domain.entities.MovieEntity
import com.movieappjc.presentation.components.ErrorApp
import com.movieappjc.presentation.components.CircularProgressBar
import com.movieappjc.presentation.components.drawer.NavigationDrawerApp
import com.movieappjc.presentation.screen.home.movie_carousel.CarouselMovie
import com.movieappjc.presentation.screen.home.movie_tabbed.MovieList
import com.movieappjc.presentation.screen.home.movie_tabbed.MovieTabbed
import com.movieappjc.presentation.viewmodel.home.HomeViewModel
import java.util.Locale

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = hiltViewModel(),
    onClickLanguage: (Locale) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    when (val state = homeViewModel.movies.collectAsStateWithLifecycle().value) {
        is Resource.Success -> {
            NavigationDrawerApp(
                drawerState = drawerState,
                openFavoriteMovie = homeViewModel::openFavoriteMovie,
                onClickLanguage = onClickLanguage
            ) {
                HomeContent(
                    StableHolder(homeViewModel),
                    drawerState,
                    ImmutableHolder(state.data.data)
                )
            }
        }

        is Resource.Error -> {
            ErrorApp(error = state.error) {
                homeViewModel.getMovies()
                homeViewModel.loadMovieTabbed(homeViewModel.indexPage)
            }
        }

        else -> {
            CircularProgressBar()
        }
    }
}

@Composable
fun HomeContent(
    homeViewModel: StableHolder<HomeViewModel>,
    drawerState: DrawerState,
    movies: ImmutableHolder<List<MovieEntity>>
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(modifier = Modifier.weight(1.15f)) {
            CarouselMovie(
                movies = movies,
                onNavigateToMovieDetail = homeViewModel()::onNavigateToMovieDetail
            )
            HomeAppBar(
                drawerState = drawerState,
                openSearchMovie = homeViewModel()::openSearchMovie
            )
        }

        Column(modifier = Modifier.weight(0.85f), verticalArrangement = Arrangement.SpaceBetween) {
            Spacer(modifier = Modifier.height(20.dp))
            MovieTabbed(loadMovieTabbed = homeViewModel()::loadMovieTabbed)
            Spacer(modifier = Modifier.height(5.dp))
            when (val state = homeViewModel().movieTabbed.collectAsStateWithLifecycle().value) {
                is Resource.Success -> {
                    MovieList(
                        ImmutableHolder(state.data.data.value()),
                        modifier = Modifier.weight(1f),
                        onNavigateToMovieDetail = homeViewModel()::onNavigateToMovieDetail
                    )
                }

                is Resource.Error -> {
                    ErrorApp(error = state.error) {
                        homeViewModel().loadMovieTabbed(homeViewModel().indexPage)
                    }
                }

                else -> {
                    CircularProgressBar()
                }
            }
        }
    }
}