package com.movieappjc.data.repositories

import com.core_app.network.DataState
import com.core_app.network.mapState
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

class MovieRepositoryImpl(
    private val movieRemote: MovieServices,
    private val appLocalDataSource: AppLocalDataSource,
    private val movieLocalDataSource: MovieLocalDataSource
) : MovieRepository() {
    override fun getTrending(): Flow<DataState<MoviesResultEntity>> {
        return getResult {
            val language = appLocalDataSource.getPreferredLanguage().first().language
            movieRemote.getTrending(language = language)
        }
    }

    override fun getPopular(): Flow<DataState<MoviesResultEntity>> {
        return getResult {
            val language = appLocalDataSource.getPreferredLanguage().first().language
            movieRemote.getPopular(language = language)
        }
    }

    override fun getPlayingNow(): Flow<DataState<MoviesResultEntity>> {
        return getResult {
            val language = appLocalDataSource.getPreferredLanguage().first().language
            movieRemote.getPlayingNow(language = language)
        }
    }

    override fun getComingSoon(): Flow<DataState<MoviesResultEntity>> {
        return getResult {
            val language = appLocalDataSource.getPreferredLanguage().first().language
            movieRemote.getComingSoon(language = language)
        }
    }

    override fun getDetailMovie(movieId: Int): Flow<DataState<MovieDetailEntity>> {
        return getResult {
            val language = appLocalDataSource.getPreferredLanguage().first().language
            movieRemote.getMovieDetail(language = language, movieId = movieId)
        }
    }

    override fun getCastCrew(movieId: Int): Flow<DataState<List<CastEntity>>> {
        return getResult {
            val language = appLocalDataSource.getPreferredLanguage().first().language
            movieRemote.getCastCrew(language = language, movieId = movieId)
        }.mapState{ it.cast }
    }

    override fun getVideos(movieId: Int): Flow<DataState<List<VideoEntity>>> {
        return getResult {
            movieRemote.getTrailerVideos(movieId = movieId)
        }.mapState { it.videos }
    }

    override fun searchMovie(name: String): Flow<DataState<MoviesResultEntity>> {
        return getResult {
            val language = appLocalDataSource.getPreferredLanguage().first().language
            movieRemote.searchMovie(query = name, language = language)
        }
    }

    override fun getPersonDetail(personId: Int): Flow<DataState<PersonEntity>> {
        return getResult {
            movieRemote.getPersonDetail(personId = personId, language = "")
        }
    }

    override fun getMovieCredits(personId: Int): Flow<DataState<List<MovieEntity>>> {
        return getResult {
            val language = appLocalDataSource.getPreferredLanguage().first().language
            movieRemote.getMovieCredits(personId = personId, language = language)
        }.mapState { it.cast }
    }

    override suspend fun saveMovie(movieEntity: MovieEntity) {
        movieLocalDataSource.saveMovie(movieEntity)
    }

    override fun getFavoriteMovies(): Flow<DataState<List<MovieEntity>>> {
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