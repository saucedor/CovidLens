package com.example.covidlens.domain.usecase

import com.example.covidlens.domain.model.CountryStats
import com.example.covidlens.domain.repo.CovidRepository

class GetCountryStatsUseCase(private val repository: CovidRepository) {
    suspend operator fun invoke(country: String): Result<CountryStats> = repository.getCountryStats(country)
}
