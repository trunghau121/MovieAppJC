package com.movieappjc.domain.usecases

import com.core_app.repository.Resource
import com.movieappjc.domain.entities.MoviesResultEntity
import com.movieappjc.domain.repositories.MovieRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PlayingNowUseCase @Inject constructor(private val movieRepository: MovieRepository) {
    operator fun invoke(): Flow<Resource<MoviesResultEntity>> {
        return movieRepository.getPlayingNow()
    }
}