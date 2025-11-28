package com.example.covidlens.domain.repo

import com.example.covidlens.data.mapper.toDomain
import com.example.covidlens.data.remote.CovidApi
import com.example.covidlens.domain.model.RegionCases
import com.example.covidlens.domain.repo.CovidRepository
import com.example.covidlens.domain.repo.UserPreferencesRepository

class CovidRepositoryImpl(
    private val api: CovidApi,
    private val prefs: UserPreferencesRepository
) : CovidRepository {

    override suspend fun getCountryStats(country: String): Result<List<RegionCases>> {
        return try {
            val response = api.getCountryStats(country)
            if (response.isEmpty()) {
                Result.failure(IllegalArgumentException("País no encontrado"))
            } else {
                // guardamos última consulta
                prefs.saveLastCountry(country)
                Result.success(response.map { it.toDomain() })
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión o API", e))
        }
    }

    override suspend fun getMultiCountrySnapshot(countries: List<String>): Result<List<RegionCases>> {
        return try {
            val out = buildList {
                for (c in countries) {
                    val res = api.getCountryStats(c)
                    if (res.isNotEmpty()) addAll(res.map { it.toDomain() })
                }
            }
            if (out.isEmpty()) {
                Result.failure(IllegalStateException("No se pudieron obtener datos"))
            } else {
                Result.success(out)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
