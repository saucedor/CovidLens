package com.example.covidlens.ui.viewmodel

import android.app.Application
import android.content.pm.PackageManager
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
            // --- Manual Dependency Injection ---

            // 1. Read the API key securely from the AndroidManifest metadata
            val ai = app.packageManager.getApplicationInfo(app.packageName, PackageManager.GET_META_DATA)
            val apiKey = ai.metaData["COVID_API_KEY"] as String

            // 2. Create API
            val api = RetrofitModule.create(apiKey)

            // 3. Create Repositories
            val covidRepo = CovidRepositoryImpl(api)
            val userPrefs = UserPrefsDataStore(app)
            val userRepo = UserPreferencesRepositoryImpl(userPrefs)

            // 4. Create UseCases
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
