package com.movieappjc.domain.usecases

import com.core_app.network.DataState
import com.movieappjc.domain.entities.MovieEntity
import com.movieappjc.domain.repositories.MovieRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MovieCreditsUseCase @Inject constructor(private val movieRepository: MovieRepository) {
    operator fun invoke(personId: Int): Flow<DataState<List<MovieEntity>>> {
        return movieRepository.getMovieCredits(personId)
    }
}