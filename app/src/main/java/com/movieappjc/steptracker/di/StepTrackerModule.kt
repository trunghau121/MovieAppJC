package com.movieappjc.steptracker.di

import android.content.Context
import com.movieappjc.steptracker.data.local.AppDatabase
import com.movieappjc.steptracker.data.local.LocalStepDao
import com.movieappjc.steptracker.data.local.StepStoreData
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object StepTrackerModule {

    @Provides
    @Singleton
    fun provideStoreData(@ApplicationContext context: Context): StepStoreData {
        return StepStoreData(context)
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideLocalStepDao(appDatabase: AppDatabase): LocalStepDao {
        return appDatabase.localStepDao()
    }
}
