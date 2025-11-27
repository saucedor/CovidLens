package com.example.covidlens.domain.model

import java.util.Date

data class DayStat(
    val date: String,
    val confirmed: Int?,
    val deaths: Int?,
    val recovered: Int?
)
