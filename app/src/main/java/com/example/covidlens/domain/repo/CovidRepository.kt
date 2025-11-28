package com.example.covidlens.domain.repo

import com.example.covidlens.domain.model.RegionCases

interface CovidRepository {
    suspend fun getCountryStats(country: String): Result<List<RegionCases>>
    suspend fun getMultiCountrySnapshot(countries: List<String>): Result<List<RegionCases>>
}