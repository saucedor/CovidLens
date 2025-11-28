package com.example.covidlens.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CovidResponseDto(
    val country: String,
    val region: String?,
    val cases: Map<String, CaseDto>
)

data class CaseDto(
    val total: Int?,
    @SerializedName("new")
    val newCases: Int?
)
