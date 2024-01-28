package com.movieappjc.presentation.route

import com.core_app.navigation.Destination
import com.core_app.navigation.NoArgumentsDestination
import com.core_app.navigation.appendParams

class DestinationApp {
    data object HomeScreen : NoArgumentsDestination("home")
    data object MovieDetail : Destination("movie_detail", DestinationKey.MOVIE_ID_KEY) {
        operator fun invoke(movieId: Int): String = route.appendParams(
            DestinationKey.MOVIE_ID_KEY to movieId
        )
    }

    data object TrailerMovie : Destination("trailer_movie", DestinationKey.MOVIE_ID_KEY) {
        operator fun invoke(movieId: Int): String = route.appendParams(
            DestinationKey.MOVIE_ID_KEY to movieId
        )
    }

    data object FavoriteMovieScreen : NoArgumentsDestination("favorite_movie")
    data object SearchMovieScreen : NoArgumentsDestination("search_movie")
    data object PersonDetailScreen : Destination(
        "person_detail", DestinationKey.PERSON_ID_KEY
    ) {
        operator fun invoke(personId: Int): String = route.appendParams(
            DestinationKey.PERSON_ID_KEY to personId
        )
    }
}