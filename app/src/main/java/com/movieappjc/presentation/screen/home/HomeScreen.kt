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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.core_app.extension.aDp
import com.core_app.extension.value
import com.core_app.utils.ImmutableHolder
import com.core_app.utils.StableHolder
import com.movieappjc.app.CountdownTimerUI
import com.movieappjc.app.components.ToUI
import com.movieappjc.app.components.drawer.NavigationDrawerApp
import com.movieappjc.domain.entities.MovieEntity
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
    val state by homeViewModel.movies.collectAsStateWithLifecycle()
    state.ToUI(
        content = { data ->
            NavigationDrawerApp(
                drawerState = drawerState,
                openFavoriteMovie = homeViewModel::openFavoriteMovie,
                onClickLanguage = onClickLanguage
            ) {
                HomeContent(
                    StableHolder(homeViewModel),
                    drawerState,
                    ImmutableHolder(data.data)
                )

                CountdownTimerUI()
            }
        },
        onRetry = {
            homeViewModel.getMovies()
            homeViewModel.loadMovieTabbed(homeViewModel.indexPage)
        }
    )
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
            Spacer(modifier = Modifier.height(20.aDp))
            MovieTabbed(loadMovieTabbed = homeViewModel()::loadMovieTabbed)
            Spacer(modifier = Modifier.height(5.aDp))
            val state by homeViewModel().movieTabbed.collectAsStateWithLifecycle()
            state.ToUI(
                content = {
                    MovieList(
                        ImmutableHolder(it.data.value()),
                        modifier = Modifier.weight(1f),
                        onNavigateToMovieDetail = homeViewModel()::onNavigateToMovieDetail
                    )
                },
                onRetry = {
                    homeViewModel().loadMovieTabbed(homeViewModel().indexPage)
                }
            )
        }
    }
}