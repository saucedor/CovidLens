package com.example.covidlens.data.remote.dto

import com.google.gson.annotations.SerializedName

data class RegionStatsDto(
    val country: String,
    val region: String?,
    val cases: Map<String, CaseDto>
)