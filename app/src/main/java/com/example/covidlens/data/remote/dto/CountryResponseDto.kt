package com.example.covidlens.data.remote.dto

data class CountryResponseDto(
    val country: String?,
    val cases: Map<String, Int?>?,
    val deaths: Map<String, Int?>?,
    val recovered: Map<String, Int?>?
)
