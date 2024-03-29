package com.movieappjc.domain.usecases

import com.core_app.network.DataState
import com.movieappjc.domain.entities.MovieEntity
import com.movieappjc.domain.repositories.MovieRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FavoriteMovieUseCase @Inject constructor(private val movieRepository: MovieRepository) {
    suspend fun saveMovie(movieEntity: MovieEntity){
        movieRepository.saveMovie(movieEntity)
    }
    fun getFavoriteMovies(): Flow<DataState<List<MovieEntity>>>{
        return movieRepository.getFavoriteMovies()
    }
    suspend fun deleteFavoriteMovie(movieId: Int){
        movieRepository.deleteFavoriteMovie(movieId)
    }
    fun checkIfMovieFavorite(movieId: Int): Flow<Boolean>{
        return movieRepository.checkIfMovieFavorite(movieId)
    }
}