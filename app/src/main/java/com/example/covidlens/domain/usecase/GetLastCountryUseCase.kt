package com.example.covidlens.domain.usecase

import com.example.covidlens.domain.repo.UserPreferencesRepository

class GetLastCountryUseCase(
    private val prefs: UserPreferencesRepository
) {
    suspend operator fun invoke(): String? = prefs.getLastCountry()
}
