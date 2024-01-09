package com.movieappjc.data.data_sources

import com.core_app.datastore.PreferenceDataStoreHelper
import com.movieappjc.common.constants.LANGUAGE_KEY
import com.movieappjc.common.constants.VIETNAMESE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.util.Locale

abstract class AppLocalDataSource {
    abstract suspend fun updateLanguage(languageCode: String)
    abstract suspend fun getPreferredLanguage(): Flow<Locale>
}

class AppLocalDataSourceImpl(
    private val dataStoreHelper: PreferenceDataStoreHelper
) : AppLocalDataSource() {
    override suspend fun updateLanguage(languageCode: String) {
        dataStoreHelper.putPreference(LANGUAGE_KEY, languageCode)
    }

    override suspend fun getPreferredLanguage(): Flow<Locale> {
        return flow<Locale> {
            val language = dataStoreHelper.getPreference(LANGUAGE_KEY, "vi").first()
            val locale = if (language == "vi") VIETNAMESE else Locale.ENGLISH
            emit(locale)
        }.flowOn(Dispatchers.IO)
    }

}