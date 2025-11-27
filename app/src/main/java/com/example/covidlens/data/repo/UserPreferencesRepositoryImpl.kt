package com.example.covidlens.data.repo

import com.example.covidlens.data.prefs.UserPrefsDataStore
import com.example.covidlens.domain.repo.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow

class UserPreferencesRepositoryImpl constructor(
    private val userPrefsDataStore: UserPrefsDataStore
) : UserPreferencesRepository {

    override fun getLastCountry(): Flow<String?> {
        return userPrefsDataStore.getLastCountry()
    }

    override suspend fun saveLastCountry(country: String) {
        userPrefsDataStore.saveLastCountry(country)
    }
}
