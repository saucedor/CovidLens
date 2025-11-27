package com.example.covidlens.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Represents the timeline data for a single region of a country from the API response.
 */
data class RegionTimelineDto(
    @SerializedName("country")
    val country: String,
    @SerializedName("region")
    val region: String,
    @SerializedName("cases")
    val cases: Map<String, CaseDayDto>
)

/**
 * Represents the case data for a single day.
 */
data class CaseDayDto(
    @SerializedName("total")
    val total: Int,
    @SerializedName("new")
    val new: Int
)
