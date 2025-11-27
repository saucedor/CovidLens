package com.example.covidlens.domain.model

data class CountryStats(
    val country: String,
    val timeline: List<DayStat>
)
