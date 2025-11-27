package com.example.covidlens.domain.repo

import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    fun getLastCountry(): Flow<String?>
    suspend fun saveLastCountry(country: String)
}
