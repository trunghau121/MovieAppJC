package com.movieappjc.domain.usecases

import com.core_app.repository.Resource
import com.movieappjc.domain.entities.MoviesResultEntity
import com.movieappjc.domain.repositories.MovieRepository
import kotlinx.coroutines.flow.Flow

class GetSearchMovie(private val movieRepository: MovieRepository) {
    operator fun invoke(name: String): Flow<Resource<MoviesResultEntity>> {
        return movieRepository.searchMovie(name)
    }
}