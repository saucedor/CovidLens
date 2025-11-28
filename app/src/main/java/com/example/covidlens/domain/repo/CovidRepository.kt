package com.example.covidlens.domain.repo

import com.example.covidlens.domain.model.RegionCases

interface CovidRepository {
    suspend fun getCountryStats(country: String): List<RegionCases>
    suspend fun getMultipleCountrySnapshot(): List<RegionCases>
}
