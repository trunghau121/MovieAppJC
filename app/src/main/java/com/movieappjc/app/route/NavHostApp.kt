package com.movieappjc.app.route

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.core_app.navigation.NavHost
import com.core_app.utils.StableHolder
import com.movieappjc.presentation.screen.account_setting.AccountSettingScreen
import com.movieappjc.presentation.screen.detail.MovieDetailScreen
import com.movieappjc.presentation.screen.favorite.FavoriteMovieScreen
import com.movieappjc.presentation.screen.menu.MyMenuScreen
import com.movieappjc.presentation.screen.person.PersonDetailScreen
import com.movieappjc.presentation.screen.search.SearchMovieScreen
import com.movieappjc.presentation.screen.test.AbsoluteCoordinatesDragAndDropGridScreen
import com.movieappjc.presentation.screen.trailer_movie.TrailerMovieScreen
import java.util.Locale

@Composable
fun NavHostApp(
    navController: StableHolder<NavHostController>,
    onClickLanguage: (Locale) -> Unit = {}
) {
    NavHost(
        navController = navController(),
        startDestination = Routes.HomeScreen
    ) {
        composable<Routes.HomeScreen> {
            AccountSettingScreen()
        }

        composable<Routes.MovieDetail> {
            MovieDetailScreen()
        }

        composable<Routes.TrailerMovie> {
            TrailerMovieScreen()
        }

        composable<Routes.FavoriteMovieScreen> {
            FavoriteMovieScreen()
        }

        composable<Routes.SearchMovieScreen> {
            SearchMovieScreen()
        }

        composable<Routes.PersonDetailScreen> {
            PersonDetailScreen()
        }
    }
}