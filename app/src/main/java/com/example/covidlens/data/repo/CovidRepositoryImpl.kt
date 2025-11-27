package com.example.covidlens.data.repo

import com.example.covidlens.data.mapper.toDomain
import com.example.covidlens.data.remote.CovidApi
import com.example.covidlens.domain.model.CountryStats
import com.example.covidlens.domain.repo.CovidRepository

class CovidRepositoryImpl constructor(
    private val api: CovidApi
) : CovidRepository {

    override suspend fun getCountryStats(country: String): Result<CountryStats> = runCatching {
        val response = api.getCountryTimeline(country)

        if (!response.isSuccessful) {
            error("Error ${response.code()}: ${response.errorBody()?.string().orEmpty()}")
        }

        val dtoList = response.body()
            ?: error("No data found for the country")

        dtoList.toDomain()
    }

    override suspend fun getSnapshot(countries: List<String>): Result<List<CountryStats>> =
        runCatching {
            countries.mapNotNull { country ->
                val res = api.getCountryTimeline(country)
                if (res.isSuccessful) {
                    res.body()?.toDomain()
                } else null
            }
        }
}
