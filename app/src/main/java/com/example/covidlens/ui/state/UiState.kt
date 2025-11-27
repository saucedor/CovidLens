package com.example.covidlens.ui.state

import com.example.covidlens.domain.model.CountryStats

sealed interface UiState {
    object Idle : UiState
    object Loading : UiState
    data class SuccessCountry(val data: CountryStats, val simulated: Boolean) : UiState
    data class SuccessComparison(val data: List<CountryStats>, val simulated: Boolean) : UiState
    data class Error(val message: String) : UiState
}
