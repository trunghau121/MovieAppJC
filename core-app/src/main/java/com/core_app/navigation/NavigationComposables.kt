package com.core_app.navigation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun NavHost(
    navController: NavHostController,
    startDestination: Destination,
    modifier: Modifier = Modifier,
    route: String? = null,
    builder: NavGraphBuilder.() -> Unit
) {
    val time = 400
    val offsetX = 100
    val timeSlide = 400
    NavHost(
        navController = navController,
        startDestination = startDestination.fullRoute,
        modifier = modifier,
        route = route,
        builder = builder,
        enterTransition = {
            fadeIn(animationSpec = tween(time)) + slideInHorizontally(
                animationSpec = tween(timeSlide), initialOffsetX = { offsetX }
            )
        },
        exitTransition = {
            fadeOut(animationSpec = tween(time)) + slideOutHorizontally(
                animationSpec = tween(timeSlide), targetOffsetX = { -offsetX }
            )
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(time)) + slideInHorizontally(
                animationSpec = tween(timeSlide), initialOffsetX = { -offsetX }
            )
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(time)) + slideOutHorizontally(
                animationSpec = tween(timeSlide), targetOffsetX = { offsetX }
            )
        }
    )
}

fun NavGraphBuilder.composable(
    destination: Destination,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit
) {
    composable(
        route = destination.fullRoute,
        arguments = arguments,
        deepLinks = deepLinks,
        content = content
    )
}