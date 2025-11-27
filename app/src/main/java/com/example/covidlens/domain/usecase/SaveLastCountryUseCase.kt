package com.example.covidlens.domain.usecase

import com.example.covidlens.domain.repo.UserPreferencesRepository

class SaveLastCountryUseCase(private val repository: UserPreferencesRepository) {
    suspend operator fun invoke(country: String) = repository.saveLastCountry(country)
}
