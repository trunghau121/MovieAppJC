package com.movieappjc.domain.usecases

import com.core_app.repository.Resource
import com.movieappjc.domain.entities.MovieDetailEntity
import com.movieappjc.domain.repositories.MovieRepository
import kotlinx.coroutines.flow.Flow

class GetDetailMovie(private val movieRepository: MovieRepository) {
    operator fun invoke(movieId: Int): Flow<Resource<MovieDetailEntity>> {
        return movieRepository.getDetailMovie(movieId)
    }
}