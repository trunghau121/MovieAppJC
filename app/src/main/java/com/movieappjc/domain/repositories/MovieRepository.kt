package com.movieappjc.domain.repositories

import com.core_app.network.DataState
import com.core_app.network.BaseRepository
import com.movieappjc.domain.entities.CastEntity
import com.movieappjc.domain.entities.MovieDetailEntity
import com.movieappjc.domain.entities.MovieEntity
import com.movieappjc.domain.entities.MoviesResultEntity
import com.movieappjc.domain.entities.PersonEntity
import com.movieappjc.domain.entities.VideoEntity
import kotlinx.coroutines.flow.Flow


abstract class MovieRepository: BaseRepository() {
    abstract fun getTrending(): Flow<DataState<MoviesResultEntity>>
    abstract fun getPopular(): Flow<DataState<MoviesResultEntity>>
    abstract fun getPlayingNow(): Flow<DataState<MoviesResultEntity>>
    abstract fun getComingSoon(): Flow<DataState<MoviesResultEntity>>
    abstract fun getDetailMovie(movieId: Int): Flow<DataState<MovieDetailEntity>>
    abstract fun getCastCrew(movieId: Int): Flow<DataState<List<CastEntity>>>
    abstract fun getVideos(movieId: Int): Flow<DataState<List<VideoEntity>>>
    abstract fun searchMovie(name: String): Flow<DataState<MoviesResultEntity>>
    abstract fun getPersonDetail(personId: Int): Flow<DataState<PersonEntity>>
    abstract fun getMovieCredits(personId: Int): Flow<DataState<List<MovieEntity>>>

    // Functions for Local resources
    abstract suspend fun saveMovie(movieEntity: MovieEntity)
    abstract fun getFavoriteMovies(): Flow<DataState<List<MovieEntity>>>
    abstract suspend fun deleteFavoriteMovie(movieId: Int)
    abstract fun checkIfMovieFavorite(movieId: Int): Flow<Boolean>
}