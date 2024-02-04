package com.movieappjc.domain.usecases

import com.core_app.network.DataState
import com.movieappjc.domain.entities.CastEntity
import com.movieappjc.domain.repositories.MovieRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CastCrewUseCase @Inject constructor(private val movieRepository: MovieRepository) {
    operator fun invoke(movieId: Int): Flow<DataState<List<CastEntity>>> {
        return movieRepository.getCastCrew(movieId)
    }
}