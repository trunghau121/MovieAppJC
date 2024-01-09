package com.movieappjc.di

import com.movieappjc.data.data_sources.AppLocalDataSource
import com.movieappjc.data.data_sources.AppLocalDataSourceImpl
import com.movieappjc.data.data_sources.MovieLocalDataSource
import com.movieappjc.data.data_sources.MovieLocalDataSourceImpl
import com.movieappjc.data.repositories.AppRepositoryImpl
import com.movieappjc.data.repositories.MovieRepositoryImpl
import com.movieappjc.domain.repositories.AppRepository
import com.movieappjc.domain.repositories.MovieRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<AppLocalDataSource> { AppLocalDataSourceImpl(get()) }
    single<MovieLocalDataSource> { MovieLocalDataSourceImpl(get()) }
    single<AppRepository> { AppRepositoryImpl(get()) }
    single<MovieRepository> { MovieRepositoryImpl(get(), get(), get()) }
}

