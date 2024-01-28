package com.movieappjc.data.repositories

import com.core_app.repository.Resource
import com.movieappjc.data.data_sources.AppLocalDataSource
import com.movieappjc.data.data_sources.MovieLocalDataSource
import com.movieappjc.data.remote.MovieServices
import com.movieappjc.domain.entities.CastEntity
import com.movieappjc.domain.entities.MovieDetailEntity
import com.movieappjc.domain.entities.MovieEntity
import com.movieappjc.domain.entities.MoviesResultEntity
import com.movieappjc.domain.entities.PersonEntity
import com.movieappjc.domain.entities.VideoEntity
import com.movieappjc.domain.repositories.MovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.transform

class MovieRepositoryImpl(
    private val movieRemote: MovieServices,
    private val appLocalDataSource: AppLocalDataSource,
    private val movieLocalDataSource: MovieLocalDataSource
) : MovieRepository() {
    override fun getTrending(): Flow<Resource<MoviesResultEntity>> {
        return getResult {
            val language = appLocalDataSource.getPreferredLanguage().first().language
            movieRemote.getTrending(language = language)
        }
    }

    override fun getPopular(): Flow<Resource<MoviesResultEntity>> {
        return getResult {
            val language = appLocalDataSource.getPreferredLanguage().first().language
            movieRemote.getPopular(language = language)
        }
    }

    override fun getPlayingNow(): Flow<Resource<MoviesResultEntity>> {
        return getResult {
            val language = appLocalDataSource.getPreferredLanguage().first().language
            movieRemote.getPlayingNow(language = language)
        }
    }

    override fun getComingSoon(): Flow<Resource<MoviesResultEntity>> {
        return getResult {
            val language = appLocalDataSource.getPreferredLanguage().first().language
            movieRemote.getComingSoon(language = language)
        }
    }

    override fun getDetailMovie(movieId: Int): Flow<Resource<MovieDetailEntity>> {
        return getResult {
            val language = appLocalDataSource.getPreferredLanguage().first().language
            movieRemote.getMovieDetail(language = language, movieId = movieId)
        }
    }

    override fun getCastCrew(movieId: Int): Flow<Resource<List<CastEntity>>> {
        return getResult {
            val language = appLocalDataSource.getPreferredLanguage().first().language
            movieRemote.getCastCrew(language = language, movieId = movieId)
        }.transform {
            if (it is Resource.Success) {
                emit(Resource.Success(it.data.cast))
            }
        }
    }

    override fun getVideos(movieId: Int): Flow<Resource<List<VideoEntity>>> {
        return getResult {
            movieRemote.getTrailerVideos(movieId = movieId)
        }.transform {
            if (it is Resource.Success) {
                emit(Resource.Success(it.data.videos))
            }
        }
    }

    override fun searchMovie(name: String): Flow<Resource<MoviesResultEntity>> {
        return getResult {
            val language = appLocalDataSource.getPreferredLanguage().first().language
            movieRemote.searchMovie(query = name, language = language)
        }
    }

    override fun getPersonDetail(personId: Int): Flow<Resource<PersonEntity>> {
        return getResult {
            movieRemote.getPersonDetail(personId = personId, language = "")
        }
    }

    override fun getMovieCredits(personId: Int): Flow<Resource<List<MovieEntity>>> {
        return getResult {
            val language = appLocalDataSource.getPreferredLanguage().first().language
            movieRemote.getMovieCredits(personId = personId, language = language)
        }.transform {
            if (it is Resource.Success) {
                emit(Resource.Success(it.data.cast))
            }
        }
    }

    override suspend fun saveMovie(movieEntity: MovieEntity) {
        movieLocalDataSource.saveMovie(movieEntity)
    }

    override fun getFavoriteMovies(): Flow<Resource<List<MovieEntity>>> {
        return getResult {
            movieLocalDataSource.getFavoriteMovies()
        }
    }

    override suspend fun deleteFavoriteMovie(movieId: Int) {
        movieLocalDataSource.deleteFavoriteMovie(movieId)
    }

    override fun checkIfMovieFavorite(movieId: Int): Flow<Boolean> {
        return movieLocalDataSource.checkIfMovieFavorite(movieId)
    }

}