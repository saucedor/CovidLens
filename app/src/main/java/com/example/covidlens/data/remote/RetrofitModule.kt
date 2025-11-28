package com.example.covidlens.data.remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitModule {

    private const val BASE_URL = "https://api.api-ninjas.com/"

    fun provideApi(): CovidApi {
        val apiKeyProvider = ApiKeyProvider()

        val client = OkHttpClient.Builder()
            .addInterceptor(ApiKeyInterceptor(apiKeyProvider))
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CovidApi::class.java)
    }
}
