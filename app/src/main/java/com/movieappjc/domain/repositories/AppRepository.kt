package com.movieappjc.domain.repositories

import kotlinx.coroutines.flow.Flow
import java.util.Locale

abstract class AppRepository {
    abstract suspend fun updateLanguage(language: String)
    abstract suspend fun getPreferredLanguage(): Flow<Locale>
}