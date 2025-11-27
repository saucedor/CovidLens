package com.example.covidlens.domain.repo

import com.example.covidlens.domain.model.CountryStats

interface CovidRepository {
    suspend fun getCountryStats(country: String): Result<CountryStats>
    suspend fun getSnapshot(countries: List<String>): Result<List<CountryStats>>
}
