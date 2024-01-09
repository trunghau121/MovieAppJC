package com.movieappjc.di

import com.core_app.navigation.AppNavigator
import com.core_app.navigation.AppNavigatorImpl
import org.koin.dsl.module

val appNavigatorModule = module {
    single<AppNavigator> { AppNavigatorImpl() }
}