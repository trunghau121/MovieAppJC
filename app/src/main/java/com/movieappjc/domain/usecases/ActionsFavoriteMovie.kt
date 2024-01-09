package com.movieappjc.domain.usecases

import com.core_app.repository.Resource
import com.movieappjc.domain.entities.MovieEntity
import com.movieappjc.domain.repositories.MovieRepository
import kotlinx.coroutines.flow.Flow

class ActionsFavoriteMovie(private val movieRepository: MovieRepository) {
    suspend fun saveMovie(movieEntity: MovieEntity){
        movieRepository.saveMovie(movieEntity)
    }
    fun getFavoriteMovies(): Flow<Resource<List<MovieEntity>>>{
        return movieRepository.getFavoriteMovies()
    }
    suspend fun deleteFavoriteMovie(movieId: Int){
        movieRepository.deleteFavoriteMovie(movieId)
    }
    fun checkIfMovieFavorite(movieId: Int): Flow<Boolean>{
        return movieRepository.checkIfMovieFavorite(movieId)
    }
}