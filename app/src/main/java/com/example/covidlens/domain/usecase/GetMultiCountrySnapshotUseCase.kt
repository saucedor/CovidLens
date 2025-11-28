package com.example.covidlens.domain.usecase

import com.example.covidlens.domain.model.RegionCases
import com.example.covidlens.domain.repo.CovidRepository

class GetMultiCountrySnapshotUseCase(
    private val repo: CovidRepository
) {
    suspend operator fun invoke(): List<RegionCases> {
        return repo.getMultipleCountrySnapshot()
    }
}
