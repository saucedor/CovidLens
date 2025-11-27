package com.example.covidlens.data.remote

import com.example.covidlens.data.remote.dto.RegionTimelineDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CovidApi {

    @GET("timeline") // Assuming the endpoint for timeline is /timeline
    suspend fun getCountryTimeline(
        @Query("country") country: String
    ): Response<List<RegionTimelineDto>>

}
