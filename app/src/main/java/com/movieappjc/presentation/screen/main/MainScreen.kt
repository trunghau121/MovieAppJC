package com.movieappjc.presentation.screen.main

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.core_app.navigation.NavigationEffects
import com.movieappjc.presentation.route.NavHostApp
import com.movieappjc.presentation.viewmodel.main.MainViewModel
import java.util.Locale

@Composable
fun MainScreen(
    mainViewModel: MainViewModel = hiltViewModel(),
    onClickLanguage: (Locale) -> Unit = {}
) {
    val navController = rememberNavController()
    NavigationEffects(
        navigationChannel = mainViewModel.navigationChannel,
        navHostController = navController
    )

    NavHostApp(
        navController = navController,
        onClickLanguage = onClickLanguage
    )


}