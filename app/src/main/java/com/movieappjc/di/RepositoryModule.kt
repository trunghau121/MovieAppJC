package com.movieappjc.di

import com.core_app.pref.CacheManager
import com.movieappjc.data.data_sources.AppLocalDataSource
import com.movieappjc.data.data_sources.AppLocalDataSourceImpl
import com.movieappjc.data.data_sources.MovieLocalDataSource
import com.movieappjc.data.data_sources.MovieLocalDataSourceImpl
import com.movieappjc.data.local.MovieDao
import com.movieappjc.data.remote.MovieServices
import com.movieappjc.data.repositories.AppRepositoryImpl
import com.movieappjc.data.repositories.MovieRepositoryImpl
import com.movieappjc.domain.repositories.AppRepository
import com.movieappjc.domain.repositories.MovieRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAppLocalDataSource(cacheManager: CacheManager): AppLocalDataSource{
        return AppLocalDataSourceImpl(cacheManager)
    }

    @Provides
    @Singleton
    fun provideMovieLocalDataSource(movieDao: MovieDao): MovieLocalDataSource{
        return MovieLocalDataSourceImpl(movieDao)
    }

    @Provides
    @Singleton
    fun provideAppRepository(appLocalDataSource: AppLocalDataSource): AppRepository{
        return AppRepositoryImpl(appLocalDataSource)
    }

    @Provides
    @Singleton
    fun provideMovieRepository(
        movieRemote: MovieServices,
        appLocalDataSource: AppLocalDataSource,
        movieLocalDataSource: MovieLocalDataSource
    ): MovieRepository{
        return MovieRepositoryImpl(movieRemote, appLocalDataSource, movieLocalDataSource)
    }
}

