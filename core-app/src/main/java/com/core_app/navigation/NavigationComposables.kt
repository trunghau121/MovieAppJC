package com.core_app.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import kotlin.reflect.KClass

@Composable
fun NavHost(
    navController: NavHostController,
    startDestination: Any,
    modifier: Modifier = Modifier,
    route: KClass<*>? = null,
    builder: NavGraphBuilder.() -> Unit
) {
    val time = 400
    val offsetX = 100
    val timeSlide = 400
    NavHost(
        navController = navController,
        startDestination = startDestination,
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