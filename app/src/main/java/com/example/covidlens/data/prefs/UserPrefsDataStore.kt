package com.example.covidlens.data.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class UserPrefsDataStore(
    private val context: Context
) {

    private object PreferencesKeys {
        val LAST_COUNTRY = stringPreferencesKey("last_country")
    }

    /** Devuelve un Flow con el último país (o null si no hay). */
    fun getLastCountryFlow(): Flow<String?> =
        context.dataStore.data.map { prefs ->
            prefs[PreferencesKeys.LAST_COUNTRY]
        }

    /** Guarda el último país. */
    suspend fun saveLastCountry(country: String) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.LAST_COUNTRY] = country
        }
    }
}
