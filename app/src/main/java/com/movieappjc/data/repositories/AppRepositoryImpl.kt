package com.movieappjc.data.repositories

import com.movieappjc.data.data_sources.AppLocalDataSource
import com.movieappjc.domain.repositories.AppRepository
import kotlinx.coroutines.flow.Flow
import java.util.Locale

class AppRepositoryImpl(private val appLocalDataSource: AppLocalDataSource): AppRepository() {
    override suspend fun updateLanguage(language: String){
        appLocalDataSource.updateLanguage(language)
    }

    override suspend fun getPreferredLanguage(): Flow<Locale> {
        return appLocalDataSource.getPreferredLanguage()
    }
}