package com.core_app.navigation

import kotlinx.coroutines.channels.Channel

interface AppNavigator {
    val navigationChannel: Channel<NavigationIntent>

    suspend fun navigateBack(
        route: Any? = null,
        inclusive: Boolean = false,
    )

    fun tryNavigateBack(
        route: Any? = null,
        inclusive: Boolean = false,
    )

    suspend fun navigateTo(
        route: Any,
        popUpToRoute: Any? = null,
        inclusive: Boolean = false,
        isSingleTop: Boolean = false,
    )

    fun tryNavigateTo(
        route: Any,
        popUpToRoute: Any? = null,
        inclusive: Boolean = false,
        isSingleTop: Boolean = false,
    )
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