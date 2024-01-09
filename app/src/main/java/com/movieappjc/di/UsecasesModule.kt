package com.movieappjc.di

import com.movieappjc.domain.usecases.ActionsFavoriteMovie
import com.movieappjc.domain.usecases.GetCastCrew
import com.movieappjc.domain.usecases.GetComingSoon
import com.movieappjc.domain.usecases.GetDetailMovie
import com.movieappjc.domain.usecases.GetPlayingNow
import com.movieappjc.domain.usecases.GetPopular
import com.movieappjc.domain.usecases.GetSearchMovie
import com.movieappjc.domain.usecases.GetTrending
import com.movieappjc.domain.usecases.GetVideoTrailer
import org.koin.dsl.module

val useCaseModule = module {
    single { GetComingSoon(get()) }
    single { GetPlayingNow(get()) }
    single { GetPopular(get()) }
    single { GetTrending(get()) }
    single { GetDetailMovie(get()) }
    single { GetCastCrew(get()) }
    single { GetVideoTrailer(get()) }
    single { ActionsFavoriteMovie(get()) }
    single { GetSearchMovie(get()) }
}