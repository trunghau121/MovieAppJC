package com.movieappjc.di

import com.movieappjc.presentation.viewmodel.detail.MovieDetailViewModel
import com.movieappjc.presentation.viewmodel.favorite.FavoriteViewModel
import com.movieappjc.presentation.viewmodel.home.HomeViewModel
import com.movieappjc.presentation.viewmodel.main.MainViewModel
import com.movieappjc.presentation.viewmodel.search.SearchMovieViewModel
import com.movieappjc.presentation.viewmodel.trailer_movie.TrailerMovieViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { MainViewModel(get()) }
    viewModel { HomeViewModel(get(), get(), get(), get(), get()) }
    viewModel { MovieDetailViewModel(get(), get(), get(), get(), get()) }
    viewModel { TrailerMovieViewModel(get(), get(), get()) }
    viewModel { FavoriteViewModel(get(), get()) }
    viewModel { SearchMovieViewModel(get(), get()) }
}