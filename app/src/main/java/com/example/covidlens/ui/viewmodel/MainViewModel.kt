package com.example.covidlens.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.covidlens.domain.model.RegionCases
import com.example.covidlens.domain.repo.CovidRepository
import com.example.covidlens.domain.repo.UserPreferencesRepository
import com.example.covidlens.ui.model.CompareResultEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// =========================================
// UI STATES (used in Search + Detail screens)
// =========================================

sealed class UiState {
    object Idle : UiState()
    object Loading : UiState()
    data class Error(val message: String) : UiState()
    data class SuccessCountry(val data: List<RegionCases>) : UiState()
}

sealed class UiStateGlobal {
    object Idle : UiStateGlobal()
    object Loading : UiStateGlobal()
    data class Error(val message: String) : UiStateGlobal()
    data class Success(val data: List<RegionCases>) : UiStateGlobal()
}

// =========================================
// COMPARE STATE (used in CompareScreen)
// =========================================

sealed class CompareState {
    object Idle : CompareState()
    object Loading : CompareState()
    data class Success(val data: List<CompareResultEntry>) : CompareState()
    data class Error(val message: String) : CompareState()
}

// =========================================
// MAIN VIEWMODEL
// =========================================

class MainViewModel(
    private val covidRepo: CovidRepository,
    private val prefsRepo: UserPreferencesRepository
) : ViewModel() {

    // -------- Country search state --------
    private val _state = MutableStateFlow<UiState>(UiState.Idle)
    val state: StateFlow<UiState> = _state

    // -------- Global snapshot state --------
    private val _globalState = MutableStateFlow<UiStateGlobal>(UiStateGlobal.Idle)
    val globalState: StateFlow<UiStateGlobal> = _globalState

    // -------- Compare state --------
    private val _compareState = MutableStateFlow<CompareState>(CompareState.Idle)
    val compareState: StateFlow<CompareState> = _compareState

    // =========================================
    // SEARCH COUNTRY
    // =========================================
    fun searchCountry(country: String) {
        if (country.isBlank()) return

        viewModelScope.launch {
            _state.value = UiState.Loading

            try {
                val result = covidRepo.getCountryStats(country)
                _state.value = UiState.SuccessCountry(result)

                prefsRepo.saveLastCountry(country)

            } catch (e: Exception) {
                _state.value = UiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    // =========================================
    // LOAD LAST COUNTRY
    // =========================================
    fun loadLastCountry(onLoaded: (String?) -> Unit) {
        viewModelScope.launch {
            val saved = prefsRepo.getLastCountry()
            onLoaded(saved)
        }
    }

    // =========================================
    // RETRY
    // =========================================
    fun retry() {
        _state.value = UiState.Idle
    }

    // =========================================
    // LOAD GLOBAL SNAPSHOT
    // =========================================
    fun loadGlobalSnapshot() {
        viewModelScope.launch {
            _globalState.value = UiStateGlobal.Loading

            try {
                val result = covidRepo.getMultipleCountrySnapshot()
                _globalState.value = UiStateGlobal.Success(result)
            } catch (e: Exception) {
                _globalState.value = UiStateGlobal.Error(e.message ?: "Unknown error")
            }
        }
    }

    // =========================================
    // COMPARE COUNTRIES (SAFE VERSION)
// =========================================
    fun compareCountries(countries: List<String>) {
        viewModelScope.launch {
            if (countries.isEmpty()) {
                _compareState.value = CompareState.Error("No countries selected.")
                return@launch
            }

            _compareState.value = CompareState.Loading

            try {
                val result = countries.map { country ->
                    try {

                        val stats = covidRepo.getCountryStats(country)

                        CompareResultEntry(
                            country = country,
                            cases = stats,
                            error = null
                        )

                    } catch (e: Exception) {

                        // Prevent crash, isolate error by country
                        CompareResultEntry(
                            country = country,
                            cases = emptyList(),
                            error = e.message ?: "Failed"
                        )
                    }
                }

                _compareState.value = CompareState.Success(result)

            } catch (e: Exception) {
                _compareState.value = CompareState.Error(e.message ?: "Unknown error")
            }
        }
    }
}
