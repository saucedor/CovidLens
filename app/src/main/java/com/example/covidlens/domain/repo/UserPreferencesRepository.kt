
package com.example.covidlens.domain.repo

interface UserPreferencesRepository {
    suspend fun getLastCountry(): String?
    suspend fun saveLastCountry(country: String)
}