package com.movieappjc.presentation.screen.main

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.core_app.navigation.NavHost
import com.core_app.navigation.NavigationEffects
import com.movieappjc.presentation.route.DestinationApp
import com.movieappjc.presentation.route.registerFavoriteMovieScreen
import com.movieappjc.presentation.route.registerHomeScreen
import com.movieappjc.presentation.route.registerMovieDetailScreen
import com.movieappjc.presentation.route.registerSearchMovieScreen
import com.movieappjc.presentation.route.registerTrailerMovieScreen
import com.movieappjc.presentation.viewmodel.main.MainViewModel
import org.koin.androidx.compose.koinViewModel
import java.util.Locale

@Composable
fun MainScreen(
    mainViewModel: MainViewModel = koinViewModel(),
    onClickLanguage: (Locale) -> Unit = {}
) {
    val navController = rememberNavController()
    NavigationEffects(
        navigationChannel = mainViewModel.navigationChannel,
        navHostController = navController
    )

    NavHost(
        navController = navController,
        startDestination = DestinationApp.HomeScreen
    ) {
        registerHomeScreen(onClickLanguage)
        registerMovieDetailScreen()
        registerTrailerMovieScreen()
        registerFavoriteMovieScreen()
        registerSearchMovieScreen()
    }


}