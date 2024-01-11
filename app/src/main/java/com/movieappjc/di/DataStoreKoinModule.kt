package com.movieappjc.di

import android.content.Context
import com.core_app.datastore.PreferenceDataStoreHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreKoinModule{
    @Provides
    @Singleton
    fun provideDataStoreHelper(@ApplicationContext context: Context): PreferenceDataStoreHelper {
        return PreferenceDataStoreHelper(context)
    }
}