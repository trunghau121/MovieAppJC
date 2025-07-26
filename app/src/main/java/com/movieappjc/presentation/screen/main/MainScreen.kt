package com.movieappjc.presentation.screen.main

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.core_app.navigation.NavigationEffects
import com.core_app.utils.StableHolder
import com.movieappjc.app.route.NavHostApp
import com.movieappjc.presentation.viewmodel.main.MainViewModel
import java.util.Locale

@Composable
fun MainScreen(
    mainViewModel: MainViewModel = hiltViewModel(),
    onClickLanguage: (Locale) -> Unit = {}
) {
    val navController = rememberNavController()
    mainViewModel.appNavigator.navHostController = navController

    NavigationEffects(
        navigationChannel = mainViewModel.navigationChannel,
        navHostController = navController
    )

    NavHostApp(
        navController = StableHolder(navController),
        onClickLanguage = onClickLanguage
    )
}