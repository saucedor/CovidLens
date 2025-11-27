package com.example.covidlens.domain.usecase

import com.example.covidlens.domain.model.CountryStats
import com.example.covidlens.domain.repo.CovidRepository

class GetMultiCountrySnapshotUseCase(private val repository: CovidRepository) {
    suspend operator fun invoke(countries: List<String>): Result<List<CountryStats>> = repository.getSnapshot(countries)
}
