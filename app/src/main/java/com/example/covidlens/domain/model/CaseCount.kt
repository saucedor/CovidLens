package com.example.covidlens.domain.model

data class CaseCount(
    val date: String,
    val total: Int,
    val new: Int
)