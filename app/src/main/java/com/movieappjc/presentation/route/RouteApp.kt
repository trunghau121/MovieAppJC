package com.movieappjc.presentation.route

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.core_app.navigation.composable
import com.movieappjc.presentation.screen.detail.MovieDetailScreen
import com.movieappjc.presentation.screen.favorite.FavoriteMovieScreen
import com.movieappjc.presentation.screen.home.HomeScreen
import com.movieappjc.presentation.screen.search.SearchMovieScreen
import com.movieappjc.presentation.screen.trailer_movie.TrailerMovieScreen
import java.util.Locale

fun NavGraphBuilder.registerHomeScreen(onClickLanguage: (Locale) -> Unit) {
    composable(destination = DestinationApp.HomeScreen) {
        HomeScreen(onClickLanguage = onClickLanguage)
    }
}

fun NavGraphBuilder.registerMovieDetailScreen() {
    composable(
        destination = DestinationApp.MovieDetail,
        arguments = listOf(navArgument(DestinationKey.MOVIE_ID_KEY) { type = NavType.IntType })
    ) {
        MovieDetailScreen()
    }
}

fun NavGraphBuilder.registerTrailerMovieScreen() {
    composable(
        destination = DestinationApp.TrailerMovie,
        arguments = listOf(navArgument(DestinationKey.MOVIE_ID_KEY) { type = NavType.IntType })
    ) {
        TrailerMovieScreen()
    }
}

fun NavGraphBuilder.registerFavoriteMovieScreen() {
    composable(
        destination = DestinationApp.FavoriteMovieScreen
    ) {
        FavoriteMovieScreen()
    }
}

fun NavGraphBuilder.registerSearchMovieScreen() {
    composable(
        destination = DestinationApp.SearchMovieScreen
    ) {
        SearchMovieScreen()
    }
}