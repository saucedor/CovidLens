package com.example.covidlens.domain.usecase

import com.example.covidlens.domain.model.RegionCases
import com.example.covidlens.domain.repo.CovidRepository

class GetCountryStatsUseCase(
    private val repo: CovidRepository
) {
    suspend operator fun invoke(country: String): List<RegionCases> {
        return repo.getCountryStats(country)
    }
}
