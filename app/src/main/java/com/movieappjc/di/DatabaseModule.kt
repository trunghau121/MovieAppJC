package com.movieappjc.di

import androidx.room.Room
import com.movieappjc.data.local.MovieDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            MovieDatabase::class.java,
            "movieappjc-database"
        ).build()
    }

    single { get<MovieDatabase>().movieDao }
}