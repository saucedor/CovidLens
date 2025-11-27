package com.example.covidlens.data.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.covidlens.domain.repo.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class UserPrefsDataStore(
    private val context: Context
) : UserPreferencesRepository {

    private object PreferencesKeys {
        val LAST_COUNTRY = stringPreferencesKey("last_country")
    }

    override fun getLastCountry(): Flow<String?> =
        context.dataStore.data.map { prefs ->
            prefs[PreferencesKeys.LAST_COUNTRY]
        }

    override suspend fun saveLastCountry(country: String) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.LAST_COUNTRY] = country
        }
    }
}
