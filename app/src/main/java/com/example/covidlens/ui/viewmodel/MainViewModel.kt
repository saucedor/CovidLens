package com.example.covidlens.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.covidlens.data.SimulatedLoader
import com.example.covidlens.domain.usecase.*
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

    // Store the last executed action to allow retrying
    private var lastAction: (() -> Unit)? = null

    var selectedDate: String? = null
    var simulatedMode: Boolean = false

    fun searchCountry(country: String) {
        // Define the search action as a local function
        fun doSearch() {
            viewModelScope.launch {
                _state.value = UiState.Loading
                try {
                    val result =
                        if (simulatedMode) SimulatedLoader.loadCountry(getApplication(), country)
                        else getCountryStats(country)

                    result.onSuccess { stats ->
                        saveLastCountry(country)
                        _state.value = UiState.SuccessCountry(stats, simulatedMode)
                    }.onFailure {
                        _state.value = UiState.Error(it.message ?: "País no encontrado o error de red.")
                    }

                } catch (e: Exception) {
                    _state.value = UiState.Error("No se pudieron obtener los datos. Revisa tu conexión.")
                }
            }
        }
        // Store the function reference and execute it
        lastAction = ::doSearch
        doSearch()
    }

    fun compareCountries(countries: List<String>) {
        // Define the compare action as a local function
        fun doCompare() {
            viewModelScope.launch {
                _state.value = UiState.Loading
                val result =
                    if (simulatedMode) SimulatedLoader.loadCountries(getApplication(), countries)
                    else getSnapshot(countries)

                result.onSuccess {
                    _state.value = UiState.SuccessComparison(it, simulatedMode)
                }.onFailure {
                    _state.value = UiState.Error("Error al comparar países.")
                }
            }
        }
        // Store the function reference and execute it
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
