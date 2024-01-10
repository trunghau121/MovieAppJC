package com.movieappjc.presentation.screen.main

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.core_app.navigation.NavigationEffects
import com.movieappjc.presentation.route.NavHostApp
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

    NavHostApp(
        navController = navController,
        onClickLanguage = onClickLanguage
    )


}