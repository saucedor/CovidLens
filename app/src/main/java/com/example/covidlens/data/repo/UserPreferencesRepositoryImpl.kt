package com.example.covidlens.data.repo

import com.example.covidlens.data.prefs.UserPrefsDataStore
import kotlinx.coroutines.flow.firstOrNull

class UserPreferencesRepositoryImpl(
    private val dataStore: UserPrefsDataStore
) : UserPreferencesRepository {

    override suspend fun getLastCountry(): String? {
        return dataStore.getLastCountryFlow().firstOrNull()
    }

    override suspend fun saveLastCountry(country: String) {
        dataStore.saveLastCountry(country)
    }
}
