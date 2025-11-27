package com.example.covidlens.data

import android.content.Context
import com.example.covidlens.domain.model.CountryStats
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun <T> loadJsonFromAssets(context: Context, fileName: String): T? {
    return withContext(Dispatchers.IO) {
        try {
            val json = context.assets.open(fileName).bufferedReader().use { it.readText() }
            val type = object : TypeToken<T>() {}.type
            Gson().fromJson(json, type)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

object SimulatedLoader {
    suspend fun loadCountry(context: Context, country: String): Result<CountryStats> {
        // In a real app, you would have a more sophisticated way to get the file for a country
        val fileName = "sample_country_stats.json"
        val data: CountryStats? = loadJsonFromAssets(context, fileName)
        return if (data != null) Result.success(data) else Result.failure(Exception("Could not load simulated data"))
    }

    suspend fun loadCountries(context: Context, countries: List<String>): Result<List<CountryStats>> {
        // In a real app, you would have a more sophisticated way to get the files for countries
        val fileName = "sample_country_comparison.json"
        val data: List<CountryStats>? = loadJsonFromAssets(context, fileName)
        return if (data != null) Result.success(data) else Result.failure(Exception("Could not load simulated data"))
    }
}
