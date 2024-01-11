package com.movieappjc.di

import com.core_app.navigation.AppNavigator
import com.core_app.navigation.AppNavigatorImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppNavigatorModule{
    @Provides
    @Singleton
    fun provideAppNavigator(): AppNavigator{
        return AppNavigatorImpl()
    }
}