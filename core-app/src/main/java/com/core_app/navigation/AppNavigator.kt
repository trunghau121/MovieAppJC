package com.core_app.navigation

import androidx.navigation.NavHostController
import kotlinx.coroutines.channels.Channel

abstract class AppNavigator {
    abstract val navigationChannel: Channel<NavigationIntent>
    var navHostController: NavHostController?= null

    abstract suspend fun navigateBack(
        route: Any? = null,
        inclusive: Boolean = false,
    )

    abstract fun tryNavigateBack(
        route: Any? = null,
        inclusive: Boolean = false,
    )

    abstract suspend fun navigateTo(
        route: Any,
        popUpToRoute: Any? = null,
        inclusive: Boolean = false,
        isSingleTop: Boolean = false,
    )

    abstract fun tryNavigateTo(
        route: Any,
        popUpToRoute: Any? = null,
        inclusive: Boolean = false,
        isSingleTop: Boolean = false,
    )

    fun getCurrentRoute(): String {
        return navHostController?.currentDestination?.route ?: ""
    }
}

sealed class NavigationIntent {
    data class NavigateBack(
        val route: Any? = null,
        val inclusive: Boolean = false,
    ) : NavigationIntent()

    data class NavigateTo(
        val route: Any,
        val popUpToRoute: Any? = null,
        val inclusive: Boolean = false,
        val isSingleTop: Boolean = false,
    ) : NavigationIntent()
}