package com.core_app.pref

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

class CacheStore(context: Context, fileName: String) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = fileName)
    private val dataStore = context.dataStore

    suspend fun <T> read(key: Preferences.Key<T>, defaultValue: T): T {
        return dataStore.data.first()[key] ?: defaultValue
    }

    suspend fun <T> write(key: Preferences.Key<T>, value: T) {
        dataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    suspend fun <T> remove(key: Preferences.Key<T>) {
        dataStore.edit { preferences ->
            preferences.remove(key)
        }
    }

    suspend fun <T> clearAll() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}