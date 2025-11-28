package com.example.covidlens.domain.model

data class RegionCases(
    val country: String,
    val region: String?,
    val timeline: List<CaseCount>
)
