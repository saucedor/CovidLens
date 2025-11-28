package com.example.covidlens.data.repo

import com.example.covidlens.data.remote.CovidApi
import com.example.covidlens.domain.model.RegionCases
import com.example.covidlens.domain.repo.CovidRepository

class CovidRepositoryImpl(
    private val api: CovidApi
) : CovidRepository {

    override suspend fun getCountryStats(country: String): List<RegionCases> {
        return api.getCountryStats(country).map { dto ->
            dto.toDomainSimple()
        }
    }

    override suspend fun getMultipleCountrySnapshot(): List<RegionCases> {
        val defaultCountries = listOf("Mexico", "USA", "Japan", "Spain", "Germany")

        return defaultCountries.flatMap { c ->
            api.getCountryStats(c).map { dto ->
                dto.toDomainSimple()
            }
        }
    }
}

// =========================================
// LOCAL EXTENSION DTO → DOMAIN (NO MAPPER FILE)
// =========================================

private fun com.example.covidlens.data.remote.dto.CovidResponseDto.toDomainSimple()
        : com.example.covidlens.domain.model.RegionCases {

    val timeline = cases.map { (date, entry) ->
        com.example.covidlens.domain.model.CaseCount(
            date = date,
            total = entry.total ?: 0,
            new= entry.newCases ?: 0
        )
    }.sortedBy { it.date }

    return com.example.covidlens.domain.model.RegionCases(
        country = country,
        region = region,
        timeline = timeline
    )
}
