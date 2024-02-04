package com.movieappjc.app.route

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.core_app.navigation.NavHost
import com.core_app.navigation.composable
import com.core_app.utils.StableHolder
import com.movieappjc.presentation.screen.detail.MovieDetailScreen
import com.movieappjc.presentation.screen.favorite.FavoriteMovieScreen
import com.movieappjc.presentation.screen.home.HomeScreen
import com.movieappjc.presentation.screen.person.PersonDetailScreen
import com.movieappjc.presentation.screen.search.SearchMovieScreen
import com.movieappjc.presentation.screen.trailer_movie.TrailerMovieScreen
import java.util.Locale

@Composable
fun NavHostApp(
    navController: StableHolder<NavHostController>,
    onClickLanguage: (Locale) -> Unit = {}
) {
    NavHost(
        navController = navController(),
        startDestination = DestinationApp.HomeScreen
    ) {
        composable(destination = DestinationApp.HomeScreen) {
            HomeScreen(onClickLanguage = onClickLanguage)
        }

        composable(
            destination = DestinationApp.MovieDetail,
            arguments = listOf(navArgument(DestinationKey.MOVIE_ID_KEY) { type = NavType.IntType })
        ) {
            MovieDetailScreen()
        }

        composable(
            destination = DestinationApp.TrailerMovie,
            arguments = listOf(navArgument(DestinationKey.MOVIE_ID_KEY) { type = NavType.IntType })
        ) {
            TrailerMovieScreen()
        }

        composable(destination = DestinationApp.FavoriteMovieScreen) {
            FavoriteMovieScreen()
        }

        composable(destination = DestinationApp.SearchMovieScreen) {
            SearchMovieScreen()
        }

        composable(
            destination = DestinationApp.PersonDetailScreen,
            arguments = listOf(navArgument(DestinationKey.PERSON_ID_KEY) { type = NavType.IntType })
        ){
            PersonDetailScreen()
        }
    }
}