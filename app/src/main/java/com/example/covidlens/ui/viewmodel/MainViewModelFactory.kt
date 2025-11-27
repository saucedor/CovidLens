package com.example.covidlens.ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.covidlens.data.prefs.UserPrefsDataStore
import com.example.covidlens.data.remote.RetrofitModule
import com.example.covidlens.data.repo.CovidRepositoryImpl
import com.example.covidlens.data.repo.UserPreferencesRepositoryImpl
import com.example.covidlens.domain.usecase.*

class MainViewModelFactory(private val app: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            // Manual Dependency Injection
            val api = RetrofitModule.create("YOUR_API_KEY") // <-- REPLACE WITH YOUR KEY
            val covidRepo = CovidRepositoryImpl(api)
            val userPrefs = UserPrefsDataStore(app)
            val userRepo = UserPreferencesRepositoryImpl(userPrefs)

            val getCountryStats = GetCountryStatsUseCase(covidRepo)
            val getSnapshot = GetMultiCountrySnapshotUseCase(covidRepo)
            val saveLastCountry = SaveLastCountryUseCase(userRepo)
            val getLastCountry = GetLastCountryUseCase(userRepo)

            @Suppress("UNCHECKED_CAST")
            return MainViewModel(app, getCountryStats, getSnapshot, saveLastCountry, getLastCountry) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
