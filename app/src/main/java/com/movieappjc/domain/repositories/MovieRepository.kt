package com.movieappjc.domain.repositories

import com.core_app.repository.BaseRepository
import com.core_app.repository.Resource
import com.movieappjc.domain.entities.CastEntity
import com.movieappjc.domain.entities.MovieDetailEntity
import com.movieappjc.domain.entities.MovieEntity
import com.movieappjc.domain.entities.MoviesResultEntity
import com.movieappjc.domain.entities.PersonEntity
import com.movieappjc.domain.entities.VideoEntity
import kotlinx.coroutines.flow.Flow


abstract class MovieRepository: BaseRepository() {
    abstract fun getTrending(): Flow<Resource<MoviesResultEntity>>
    abstract fun getPopular(): Flow<Resource<MoviesResultEntity>>
    abstract fun getPlayingNow(): Flow<Resource<MoviesResultEntity>>
    abstract fun getComingSoon(): Flow<Resource<MoviesResultEntity>>
    abstract fun getDetailMovie(movieId: Int): Flow<Resource<MovieDetailEntity>>
    abstract fun getCastCrew(movieId: Int): Flow<Resource<List<CastEntity>>>
    abstract fun getVideos(movieId: Int): Flow<Resource<List<VideoEntity>>>
    abstract fun searchMovie(name: String): Flow<Resource<MoviesResultEntity>>
    abstract fun getPersonDetail(personId: Int): Flow<Resource<PersonEntity>>
    abstract fun getMovieCredits(personId: Int): Flow<Resource<List<MovieEntity>>>

    // Functions for Local resources
    abstract suspend fun saveMovie(movieEntity: MovieEntity)
    abstract fun getFavoriteMovies(): Flow<Resource<List<MovieEntity>>>
    abstract suspend fun deleteFavoriteMovie(movieId: Int)
    abstract fun checkIfMovieFavorite(movieId: Int): Flow<Boolean>
}