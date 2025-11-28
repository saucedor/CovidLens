package com.example.covidlens.domain.prefs

interface UserPreferencesRepository {
    suspend fun getLastCountry(): String?
    suspend fun saveLastCountry(country: String)
}
