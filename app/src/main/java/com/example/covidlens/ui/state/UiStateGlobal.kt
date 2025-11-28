package com.example.covidlens.ui.state

import com.example.covidlens.domain.model.RegionCases

sealed class UiStateGlobal {
    object Loading : UiStateGlobal()
    data class Success(val regions: List<RegionCases>) : UiStateGlobal()
    data class Error(val message: String) : UiStateGlobal()
    object Idle : UiStateGlobal()
}