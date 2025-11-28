package com.example.covidlens.ui.state

import com.example.covidlens.domain.model.RegionCases

sealed class UiState {
    object Idle : UiState()
    object Loading : UiState()
    data class Error(val message: String) : UiState()

    // Resultado de un país específico (puede tener una o varias regiones)
    data class SuccessCountry(val regions: List<RegionCases>) : UiState()

    // Resultado de comparación entre países
    data class SuccessCompare(val data: List<RegionCases>) : UiState()
}
