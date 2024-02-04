package com.movieappjc.domain.usecases

import com.core_app.network.DataState
import com.movieappjc.domain.entities.MoviesResultEntity
import com.movieappjc.domain.repositories.MovieRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ComingSoonUseCase @Inject constructor(private val movieRepository: MovieRepository) {
    operator fun invoke(): Flow<DataState<MoviesResultEntity>> {
        return movieRepository.getComingSoon()
    }
}