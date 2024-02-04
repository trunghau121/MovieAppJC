package com.movieappjc.app.di

import android.content.Context
import com.core_app.base.app.AppInitializer
import com.core_app.base.app.AppInitializerImpl
import com.core_app.base.app.NetworkConfig
import com.core_app.base.app.TimberInitializer
import com.core_app.pref.CacheManager
import com.movieappjc.app.MovieJCApp
import com.movieappjc.app.MovieJCNetworkConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    @Singleton
    fun provideApplication(): MovieJCApp {
        return MovieJCApp()
    }

    @Provides
    @Singleton
    fun provideNetworkConfig(): NetworkConfig {
        return MovieJCNetworkConfig()
    }

    @Provides
    @Singleton
    fun provideCacheManager(@ApplicationContext context: Context): CacheManager {
        return CacheManager(context)
    }

    @Provides
    @Singleton
    fun provideTimberInitializer(
        networkConfig: NetworkConfig
    ) = TimberInitializer(networkConfig.isDev())

    @Provides
    @Singleton
    fun provideAppInitializer(
        timberInitializer: TimberInitializer
    ): AppInitializer {
        return AppInitializerImpl(timberInitializer)
    }
}