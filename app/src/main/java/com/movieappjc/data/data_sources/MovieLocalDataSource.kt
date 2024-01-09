package com.movieappjc.data.data_sources

import com.movieappjc.data.local.MovieDao
import com.movieappjc.data.local.MovieTable
import com.movieappjc.data.local.fromMovieEntity
import com.movieappjc.domain.entities.MovieEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.transform

abstract class MovieLocalDataSource {
    abstract suspend fun saveMovie(movieEntity: MovieEntity)
    abstract suspend fun getFavoriteMovies(): List<MovieTable>
    abstract suspend fun deleteFavoriteMovie(movieId: Int)
    abstract fun checkIfMovieFavorite(movieId: Int): Flow<Boolean>
}

class MovieLocalDataSourceImpl(private val movieDao: MovieDao): MovieLocalDataSource() {
    override suspend fun saveMovie(movieEntity: MovieEntity) {
        movieDao.insert(movieEntity.fromMovieEntity())
    }

    override suspend fun getFavoriteMovies(): List<MovieTable> {
        return movieDao.getMovies()
    }

    override suspend fun deleteFavoriteMovie(movieId: Int) {
        movieDao.delete(movieId)
    }

    override fun checkIfMovieFavorite(movieId: Int): Flow<Boolean> {
        return movieDao.checkMovie(movieId).flowOn(Dispatchers.IO).transform {
            emit(it != null)
        }
    }

}