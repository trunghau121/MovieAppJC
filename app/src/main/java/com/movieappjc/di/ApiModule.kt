package com.movieappjc.di

import com.movieappjc.data.remote.MovieServices
import org.koin.dsl.module
import retrofit2.Retrofit

val apiModule = module {
    single(createdAtStart = false) { get<Retrofit>().create(MovieServices::class.java) }
}