package com.example.covidlens.ui.model

import com.example.covidlens.domain.model.RegionCases

data class CompareResultEntry(
    val country: String,
    val cases: List<RegionCases>,
    val error: String? = null
)
