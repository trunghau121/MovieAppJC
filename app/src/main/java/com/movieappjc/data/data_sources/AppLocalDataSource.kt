package com.movieappjc.data.data_sources

import com.core_app.pref.CacheManager
import com.movieappjc.app.common.constants.LANGUAGE_KEY
import com.movieappjc.app.common.constants.VIETNAMESE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.util.Locale

abstract class AppLocalDataSource {
    abstract suspend fun updateLanguage(languageCode: String)
    abstract suspend fun getPreferredLanguage(): Flow<Locale>
}

class AppLocalDataSourceImpl(
    private val cacheManager: CacheManager
) : AppLocalDataSource() {
    override suspend fun updateLanguage(languageCode: String) {
        cacheManager.write(LANGUAGE_KEY, languageCode)
    }

    override suspend fun getPreferredLanguage(): Flow<Locale> {
        return flow<Locale> {
            val language = cacheManager.read(LANGUAGE_KEY, "vi")
            val locale = if (language == "vi") VIETNAMESE else Locale.ENGLISH
            emit(locale)
        }.flowOn(Dispatchers.IO)
    }

}