package com.core_app.di

import com.core_app.datastore.PreferenceDataStoreHelper
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataStoreKoinModule = module {
    single {
        PreferenceDataStoreHelper(androidContext())
    }
}