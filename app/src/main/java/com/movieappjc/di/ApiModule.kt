package com.movieappjc.di

import com.movieappjc.data.remote.MovieServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule{
    @Provides
    @Singleton
    fun provideMovieServices(retrofit: Retrofit): MovieServices{
        return retrofit.create(MovieServices::class.java)
    }
}