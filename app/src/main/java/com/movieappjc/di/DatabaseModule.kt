package com.movieappjc.di

import android.content.Context
import androidx.room.Room
import com.movieappjc.data.local.MovieDao
import com.movieappjc.data.local.MovieDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule{
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MovieDatabase{
        return Room.databaseBuilder(
            context,
            MovieDatabase::class.java,
            "movieappjc-database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideMovieDao(database: MovieDatabase): MovieDao{
        return database.movieDao
    }
}