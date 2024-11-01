package com.movieappjc.app.route

import kotlinx.serialization.Serializable

@Serializable
sealed class Routes {
    @Serializable
    data object HomeScreen: Routes()

    @Serializable
    data class MovieDetail(val movieId: Int): Routes()

    @Serializable
    data class TrailerMovie(val movieId: Int): Routes()

    @Serializable
    data object FavoriteMovieScreen: Routes()

    @Serializable
    data object SearchMovieScreen: Routes()

    @Serializable
    data class PersonDetailScreen(val personId: Int): Routes()

    @Serializable
    data object MenuScreen: Routes()
}