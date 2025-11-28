package com.example.covidlens.ui.state

sealed interface UiState {
    object Idle : UiState
    object Loading : UiState
    data class SuccessCountry(val data: CountryStats) : UiState
    data class SuccessComparison(val data: List<CountryStats>) : UiState
    data class Error(val message: String) : UiState
}
