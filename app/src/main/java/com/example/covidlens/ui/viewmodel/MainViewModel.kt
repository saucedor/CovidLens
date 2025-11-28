package com.example.covidlens.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.covidlens.domain.usecase.GetCountryStatsUseCase
import com.example.covidlens.domain.usecase.GetLastCountryUseCase
import com.example.covidlens.domain.usecase.GetMultiCountrySnapshotUseCase
import com.example.covidlens.domain.usecase.SaveLastCountryUseCase
import com.example.covidlens.ui.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class MainViewModel(
    app: Application,
    private val getCountryStats: GetCountryStatsUseCase,
    private val getSnapshot: GetMultiCountrySnapshotUseCase,
    private val saveLastCountry: SaveLastCountryUseCase,
    private val getLastCountry: GetLastCountryUseCase
) : AndroidViewModel(app) {

    private val _state = MutableStateFlow<UiState>(UiState.Idle)
    val state: StateFlow<UiState> = _state

    private var lastAction: (() -> Unit)? = null

    fun searchCountry(country: String) {
        val query = country.trim()
        if (query.isBlank()) {
            _state.value = UiState.Error("Please enter a country before searching.")
            return
        }

        fun doSearch() {
            viewModelScope.launch {
                _state.value = UiState.Loading
                try {
                    val result = getCountryStats(query)

                    result.onSuccess { stats ->
                        saveLastCountry(query)
                        _state.value = UiState.SuccessCountry(stats)
                    }.onFailure {
                        _state.value = UiState.Error(it.message ?: "Country not found or network error.")
                    }
                } catch (e: Exception) {
                    _state.value = UiState.Error(e.message ?: "Could not retrieve data. Check your connection.")
                }
            }
        }
        lastAction = ::doSearch
        doSearch()
    }

    fun compareCountries(countries: List<String>) {
        val sanitized = countries.map { it.trim() }.filter { it.isNotBlank() }
        if (sanitized.size < 2) {
            _state.value = UiState.Error("Enter at least two countries to compare.")
            return
        }

        fun doCompare() {
            viewModelScope.launch {
                _state.value = UiState.Loading
                val result = getSnapshot(sanitized)

                result.onSuccess {
                    _state.value = UiState.SuccessComparison(it)
                }.onFailure {
                    _state.value = UiState.Error("Error comparing countries.")
                }
            }
        }
        lastAction = ::doCompare
        doCompare()
    }

    fun retry() {
        lastAction?.invoke()
    }

    fun loadLastCountry(onLoaded: (String?) -> Unit) {
        viewModelScope.launch {
            onLoaded(getLastCountry().firstOrNull())
        }
    }
}
