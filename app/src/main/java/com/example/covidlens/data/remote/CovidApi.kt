package com.example.covidlens.data.remote

import com.example.covidlens.data.remote.dto.CovidResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface CovidApi {

    @GET("v1/covid19")
    suspend fun getCountryStats(
        @Query("country") country: String
    ): List<CovidResponseDto>
}