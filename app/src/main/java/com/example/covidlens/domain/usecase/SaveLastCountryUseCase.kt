package com.example.covidlens.domain.usecase

import com.example.covidlens.domain.repo.UserPreferencesRepository

class SaveLastCountryUseCase(
    private val prefs: UserPreferencesRepository
) {
    suspend operator fun invoke(country: String) {
        prefs.saveLastCountry(country)
    }
}
