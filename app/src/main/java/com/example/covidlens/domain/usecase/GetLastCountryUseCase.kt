package com.example.covidlens.domain.usecase

import com.example.covidlens.domain.repo.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow

class GetLastCountryUseCase(private val repository: UserPreferencesRepository) {
    operator fun invoke(): Flow<String?> = repository.getLastCountry()
}
